package spaceclipse.herramientas;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class PanelPropuestasTurno extends PanelPropuestas {
	
	public final static String ID = "SPACEclipse.view1";

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		bTurno.setBounds(new Rectangle(6, 10, 100, 31));
	    liEdicion.setBounds(new Rectangle(5, 50, 187, 182));
		bTurno.setText(parametros.getProperty("buttonTurno"));
	}
	
	protected void enviarMensaje(short mensaje) {
		MensajeTurno m;
		m = new MensajeTurno(mensaje,usuario);
		clienteCanal.enviar(m);
		procesarMensajeTurno(m,usuario);
	}
	
	public void bTurno_actionPerformed(SelectionEvent e) {
		enviarMensaje(ConstPanelTurno.TURNO_PEDIR);
	}
	
	public void bDar_selectionPerformed(SelectionEvent e) {
		int index = liEdicion.getSelectionIndex();
		if (index >= 0) {
			String usVot = extraerNombUsuario(((String)liEdicion.getItem(index)));
			String cambio = "";
			if (usVot.length() > 0) {
				MensajeTurno mt = new MensajeTurno(ConstPanelTurno.TURNO_OK,usuario);
				mt.setCambio(cambio);
				mt.setUsuario(usVot);
				clienteCanal.enviar(mt);
				procesarMensajeTurno(mt,usuario);
			}
		}
	}

	public void bNoDar_selectionPerformed(SelectionEvent e) {
		int index = liEdicion.getSelectionIndex();
		if (index >= 0) {
			String usVot = extraerNombUsuario(((String)liEdicion.getItem(index)));
			String cambio = "";
			if (usVot.length() > 0) {
				MensajeTurno mt = new MensajeTurno(ConstPanelTurno.TURNO_NOOK, usuario);
				mt.setCambio(cambio);
				mt.setUsuario(usVot);
				clienteCanal.enviar(mt);
				procesarMensajeTurno(mt, usuario);
			}
		}
	}
	
	protected void editarLeader(String edit, String p) {
		//FGG 18/12/2008 Esta etiqueta hay que ponerla para el awareness
		lLeader.setText(edit);
		if (edit.equals(usuario)) {
			coordinador.hacerCambios("");
		} else {
			coordinador.deshacerCambios();
		}
	}

	//JGA 11/02/2010 El nombre del canal se lee desde un metodo para que sea abstracto
	protected String getCanalLocal() {
		return "turno";
	}

}
