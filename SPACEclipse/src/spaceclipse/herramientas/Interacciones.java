package spaceclipse.herramientas;

import java.sql.PreparedStatement;

import spaceclipse.util.Agente;

public class Interacciones implements ILog {
	/*public static void registrarInteraccion(String sesion, String usuario, String accion,
    String objeto, String valor1, String valor2, String valor3) {
    PreparedStatement ps;
    try {
      	ps = Agente.getBD().prepSentencia("insert into interacciones "+
        "(sesion,fecha,hora,usuario,accion,objeto,valor1,valor2,valor3) values "+
        "(?,CURRENT_DATE(),CURRENT_TIME(),?,?,?,?,?,?)");
      	ps.setString(1,sesion);
      	ps.setString(2,usuario);
      	ps.setString(3,accion);
      	ps.setString(4,objeto);
      	ps.setString(5,valor1);
      	ps.setString(6,valor2);
      	ps.setString(7,valor3);
      	Agente.ejecutarUpdIns(ps);
      	Agente.getBD().cerrarSentencia(ps);
    	} catch (Exception e) {
      	System.err.println("Error BD (registro interaccion): "+e.toString());
    	}
    }*/

	public void registrarMensaje(String sesion, String usuario, String mensaje, String tipoMensaje, String texto) {
		PreparedStatement ps;
		try {
			ps = Agente.getBD().prepSentencia("insert into mensajes_chat "+
					"(sesion,fecha,hora,usuario,mensaje,tipo_mensaje,texto) values "+
					"(?,CURRENT_DATE(),CURRENT_TIME(),?,?,?,?)");
			ps.setString(1,sesion);
			ps.setString(2,usuario);
			ps.setString(3,mensaje);
			ps.setString(4,tipoMensaje);
			ps.setString(5,texto);
			Agente.ejecutarUpdIns(ps);
			Agente.getBD().cerrarSentencia(ps);
		} catch (Exception e) {
			System.err.println("Error BD (registro mensaje): "+e.toString());
		}
	}

}