package spaceclipse.herramientas;

import spaceclipse.sincronizacion.Mensaje;

public class MensajeColorUsuarios extends Mensaje {
	private String usuarios[];
	private byte colores[];

	public MensajeColorUsuarios(short tipo,String s) {
		super(tipo,s);
		usuarios = null;
		colores = null;
	}

	public void setUsuarios(String usuarios[]) {
		this.usuarios = new String[usuarios.length];
		for (int i=0; i<usuarios.length; i++)
			this.usuarios[i] = usuarios[i];
	}

	public void setColores(byte colores[]) {
		this.colores = new byte[colores.length];
		for (int i=0; i<colores.length; i++)
			this.colores[i] = colores[i];
	}

	public String[] getUsuarios() { return usuarios; }
	public byte[] getColores() { return colores; }

}
