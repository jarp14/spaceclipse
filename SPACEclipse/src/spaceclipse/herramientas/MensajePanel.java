package spaceclipse.herramientas;

import spaceclipse.sincronizacion.Mensaje;

@SuppressWarnings("serial")
public class MensajePanel extends Mensaje {
	private String usuario;

	MensajePanel(short tipo, String s) {
		super(tipo,s);
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getUsuario() {
		return usuario;
	}

}
