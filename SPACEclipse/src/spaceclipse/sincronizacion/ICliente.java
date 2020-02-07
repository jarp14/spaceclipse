package spaceclipse.sincronizacion;

public interface ICliente {
	//public void recibirDatos(Mensaje m, String quienEnvia);
	public void recibirDatos(byte[] message);
}
