package spaceclipse.herramientas;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class SemaforoProp {
	private Composite semaforo;
	private Vector procesos = new Vector(2,2);

	public SemaforoProp(Composite c) {
		semaforo = new Composite(c,SWT.NONE);
		semaforo.setSize(16,21);
		apagar();
	}

	public void insertarProceso(String usuario) {
		if (!procesos.contains(usuario))
			procesos.addElement(usuario);
		encender();
	}

	public void eliminarProceso(String usuario) {
		procesos.removeElement(usuario);
		if (procesos.size() == 0)
			apagar();
	}
	
	public void setBounds(Rectangle r) {
		semaforo.setBounds(r);
	}
	
	private void apagar() {
		semaforo.setBackground(new Color(null,new RGB(255,0,0)));
	}

	private void encender() {
		semaforo.setBackground(new Color(null,new RGB(0,255,0)));
	}
  
	public boolean estaApagado() {
		return procesos.size() == 0;
	}
	
}
