package spaceclipse.herramientas;

import spaceclipse.sincronizacion.Mensaje;

@SuppressWarnings("serial")
public class MensajeEstado extends Mensaje {
	String usuarioEstado;
	String estado;
	boolean borrarOtros;

	public MensajeEstado(short tipo, String s) {
		super(tipo,s);
	}

	public void setUsuarioEstado(String ue) { usuarioEstado = ue; }
	public void setEstado(String e) { estado = e; }
	public void setBorrarOtros(boolean bo) { borrarOtros = bo; }

	public String getUsuarioEstado() { return usuarioEstado; }
	public String getEstado() { return estado; }
	public boolean getBorrarOtros() { return borrarOtros; }

}
