package spaceclipse.herramientas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class PanelPropuestasPerspectivas extends PanelPropuestas {

	public final static String ID = "SPACEclipse.perspectivePanel";

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		bTurno.setBounds(new Rectangle(6, 10, 85, 31));
		combo = new Combo(parent, SWT.READ_ONLY);
	    combo.setBounds(6, 50, 110, 65);
	    String ids = parametros.getProperty("idsPerspectives");
	    String items[] = ids.split(", ");
	    combo.setItems(items);
	    combo.select(0); // Para que este seleccionado alguno
		liEdicion.setBounds(new Rectangle(6, 75, 187, 165));

		bTurno.setText(parametros.getProperty("buttonPerspective"));
	}
	
	protected void enviarMensaje(short mensaje) {
		MensajeTurno m;
		m = new MensajeTurno(mensaje, usuario);
		m.setCambio(combo.getText());
		clienteCanal.enviar(m);
		procesarMensajeTurno(m,usuario);
	}
	
	public void bTurno_actionPerformed(SelectionEvent e) {
		enviarMensaje(ConstPanelTurno.TURNO_PEDIR);
	}
	
	public void bDar_selectionPerformed(SelectionEvent e) {
		int index = liEdicion.getSelectionIndex();
		if (index >= 0) {
			String usVot=extraerNombUsuario(((String)liEdicion.getItem(index)));
			String cambio = "";
			cambio = extraerCambio((String)liEdicion.getItem(index));
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
			cambio = extraerCambio((String)liEdicion.getItem(index));
			if (usVot.length() > 0) {
				MensajeTurno mt = new MensajeTurno(ConstPanelTurno.TURNO_NOOK,usuario);
				mt.setCambio(cambio);
				mt.setUsuario(usVot);
				clienteCanal.enviar(mt);
				procesarMensajeTurno(mt,usuario);
			}
		}
	}
	
	protected void editarLeader(String edit, String p){
		//FGG 18/12/2008 Esta etiqueta hay q ponerla para el awareness
		lLeader.setText(edit);
		coordinador.hacerCambios(p);
	}

	//JGA 11/02/2010 El nombre del canal se lee desde un metodo para que sea abstracto
	protected String getCanalLocal() {
		return "perspectiva";
	}
	
}
