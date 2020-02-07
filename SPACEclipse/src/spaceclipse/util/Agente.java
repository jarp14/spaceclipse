package spaceclipse.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Agente {
	private static Agente mInstancia = null;
	private static Connection mBD = null;

	protected Agente() throws Exception {
		// ODBC
		// Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		// JDBC
		Class.forName("com.mysql.jdbc.Driver"); // mysql v3+
	}

	public static Agente getBD() throws Exception {
		if (mInstancia == null)
			mInstancia = new Agente();
		if (mBD == null) // Conexion por defecto
			mBD = DriverManager.getConnection("jdbc:mysql://"+getServidorDef()+"/cubico",
					"cubico","cubico"); // SPACE
		return mInstancia;
	}

	public static String getServidorDef() { return "space.inf-cr.uclm.es"; } // Servidor por defecto

	public static void cambiarServidor(String servidor, String bd, String usuario, String clave) throws Exception {
		if (mInstancia == null)
			mInstancia = new Agente();
		if (mBD != null)
			mBD.close();
		mBD = DriverManager.getConnection("jdbc:mysql://"+servidor+"/"+bd,usuario,clave);
	}

	public static void desconectar() throws Exception {
		mBD.close();
	}

	public static Statement crearSentencia() throws Exception {
		Statement stmt = null;
		stmt = mBD.createStatement();
		return stmt;
	}

	public static PreparedStatement prepSentencia(String sql) throws Exception {
		PreparedStatement pstmt = null;
		pstmt = mBD.prepareStatement(sql);
		return pstmt;
	}

	public static void cerrarSentencia(ResultSet rs, Statement stmt) throws Exception {
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
	}

	public static void cerrarSentencia(ResultSet rs, PreparedStatement pstmt) throws Exception {
		if (rs != null)
			rs.close();
		if (pstmt != null)
			pstmt.close();
	}

	public static void cerrarSentencia(PreparedStatement pstmt) throws Exception {
		if (pstmt != null)
			pstmt.close();
	}

	public static ResultSet ejecutarSelect(String sql, Statement stmt) throws Exception {
		ResultSet rs = null;
		rs = stmt.executeQuery(sql);
		return rs;
	}

	public static ResultSet ejecutarSelect(PreparedStatement pstmt) throws Exception {
		ResultSet rs = null;
		rs = pstmt.executeQuery();
		return rs;
	}

	public static int ejecutarUpdIns(PreparedStatement pstmt) throws Exception {
		int ret = 0;
		ret = pstmt.executeUpdate();
		return ret;
	}

	public static int ejecutarUpdIns(String sql, Statement stmt) throws Exception {
		int ret = 0;
		ret = stmt.executeUpdate(sql);
		return ret;
	}

	public static boolean existeReg(String tabla, String columna, String valor) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs;
		boolean existe = false;

		pstmt = prepSentencia("SELECT "+columna+" FROM "+tabla+" WHERE "+columna+"=?");
		pstmt.setString(1,valor);
		rs = ejecutarSelect(pstmt);
		existe = rs.next();
		cerrarSentencia(rs,pstmt);

		return existe;
	}

	public static boolean existeReg(String tabla, String columna, int valor) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs;
		boolean existe = false;

		pstmt = prepSentencia("SELECT "+columna+" FROM "+tabla+" WHERE "+columna+"=?");
		pstmt.setInt(1,valor);
		rs = ejecutarSelect(pstmt);
		existe = rs.next();
		cerrarSentencia(rs,pstmt);

		return existe;
	}

}