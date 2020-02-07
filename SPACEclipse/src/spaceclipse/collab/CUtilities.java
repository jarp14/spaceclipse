package spaceclipse.collab;

import spaceclipse.collab.interfaces.ICollaborativeEditor;
import spaceclipse.herramientas.IAplicacion;

public class CUtilities {
	
	static ICollaborativeEditor editor = null;
	static IAplicacion aplicacion = null;

	public static ICollaborativeEditor getEditor() {
		return editor;
	}

	public static void setEditor(ICollaborativeEditor editor) {
		CUtilities.editor = editor;
	}

	public static IAplicacion getAplicacion() {
		return aplicacion;
	}

	public static void setAplicacion(IAplicacion aplicacion) {
		CUtilities.aplicacion = aplicacion;
	}
	
}