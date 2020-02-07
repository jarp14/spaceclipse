package spaceclipse.mensajes;

import spaceclipse.sincronizacion.Mensaje;
import spaceclipse.util.ConsMensajes;

public class MSNuevoUsuario extends Mensaje {

	public MSNuevoUsuario(String s) {
		super(ConsMensajes.MS_NUEVO_USUARIO,s);
	}

}