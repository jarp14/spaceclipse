package spaceclipse.collab.messages;

import java.io.Serializable;

import spaceclipse.sincronizacion.Mensaje;

public class ModelMessage extends Mensaje implements Serializable {

	// JGA 27/07/2009 Hereda de Mensaje para integrar con SPACEclipse
	public ModelMessage(short tipo, String sender) {
		super(tipo, sender);
	}
	
	char[] archivoModelo;
	char[] archivoDiagrama;
	
	//TODO: Ver si realmente hacen falta las rutas
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