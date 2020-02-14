package spaceclipse.herramientas;

import spaceclipse.sincronizacion.Mensaje;

@SuppressWarnings("serial")
public class MensajeEstadoTodos extends Mensaje {
	String estado;

	public MensajeEstadoTodos(short tipo, String s) {
		super(tipo,s);
	}

	public void setEstado(String e) { estado = e; }

	public String getEstado() { return estado; }

}
