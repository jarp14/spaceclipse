package spaceclipse.herramientas;

import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;

import spaceclipse.ecf.SpaceClient;

public class CoordinarPerspectivas implements ICoordinacion {
	private ViewPart vista;
	private String usuario;
	private SpaceClient cliente;

	public CoordinarPerspectivas(ViewPart v,SpaceClient c,String u) {
		vista = v;
		usuario = u;
		cliente = c;
	}

	@Override
	public void deshacerCambios() {
		MensajeEstado m = new MensajeEstado(ConstPanelTurno.ESTADO_GLOBAL,usuario);
		m.setUsuarioEstado(usuario);
		m.setBorrarOtros(true);
		cliente.enviarDatos(m);
	}

	@Override
	public void hacerCambios(String p) {
		MensajeEstado m = new MensajeEstado(ConstPanelTurno.ESTADO_GLOBAL,usuario);
		m.setUsuarioEstado(usuario);
		m.setEstado(p); 
		m.setBorrarOtros(true);
		cliente.enviarDatos(m);
		try {
			if (p.equals("Modeling")) {
				openPerspective("SPACEclipse.perspectivaNueva", vista.getSite().getWorkbenchWindow());			
			} else {
				openPerspective(p, vista.getSite().getWorkbenchWindow());
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void openPerspective(final String perspectiveId, final IWorkbenchWindow activeWorkbenchWindow) throws ExecutionException {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		IPerspectiveDescriptor desc = activeWorkbenchWindow.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(perspectiveId);
		if (desc == null) {
			System.out.println("ERROR: No encuentra la perspectiva ["+perspectiveId+"]");
		}
		/*try {
			if (activePage != null) {
				activePage.setPerspective(desc);
			} else {
				IAdaptable input = ((Workbench) workbench)
						.getDefaultPageInput();
				activeWorkbenchWindow.openPage(perspectiveId, input);
			}
		} catch (WorkbenchException e) {
			throw new ExecutionException("Perspective could not be opened.", e); //$NON-NLS-1$
		}*/
	}

}