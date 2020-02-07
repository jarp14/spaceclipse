package spaceclipse.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import spaceclipse.herramientas.ChatEstructuradoSWT;
import spaceclipse.herramientas.PanelPropuestasTurno;
import spaceclipse.herramientas.PanelSesion;

public class PerspectivaComando implements IPerspectiveFactory {

	private IPageLayout factory;

	public PerspectivaComando() {
		super();
	}

	public void createInitialLayout(IPageLayout factory) {
		this.factory = factory;
		addViews();
	}

	private void addViews() {
		IFolderLayout bottom = factory.createFolder("bottom", IPageLayout.BOTTOM, 0.80f, factory.getEditorArea());
		bottom.addView(ChatEstructuradoSWT.ID);
		bottom.addView(IPageLayout.ID_PROP_SHEET);
		
		IFolderLayout topLeft = factory.createFolder("left", IPageLayout.LEFT, 0.15f, factory.getEditorArea());
		IFolderLayout bottomLeft = factory.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.55f, "left");
		topLeft.addView(PanelSesion.ID);
		bottomLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
		
		IFolderLayout topRight = factory.createFolder("right", IPageLayout.RIGHT, 0.85f, factory.getEditorArea());
		IFolderLayout bottomRight = factory.createFolder("bottomRight", IPageLayout.BOTTOM, 0.55f, "right");
		topRight.addView(PanelPropuestasTurno.ID);
		bottomRight.addView(IPageLayout.ID_OUTLINE);
	}
}