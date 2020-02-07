package spaceclipse.herramientas;

public interface ILog {
	public void registrarMensaje(String sesion, String usuario, String mensaje, String tipoMensaje, String texto);
}