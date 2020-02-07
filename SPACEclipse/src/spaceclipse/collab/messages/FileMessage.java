package spaceclipse.collab.messages;

import java.io.Serializable;

import spaceclipse.sincronizacion.Mensaje;

public class FileMessage extends Mensaje implements Serializable {

	char[] fichero;
	String rutaFichero;

	public FileMessage(short tipo, String sender) {
		super(tipo, sender);
	}


	public String getRutaFichero() {
		return rutaFichero;
	}

	public void setRutaFichero(String rutaFichero) {
		this.rutaFichero = rutaFichero;
	}

	public char[] getFichero() {
		return fichero;
	}

	public void setFichero(char[] fichero) {
		this.fichero = fichero;
	}

}
