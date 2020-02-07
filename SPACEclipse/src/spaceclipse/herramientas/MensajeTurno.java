package spaceclipse.herramientas;

import spaceclipse.sincronizacion.Mensaje;

public class MensajeTurno extends Mensaje {
	private String usuario = "";
	private String cambio = "";

	public MensajeTurno(short tipo, String s) {
		super(tipo,s);
	}
	 
	public void setCambio(String c) {
		cambio = c;
	}
	 
	public String getCambio() {
		return cambio;
	}
	  
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getUsuario() {
		return usuario;
	}
	
}