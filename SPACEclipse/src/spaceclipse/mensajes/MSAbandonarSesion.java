package spaceclipse.mensajes;

import spaceclipse.sincronizacion.Mensaje;
import spaceclipse.util.ConsMensajes;

public class MSAbandonarSesion extends Mensaje {

	int numeroParticipante;

	public MSAbandonarSesion (String s) {
		super(ConsMensajes.MS_ABANDONAR_SESION,s);
	}

	public int getNumeroParticipante() {
		return numeroParticipante;
	}

	public void setNumeroParticipante(int numeroParticipante) {
		this.numeroParticipante = numeroParticipante;
	}
	
}