package spaceclipse.collab.messages;

import java.io.Serializable;

import spaceclipse.sincronizacion.Mensaje;

@SuppressWarnings("serial")
public class ModelMessage extends Mensaje implements Serializable {

	public ModelMessage(short tipo, String sender) {
		super(tipo, sender);
	}
	
	char[] archivoModelo;
	char[] archivoDiagrama;
	
	String rutaArchivoModelo;
	String rutaArchivoDiagrama;
	
	public String getRutaArchivoModelo() {
		return rutaArchivoModelo;
	}
	
	public void setRutaArchivoModelo(String rutaArchivoModelo) {
		this.rutaArchivoModelo = rutaArchivoModelo;
	}
	
	public String getRutaArchivoDiagrama() {
		return rutaArchivoDiagrama;
	}
	
	public void setRutaArchivoDiagrama(String rutaArchivoDiagrama) {
		this.rutaArchivoDiagrama = rutaArchivoDiagrama;
	}
	
	public char[] getArchivoModelo() {
		return archivoModelo;
	}
	
	public void setArchivoModelo(char[] archivoModelo) {
		this.archivoModelo = archivoModelo;
	}
	
	public char[] getArchivoDiagrama() {
		return archivoDiagrama;
	}
	
	public void setArchivoDiagrama(char[] archivoDiagrama) {
		this.archivoDiagrama = archivoDiagrama;
	}
	
}