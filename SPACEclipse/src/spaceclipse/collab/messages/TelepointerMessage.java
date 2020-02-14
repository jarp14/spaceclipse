package spaceclipse.collab.messages;

import java.io.Serializable;

import spaceclipse.sincronizacion.Mensaje;

@SuppressWarnings("serial")
public class TelepointerMessage extends Mensaje implements Serializable {
	protected int x;
	protected int y;
	
	public TelepointerMessage(short tipo, String sender) {
		super(tipo, sender);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}