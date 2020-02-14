package spaceclipse.herramientas;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class Colores {
	
	private Colores() {
		throw new IllegalStateException("Utility class");
	}
	
	public static final Color AZUL = new Color(null,new RGB(0,0,255));
	public static final Color MAGENTA = new Color(null,new RGB(255,20,147));
	public static final Color GRIS = new Color(null,new RGB(105,105,105));
	public static final Color ROJO = new Color(null,new RGB(255,0,0));
	public static final Color NARANJA = new Color(null,new RGB(255,70,0));
}
