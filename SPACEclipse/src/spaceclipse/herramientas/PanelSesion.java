package spaceclipse.herramientas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import spaceclipse.collab.CUtilities;
import spaceclipse.collab.interfaces.ICollaborativeEditor;
import spaceclipse.ecf.SpaceClient;
import spaceclipse.ecf.SpaceClienteCanal;
import spaceclipse.sincronizacion.ICliente;
import spaceclipse.sincronizacion.Mensaje;
import spaceclipse.util.Agente;
import spaceclipse.util.Util;

public class PanelSesion extends ViewPart implements ICliente {
	public static final String ID = "SPACEclipse.panelSesion";

	private int alto;
	private int ancho;
	private boolean grande=true;
	private boolean vertical=true;
	private int wFoto; 
	private int hFoto;
	private Vector<UsuarioPanel> tabUsuarios;
	private Color colores[] = {Colores.AZUL, Colores.MAGENTA, Colores.GRIS, Colores.ROJO, Colores.NARANJA};
	private boolean asigColores[] = {false, false, false, false, false};
	private boolean pitar = true;

	private static final String CANAL_LOCAL = "panel";
	private SpaceClienteCanal clienteCanal = null;
	private String usuario;
	private int usuarios;

	private Canvas canvas;
	private Image im;

	public void iniciarConexion(SpaceClient cliente, String usuario) {
		this.clienteCanal = new SpaceClienteCanal(this, cliente, CANAL_LOCAL);
		this.usuario = usuario;

		usuarios = clienteCanal.getParticipantes();

		if (usuarios == 1) { // Es el primero
			byte colorAsig = obtenerColor();
			UsuarioPanel usPan = crearUsuario(usuario, colorAsig);
			tabUsuarios.add(usPan);
		} else { // Comunicar entrada a los demas para que actualicen
			Mensaje m = new Mensaje(ConstPanel.MSJ_PANEL_ENTRAR, usuario);
			clienteCanal.enviar(m);
		}
		canvas.redraw();

	}
	
	public void enviarConfirmacion() {
		Mensaje m = new Mensaje(ConstPanel.MSJ_PANEL_CONFIRMACION, usuario);
		clienteCanal.enviar(m);
	}

	@Override
	public void createPartControl(Composite parent) {
		if (grande && vertical) {
			ancho = 100;
			alto = 250;
		} else if (grande && !vertical) {
			ancho = 175;
			alto = 80;
		} else if (!grande && vertical) {
			ancho = 28;
			alto = 240;
		} else if (!grande && !vertical) {
			ancho = 150;
			alto = 50;
		}
		wFoto = (grande ? 48:24);
		hFoto = (grande ? 48:36);

		tabUsuarios = new Vector<UsuarioPanel>();

		canvas = new Canvas(parent, SWT.NONE);
		canvas.setBounds(parent.getClientArea());
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				int i;
				UsuarioPanel usPan;
				Color color;

				for (i=0; i<tabUsuarios.size(); i++) {
					usPan = tabUsuarios.get(i);
					// Buscar color del usuario
					if (usPan != null) {
						if (usPan.getColor() == -1) {
							color = new Color(null,new RGB(0,0,0));
						} else {
							color = colores[usPan.getColor()];
						}
						e.gc.setForeground(color);
						
						// Dibuja nombre...
						if (!grande) { // Panel pequeño (nombre debajo de la foto)
							// Si no es grande, no se dibujan las etiquetas de los dos estados
							if (vertical) {
								e.gc.drawString(usPan.getNombre(), 2, alto/5*i+2+hFoto);
							} else {
								e.gc.drawString(usPan.getNombre(), (ancho/5)*i+2, 2+hFoto);
							}
						} else { // Panel grande (nombre y estado a la derecha de la foto)
							if (vertical) {
								e.gc.drawString(usPan.getNombre(), 1+wFoto+2, (alto/5)*i+2+4);
							} else {
								e.gc.drawString(usPan.getNombre(), (ancho/5)*i+2, 2+hFoto+4);
							}
							
							// Estado Global
							String estadoglobal = usPan.getEstadoGlobal(); 
							// estadoglobal = "Use Cases"; // Para probar
							if (estadoglobal != null) {
								if (vertical) {
									e.gc.drawString(estadoglobal, 1+wFoto+2, (alto/5)*i+2+20);
								} else {
									e.gc.drawString(estadoglobal, (ancho/5)*i+2, 2+hFoto+20);
								}
							}
							
							// Estado particular
							String estado = usPan.getEstado();
							//if (i==0)
							//	estado = "Editing"; // Para probar
							if (estado != null) {
								if (vertical) {
									e.gc.drawString(estado, 1+wFoto+2, (alto/5)*i+2+34);
								} else {
									e.gc.drawString(estado, (ancho/5)*i+2, 2+hFoto+34);
								}
							}

						}
						
						// Foto
						im = usPan.getFoto();
						if (im != null){
							e.gc.drawImage(new Image(Display.getCurrent(), im.getImageData().scaledTo(wFoto,hFoto)), (vertical?2:(ancho/5)*i+1), (vertical?(alto/5)*i+2:1));
						} else {
							System.out.println("NO COGE LA FOTO");
						}
						// Marco del usuario actual
						if (usuario.equals(usPan.getNombre())) {
							if (!vertical) { // Panel horizontal
								e.gc.drawRectangle((ancho/5)*i+1, 1, ancho/5-1, alto-3);
							} else { // Panel vertical
								e.gc.drawRectangle(1, (alto/5)*i+1, ancho-3, alto/5-1);
							}
						}
					} 
				}
			}
		});

		getSite().getPage().addPartListener(new IPartListener2() {
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {}
			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {}
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				salir();
			}
			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {}
			@Override
			public void partHidden(IWorkbenchPartReference partRef) {}
			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {}
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {}
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {}

		});	    
	}    

	public void salir() {
		cerrar();
	}

	@Override
	public void setFocus() {}

	public void asignarColorUsuario(String nombre) {
		int i;
		boolean enc = false;
		boolean primeroLista = false;
		byte colorAsig = -1;
		UsuarioPanel usPan;

		// Soy el primero de la lista con color asignado, soy el que asigna
		for (i=0; i<tabUsuarios.size() && !enc && !primeroLista; i++) {
			usPan = tabUsuarios.get(i);
			if (usPan != null) {
				primeroLista = true;
				if (usPan.getNombre().equals(usuario))
					enc = true;
			}
		}
		
		if (primeroLista && enc) {
			// Construir las estructuras de datos para enviar los colores
			String us[] = new String[tabUsuarios.size()+1];
			byte col[] = new byte[tabUsuarios.size()+1];
			for (i=0; i<tabUsuarios.size(); i++) {
				usPan = tabUsuarios.get(i);
				us[i] = usPan.getNombre();
				col[i] = usPan.getColor();
			}

			// Para el nuevo usuario
			colorAsig = obtenerColor();
			usPan = crearUsuario(nombre, colorAsig);
			us[tabUsuarios.size()] = usPan.getNombre();
			col[tabUsuarios.size()] = usPan.getColor();
			tabUsuarios.add(usPan);

			// Enviar los colores
			MensajeColorUsuarios mcu = new MensajeColorUsuarios(ConstPanel.MSJ_PANEL_ACT_COLOR, usuario);
			mcu.setUsuarios(us);
			mcu.setColores(col);
			clienteCanal.enviar(mcu);
			
			canvas.redraw();
		}
	}

	private byte obtenerColor() {
		byte colorAsig = -1;
		boolean enc = false;

		for (int j=0; j<colores.length && !enc; j++) {
			if (!asigColores[j]) {
				enc = true;
				asigColores[j] = true;
				colorAsig = (byte)j;
			}
		}
		
		return colorAsig;
	}

	public void procesarMensaje(Mensaje m) {
		String quienEnvia = m.getSender();
		try {
			switch (m.getTipo()) {
				case ConstPanel.MSJ_PANEL_ENTRAR:
					// Alguien entra
					// Un unico usuario calcula el color del nuevo usuario y lo comunica a todos
					asignarColorUsuario(m.getSender());
					break;
				case ConstPanel.MSJ_PANEL_ACT_COLOR:
					actualizar((MensajeColorUsuarios)m);
					if (pitar && !quienEnvia.equals(usuario))
						Util.pitido();
					break;
				case ConstPanel.MSJ_PANEL_SALIR:
					quitarUsuario(((MensajePanel)m).getUsuario());
					if (pitar && !quienEnvia.equals(usuario))
						Util.pitido();
					break;
				case ConstPanel.MSJ_ESTADO_USUARIO: {
					MensajeEstado me = (MensajeEstado)m;
					procesarEstado(me.getUsuarioEstado(), me.getEstado(), me.getBorrarOtros());
					break;
				}
				case ConstPanel.MSJ_ESTADOGLOBAL_USUARIO: {
					MensajeEstado me = (MensajeEstado)m;
					//procesarEstadoGlobalTodos(me.getEstado());
					procesarEstadoGlobal(me.getUsuarioEstado(), me.getEstado(), me.getBorrarOtros());
					break;
				}
				case ConstPanel.MSJ_ESTADO_TODOS: {
					MensajeEstadoTodos met=(MensajeEstadoTodos)m;
					procesarEstadoTodos(met.getEstado());
					break;
				}
				case ConstPanel.MSJ_PANEL_INICIO: {
					enviarConfirmacion();
					break;
				}
				case ConstPanel.MSJ_PANEL_CONFIRMACION: {
					usuarios++;
					break;
				}
				default:
					break;
			}
		} catch(Exception e) {
			System.err.println("Error Panel (recibir datos): "+e.toString());
			e.printStackTrace();
		}
	}
	
	// actualiza la tabla hash de usuarios para reflejar los usuarios de la sesion,
	// incluyendo los colores, calculados por el emisor
	public void actualizar(MensajeColorUsuarios mcu) {
		UsuarioPanel usPan;
		//String nombreUs;
		byte colorAsig = -1;
		String us[];
		byte col[];

		us = mcu.getUsuarios();
		col = mcu.getColores();

		tabUsuarios.removeAllElements();
		for (int i=0; i<us.length; i++){
			usPan = crearUsuario(us[i],col[i]);
			tabUsuarios.add(usPan);
		}
		canvas.redraw();

	// TODO: Quitar esto de aqui.
	// JGA 09/03/2010 Se actualizan los telepunteros
    	final ICollaborativeEditor editorTP2 = CUtilities.getEditor();
		((WorkbenchPart)editorTP2).getSite().getShell().getDisplay().syncExec(
				new Runnable() {
					public void run() {
						editorTP2.actualizaTelepunteros(getTablaUsuarios());
					}
				});
	}

	private void quitarUsuario(String usuario) {
		int i;
		boolean enc = false;
		UsuarioPanel usPan = null;
		UsuarioPanel usBorr = null;
		
		for (i=0; i<tabUsuarios.size() && !enc; i++) {
			usPan = tabUsuarios.get(i);
			if(usPan.getNombre().equals(usuario)){
				enc = true;
				usBorr = usPan;
			}
		}
		
		if (enc) {
			tabUsuarios.remove(usBorr);
		}

		// Dejar libre el color del que se ha marchado (repasando la tabla hash)
		for (i=0; i<asigColores.length; i++)
			asigColores[i] = false;
		for (i=0; i<tabUsuarios.size(); i++) {
			usPan = tabUsuarios.get(i);
			if (usPan != null)
				asigColores[usPan.getColor()] = true;
		}
		canvas.redraw();
	}
	
	public UsuarioPanel crearUsuario(String nombUsuario, byte color) {
		UsuarioPanel usuario;
		String sql = null;
		String urlFoto = null;
		PreparedStatement ps;
		ResultSet rs;
		//int i;
		//boolean enc = false;
		//byte colorAsig = -1;

		// Buscar foto
		try {
			sql = "select foto from usuarios where id=?";
			ps = Agente.getBD().prepSentencia(sql);
			ps.setString(1, nombUsuario);
			rs = Agente.getBD().ejecutarSelect(ps);
			if (rs.next())
				urlFoto = rs.getString("foto");
			Agente.getBD().cerrarSentencia(rs, ps);
		} catch(Exception e) {
			System.err.println("Error Panel (crear usuario): "+e.toString());
		}
		System.out.println("RUTA: "+urlFoto);
		// Crear usuario
		usuario = new UsuarioPanel(urlFoto, color, this, nombUsuario);

		return usuario;
	}

	public void asignarEstado(String usuarioEstado, String estado, boolean borrarOtros) {
		// Comunicar estado a los demas para que actualicen
		MensajeEstado me = new MensajeEstado(ConstPanel.MSJ_ESTADO_USUARIO, usuario);
		me.setUsuarioEstado(usuarioEstado);
		me.setEstado(estado);
		me.setBorrarOtros(borrarOtros);
		clienteCanal.enviar(me);
		// Procesar
		procesarEstado(usuarioEstado, estado, borrarOtros);
	}
	
	// Igual que el anterior pero para la etiqueta del estado Global
	public void asignarEstadoGlobal(String usuarioEstado, String estado, boolean borrarOtros) {
		// Comunicar estado a los demas para que actualicen
		MensajeEstado me = new MensajeEstado(ConstPanel.MSJ_ESTADOGLOBAL_USUARIO, usuario);
		me.setUsuarioEstado(usuarioEstado);
		me.setEstado(estado);
		me.setBorrarOtros(borrarOtros);
		clienteCanal.enviar(me);
		// Procesar
		procesarEstadoGlobal(usuarioEstado, estado, borrarOtros);
	}

	private void procesarEstado(String usuarioEstado, String estado, boolean borrarOtros) {
		UsuarioPanel usPan;

		for (int i=0; i<tabUsuarios.size(); i++) {
			usPan = tabUsuarios.get(i);
			if (usPan != null) {
				if ((usPan.getNombre()).equals(usuarioEstado)) {
					usPan.setEstado(estado);
				} else {
					if (borrarOtros)
						usPan.setEstado("");
				}
			}
		}
		canvas.redraw();
	}
	
	private void procesarEstadoGlobal(String usuarioEstado, String estado, boolean borrarOtros) {
		UsuarioPanel usPan;

		for (int i=0; i<tabUsuarios.size(); i++) {
			usPan = tabUsuarios.get(i);
			if (usPan != null) {
				if ((usPan.getNombre()).equals(usuarioEstado)) {
					usPan.setEstadoGlobal(estado);
				} else {
					if(borrarOtros)
						usPan.setEstadoGlobal("");
				}
			}
		}
		canvas.redraw();
	}

	public void asignarEstadoTodos(String estado) {
		// Comunicar estado a los demas para que actualicen
		MensajeEstadoTodos met = new MensajeEstadoTodos(ConstPanel.MSJ_ESTADO_TODOS, usuario);
		met.setEstado(estado);
		clienteCanal.enviar(met);
		// Procesar
		procesarEstadoTodos(estado);
	}

	private void procesarEstadoTodos(String estado) {
		UsuarioPanel usPan;

		for (int i=0; i<tabUsuarios.size(); i++) {
			usPan = tabUsuarios.get(i);
			if (usPan != null)
				usPan.setEstado(estado);
		}
		canvas.redraw();
	}
	
	private void procesarEstadoGlobalTodos(String estado) {
		UsuarioPanel usPan;

		for(int i=0; i<tabUsuarios.size(); i++) {
			usPan = tabUsuarios.get(i);
			if (usPan != null)
				usPan.setEstadoGlobal(estado);

		}
		canvas.redraw();
	}

	public void cerrar() { // Desconectar
		if (clienteCanal != null) { // Comunicar salida a los demas para que eliminen al usuario
			MensajePanel m = new MensajePanel(ConstPanel.MSJ_PANEL_SALIR, usuario);
			m.setUsuario(usuario);
			clienteCanal.enviar(m);
			clienteCanal.desconectarCanal();
		}
	}

	public void setPitidoAwareness(boolean pitido) {
		pitar = pitido;
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
					procesarMensaje(m);
				}

			});
			//procesarMensaje(m,usuario);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean isGrande() {
		return grande;
	}
	public void setGrande(boolean grande) {
		this.grande = grande;
	}
	public boolean isVertical() {
		return vertical;
	}
	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}

	public Vector<UsuarioPanel> getTablaUsuarios() {
		return tabUsuarios;
	}
}