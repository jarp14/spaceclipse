/*package spaceclipse.herramientas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import spaceclipse.Activator;
import spaceclipse.collab.CUtilities;
import spaceclipse.collab.interfaces.ICollaborativeEditor;
import spaceclipse.ecf.SpaceClient;
import spaceclipse.space.SpacEclipse;
import spaceclipse.space.SpaceEditorInput;

public class EditorSoloPunteros extends EditorPart implements ICollaborativeEditor, IEditor {
	final static String ID = "SPACEclipse.editorCollab";
	boolean isDirty = false;
	String theString = "";

	private SpaceClient cliente;
	protected Composite myParent;
	protected boolean habilitado;
	
	//JGA 05/03/2010 Tabla de telepunteros
	private Hashtable<String, ImageFigure> telepunteros = new Hashtable<String, ImageFigure>();
	
	//JGA 09/03/2010 Coordenadas del ultimo telepuntero que se envio
	private int miX = 99999;
	private int miY = 99999;
	
	public EditorSoloPunteros() {
		CUtilities.setEditor(this);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {}

	@Override
	public void doSaveAs() {}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		this.setSite(site);
		this.setInput(input);
	}

	public void iniciarCliente(SpaceClient cliente) {
		this.cliente = cliente;
	}

	public void openEditor(SpaceEditorInput spaceinput, String usuario) {
		SpaceEditorInput entrada;

		if (spaceinput == null) {
			entrada = new SpaceEditorInput();
		} else {
			entrada = spaceinput;
		}
		IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.openEditor(entrada, ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public String fileToString(String filePath) {
		File file = new File(filePath);
		StringBuffer buffer = new StringBuffer();
		String line = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			while (null != (line = in.readLine())) {
				buffer.append(line+"\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return isDirty;
	}

	@Override
	public void createPartControl(Composite parent) {
		myParent = parent;
		// Se crea el listener de los telepunteros
		final FigureCanvas canvas = new FigureCanvas(myParent);
		//final FigureCanvas canvas = (FigureCanvas) getCanvas();
		canvas.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				// Primero se comprueba que la diferencia con el anterior envio es > 5
				if (Math.abs(e.x-miX) > 5 || Math.abs(e.y-miY) > 5) {
					miX = e.x;
					miY = e.y;

					SpacEclipse app = (SpacEclipse) CUtilities.getAplicacion();

					//JGA 05/03/2010 Hay que pasar a coordenadas absolutas, porque los
					//		demas puede que estan viendo una porcion distinta del diagrama
					Rectangle abs = new Rectangle(e.x, e.y, 0, 0);
					//canvas.getContents().translateToRelative(abs);

					app.notificarTelepuntero(abs.x, abs.y);
				}
			}
		});
		
		// Deshabilitado por defecto
		deshabilitar(); 
	}

	@Override
	public void setFocus() {}

	public String toString() {
		return "Es mi Editor";
	}

	public void salir() {
		cerrar();
		if (cliente != null) {
			cliente.desconectarSesion();
		}
	}

	public void cerrar() {}

	@Override
	public void deshabilitar() {
		habilitado = false;
		myParent.setEnabled(false);
	}

	@Override
	public void habilitar() {
		habilitado = true;
		myParent.setEnabled(true);
	}

	@Override
	public void copiaModelo(char[] archivoModelo, char[] archivoDiagrama, String rutaArchivoModelo,
			String rutaArchivoDiagrama) {
		//JGA 11-02-2010 Primero se habilita si estaba deshabilitado
				//TODO: Mirar si hace falta
				boolean antesHabilitado = true;
				if (!habilitado) {
					habilitar();
					antesHabilitado = false;
				}

				Resource resource = this.getDiagram().eResource();
				IFile iFile = WorkspaceSynchronizer.getFile(resource);
				String archivoDiagramaDestino = iFile.getLocation().toString();
				// Es el mismo nombre quitando "_diagram"
				String archivoModeloDestino = archivoDiagramaDestino.substring(0, archivoDiagramaDestino.length()-8);

				try {
					FileWriter writer = new FileWriter(new File(archivoModeloDestino));
					writer.write(archivoModelo);
					writer.close();

					writer = new FileWriter(new File(archivoDiagramaDestino));
					writer.write(archivoDiagrama);
					writer.close();

					//TODO: Ver si de aqu� al final sobra algo
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					root.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

					EList list = this.getEditingDomain().getResourceSet().getResources();
					for (int i = 0; i < list.size(); i++) {
						Resource res = (Resource) list.get(i);
						res.load(this.getEditingDomain().getResourceSet().getLoadOptions());
					}

					this.getDocumentProvider().synchronize(this.getEditorInput());

					EObject rootPkg = this.getDiagram().getElement();
					List editPolicies = CanonicalEditPolicy.getRegisteredEditPolicies(rootPkg);
					for (Iterator it = editPolicies.iterator(); it.hasNext();) {
						CanonicalEditPolicy nextEditPolicy = (CanonicalEditPolicy) it.next();
						nextEditPolicy.refresh();
					}
				} catch (Exception e) {
					System.err.println("Error al copiar el modelo");
					e.printStackTrace();
				} finally {
					//JGA 11-02-2010 Al final se deshabilita si antes lo estaba
					if (!antesHabilitado) {
						deshabilitar();
					}
				}		
		
	}

	@Override
	public void mueveTelepuntero(String usuario, int x, int y) {
		final Canvas canvas = getCanvas();
		LightweightSystem lws = new LightweightSystem(canvas);
	    IFigure panel = new Figure();
	    lws.setContents(panel);
		//IFigure contents = canvas.getContents();
		ImageFigure figura = telepunteros.get(usuario);
		// En teoria nunca debera ser null
		Rectangle rect = new Rectangle(x, y, 12, 20);
		figura.setBounds(rect);
		if (!panel.getChildren().contains(figura)) {
			panel.add(figura);
		}
	}

	@Override
	public void actualizaTelepunteros(Vector<UsuarioPanel> listaUsuarios) {
		UsuarioPanel usuario = null;
		ImageFigure figura = null;
		// Se crea una tabla nueva para ir pasando aqui los existentes y meter los nuevos.
		// Asi, los viejos no se copiaran. Al final, la nueva sustituye a la vieja.
		Hashtable<String, ImageFigure> telepunterosNuevos = new Hashtable<String, ImageFigure>();
		for (int i = 0; i < listaUsuarios.size(); i++) {
			usuario = (UsuarioPanel) listaUsuarios.elementAt(i);
			figura = telepunteros.get(usuario.getNombre());
			if (figura == null) {
				// Telepuntero nuevo
				int color = usuario.getColor()+1;
				String ruta = "resources/flecha" +color+ ".png";
				ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin("SPACEclipse", ruta);
				figura = new ImageFigure(desc.createImage());
			}
			telepunterosNuevos.put(usuario.getNombre(), figura);
		}
		telepunteros = telepunterosNuevos;
	}
	
	//JGA 10/03/2010 Metodo para obtener el canvas
	private Canvas getCanvas() {
		Control[] controles = myParent.getChildren();
		Canvas control = (Canvas) controles[0];
		return (Canvas) control;
	}

	@Override
	public void copiaFichero(char[] archivoFichero, String rutaArchivo) {
		// TODO Auto-generated method stub
		
	}
}*/