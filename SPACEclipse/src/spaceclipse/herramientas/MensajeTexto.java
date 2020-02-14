package spaceclipse.herramientas;

import spaceclipse.sincronizacion.Mensaje;

@SuppressWarnings("serial")
public class MensajeTexto extends Mensaje {
	private String texto;

	public MensajeTexto(short tipo, String s) {
		super(tipo,s);
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getTexto() {
		return texto;
	}

}
