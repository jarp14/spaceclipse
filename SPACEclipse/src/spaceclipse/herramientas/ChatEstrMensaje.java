package spaceclipse.herramientas;

import java.util.StringTokenizer;
import java.util.Vector;

class ChatEstrMensaje {
	private String mensajeId, texto;
	private Vector respondeA;
	private boolean requiereTexto;
	
	ChatEstrMensaje(String mId, String texto, String resp, boolean reqTexto) {
		mensajeId = mId;
		this.texto = texto;
		respondeA = new Vector(2,2);
		if (resp != null) {
			StringTokenizer st = new StringTokenizer(resp,",");
			while(st.hasMoreTokens()) {
				respondeA.addElement(st);
				st.nextToken();
			}
		}
		requiereTexto = reqTexto;
	}
	
	public String getMensajeId() { 
		return mensajeId; 
	}

	public String getTexto() { 
		return texto; 
	}

	public boolean getRequiereTexto() { 
		return requiereTexto; 
	}
	
}