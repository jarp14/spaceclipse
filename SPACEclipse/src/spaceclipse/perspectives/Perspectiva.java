package spaceclipse.perspectives;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import spaceclipse.Activator;
import spaceclipse.herramientas.ChatEstructuradoSWT;
import spaceclipse.herramientas.PanelSesion;
import spaceclipse.herramientas.CoordinarTurno;
import spaceclipse.herramientas.PanelPropuestasTurno;

import spaceclipse.space.SpacEclipse;

public class Perspectiva {

	public static void inicializaVistas(SpacEclipse app) {
		IViewPart vp1 = null;
		IViewPart vp2 = null;
		IViewPart vp3 = null;

		IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			vp1 = page.showView(PanelSesion.ID);
			vp2 = page.showView(ChatEstructuradoSWT.ID);
			vp3 = page.showView(PanelPropuestasTurno.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

		if (vp1 instanceof PanelSesion) {
			app.setPanelSesion((PanelSesion) vp1);
			app.getPanelSesion().iniciarConexion(app.getCliente(), app.getUsuario());
			app.getPanelSesion().setGrande(true);
			app.getPanelSesion().setVertical(true);
		} else {
			System.out.println("Ha ocurrido un error extraño con la vista del Panel de Sesion");
		}

		if (vp2 instanceof ChatEstructuradoSWT) {
			app.setChat((ChatEstructuradoSWT) vp2);
			app.getChat().iniciarConexion(app.getCliente(), app.getUsuario());
			app.getChat().setGrande(true);
		} else {
			System.out.println("Ha ocurrido un error extraño con la vista del Chat");
		}

		if (vp3 instanceof PanelPropuestasTurno) {
			app.setPanelturno((PanelPropuestasTurno) vp3);
			app.getPanelturno().iniciarConexion(app.getCliente(), app.getUsuario(), 
					new CoordinarTurno(app.getEditor(), app.getCliente(), app.getUsuario()));
		} else {
			System.out.println("Ha ocurrido un error extraño con la vista del Panel de Turno");
		}
		
		try {
			PlatformUI.getWorkbench().showPerspective("SPACEclipse.perspectiva", page.getWorkbenchWindow());
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().resetPerspective();
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}	
}