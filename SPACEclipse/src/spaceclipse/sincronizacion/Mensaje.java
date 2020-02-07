package spaceclipse.sincronizacion;

public class Mensaje implements java.io.Serializable {
	private short tipo;	
	private String sender;

	public Mensaje(short tipo, String sender) {
		this.tipo = tipo;
		this.sender = sender;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String s) {
		sender = s;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(short t) {
		tipo = t;
	}
	
}