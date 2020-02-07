package spaceclipse.space;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.FileEditorInput;

import spaceclipse.Activator;
import spaceclipse.collab.CUtilities;
import spaceclipse.ecf.SpaceClient;
import spaceclipse.collab.messages.TelepointerMessage;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import spaceclipse.collab.messages.ModelMessage;

import spaceclipse.herramientas.ChatEstructuradoSWT;
import spaceclipse.collab.interfaces.ICollaborativeEditor;
import spaceclipse.collab.messages.FileMessage;
import spaceclipse.herramientas.IEditor;
import spaceclipse.herramientas.PanelPropuestas;
import spaceclipse.herramientas.PanelSesion;
import spaceclipse.herramientas.ConstPanelTurno;
import spaceclipse.herramientas.IAplicacion;
import spaceclipse.herramientas.MensajeEstado;
import spaceclipse.mensajes.MSNuevoUsuario;
import spaceclipse.perspectives.Perspectiva;
import spaceclipse.sincronizacion.FConectarSWT;
import spaceclipse.sincronizacion.ICliente;
import spaceclipse.sincronizacion.Mensaje;
import spaceclipse.util.ConsMensajes;

public class SpacEclipse implements IAplicacion, ICliente {
	public final static short MENSAJE_MODELO = 1001;
	public final static short MENSAJE_TELEPUNTERO = 2001;
	
	private static String canalAplic = "space";
	private SpaceClient cliente;
	private String usuario;

	private PanelSesion panelsesion = null;
	private ChatEstructuradoSWT chat = null;
	private ICollaborativeEditor editor = null;
	private PanelPropuestas panelturno = null;
	

	public SpacEclipse() {}
	
	@Override
	public void iniciarAplicacion(String host, String sesion, String usuario, String tipo, String fichero, boolean esAdmin) {
		try {
			setCliente(new SpaceClient(this,host,sesion,tipo,canalAplic,usuario));
			getCliente().createAndConnect();
			this.setUsuario(usuario);
			
			CUtilities.setAplicacion(this);
			
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fichero));
			// Se guarda el editor para luego acceder a el desde IniciaPerspectiva
			editor = (ICollaborativeEditor) Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow()
					.getActivePage()
					.openEditor(new FileEditorInput(file), "lciat.diagram.part.LciatDiagramEditorID");
			Perspectiva.inicializaVistas(this);

			MSNuevoUsuario ms = new MSNuevoUsuario(usuario);
			getCliente().enviarDatos(ms);
		} catch(Exception e) {
			System.out.println("Excepcion de ECF en SPACEclipse");
			e.printStackTrace();
		}
	}

	public void iniciar() {
		FConectarSWT FConectar = new FConectarSWT(new Shell(),SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,this,"","EN");
		try {
			FConectar.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void procesaMensaje(Mensaje m, String quienEnvia) {
		switch (m.getTipo()) {
		case ConstPanelTurno.ESTADO_EDICION:
			MensajeEstado mEstado = (MensajeEstado) m;
			if (getPanelSesion() != null)
				getPanelSesion().asignarEstado(mEstado.getUsuarioEstado(),mEstado.getEstado(),mEstado.getBorrarOtros());
			break;
		case ConstPanelTurno.ESTADO_GLOBAL:
			MensajeEstado mEstadoG = (MensajeEstado) m;
			if (getPanelSesion() != null)
				getPanelSesion().asignarEstadoGlobal(mEstadoG.getUsuarioEstado(),mEstadoG.getEstado(),mEstadoG.getBorrarOtros());
			break;
		case ConsMensajes.MS_NUEVO_USUARIO :
			final ICollaborativeEditor editorTP2 = CUtilities.getEditor();
			((GraphicalEditor) editorTP2).getSite().getShell().getDisplay()
					.syncExec(new Runnable() {
						public void run() {
							editorTP2.actualizaTelepunteros(getPanelSesion().getTablaUsuarios());
						}
					});
			break;
		case SpacEclipse.MENSAJE_MODELO :
			final ModelMessage mensaje = (ModelMessage) m;
			final ICollaborativeEditor editor = CUtilities.getEditor();
			((GraphicalEditor) editor).getSite().getShell().getDisplay()
					.syncExec(new Runnable() {
						public void run() {
							editor.copiaModelo(mensaje.getArchivoModelo(), 
									mensaje.getArchivoDiagrama(), 
									mensaje.getRutaArchivoModelo(),
									mensaje.getRutaArchivoDiagrama());
						}
					});
			break;
		case SpacEclipse.MENSAJE_TELEPUNTERO :
			final TelepointerMessage mensajeTelepuntero = (TelepointerMessage) m;
			final ICollaborativeEditor editorTP = CUtilities.getEditor();
			((GraphicalEditor) editorTP).getSite().getShell().getDisplay().syncExec(new Runnable() {
				public void run() {
					editorTP.mueveTelepuntero(mensajeTelepuntero.getSender(),
							mensajeTelepuntero.getX(),
							mensajeTelepuntero.getY());
				}
			});
			break;
		}
	}
	
	@Override
	public void recibirDatos(byte[] message) {
		ByteArrayInputStream bins = new ByteArrayInputStream(message);
		try {
			ObjectInputStream ois = new ObjectInputStream(bins);
			final Mensaje m = (Mensaje) ois.readObject();
			Display.getDefault().asyncExec(new Runnable(){
				@Override
				public void run() {
					procesaMensaje(m,m.getSender());
				}

			});
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void notificarTelepuntero(int x, int y) {
		try {
			TelepointerMessage mensaje = new TelepointerMessage(SpacEclipse.MENSAJE_TELEPUNTERO, getUsuario());
			mensaje.setX(x);
			mensaje.setY(y);
			cliente.enviarDatos(mensaje);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void notificarModelo(char[] modelo, char[] diagrama, String rutaModelo, String rutaDiagrama) {
		try {
			ModelMessage mensaje = new ModelMessage(SpacEclipse.MENSAJE_MODELO, usuario);
			mensaje.setArchivoModelo(modelo);
			mensaje.setArchivoDiagrama(diagrama);
			mensaje.setRutaArchivoModelo(rutaModelo);
			mensaje.setRutaArchivoDiagrama(rutaDiagrama);
			cliente.enviarDatos(mensaje);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PanelSesion getPanelSesion() {
		return panelsesion;
	}

	public void setPanelSesion(PanelSesion panelsesion) {
		this.panelsesion = panelsesion;
	}
	public SpaceClient getCliente() {
		return cliente;
	}

	public void setCliente(SpaceClient cliente) {
		this.cliente = cliente;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public ChatEstructuradoSWT getChat() {
		return chat;
	}

	public void setChat(ChatEstructuradoSWT chat) {
		this.chat = chat;
	}
	public PanelPropuestas getPanelturno() {
		return panelturno;
	}

	public void setPanelturno(PanelPropuestas panelturno) {
		this.panelturno = panelturno;
	}
	public IEditor getEditor() {
		return editor;
	}
}