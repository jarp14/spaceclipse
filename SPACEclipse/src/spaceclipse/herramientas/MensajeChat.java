package spaceclipse.herramientas;

import spaceclipse.sincronizacion.Mensaje;

public class MensajeChat extends Mensaje {
	String mensajeId;
	String textoAdic;

	public MensajeChat(short tipo, String s) {
		super(tipo,s);
		mensajeId = "";
		textoAdic = "";
	}

	public void setMensajeId(String mi) { mensajeId=mi; }
	public void setTextoAdic(String ta) { textoAdic=ta; }

	public String getMensajeId() { return mensajeId; }
	public String getTextoAdic() { return textoAdic; }

}