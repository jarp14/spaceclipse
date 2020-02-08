package spaceclipse.sincronizacion;

import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import spaceclipse.herramientas.IAplicacion;
import spaceclipse.util.Agente;
import spaceclipse.util.Util;

public class FSesionSWT extends Dialog {

	private IAplicacion aplicacion;
	private String host;
	private String usuario;
	private byte esAdmin;
	private Shell shell;
	private Properties param;

	private List lstSesiones;
	private List lstParticipantes;
	private List lstMiembros;
	private Label lblNombreSes;
	private Button butNuevaSesion;
	private Button butEliminarSesion;
	private Button butEliminarParticipante;
	private Button butAccederSesion;
	private Button butActualizar;
	private Button butAnadir;
	private Label label1;
	private Label label2;
	private Text tUsuario;
	private Text tUsuarioAnad;

	public FSesionSWT(Shell parent, int style, IAplicacion aplicacion, String host, String usuario, byte esAdmin,Properties param){
		super(parent, style);
		this.shell = parent;
		this.param = param;
		this.aplicacion = aplicacion;
		this.host = host;
		this.usuario = usuario;
		this.esAdmin = esAdmin;
	}

	public void open() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		createContents();
		shell.pack();
		shell.setBounds(0, 0, 600, 400);
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public void createContents() {
		shell.setLayout(null);
		shell.setText(param.getProperty("titSesiones"));
		shell.addListener(SWT.Close, new FSesion_this_windowListener(this));

		lstSesiones = new List(shell,SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		lstSesiones.setBounds(new Rectangle(6, 69, 588, 96));
		lstSesiones.addSelectionListener(new FSesion_lstSesiones_selecctionAdapter(this));

		lstParticipantes = new List(shell,SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		lstParticipantes.setBounds(new Rectangle(6, 240, 270, 80));

		lstMiembros = new List(shell,SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		lstMiembros.setBounds(new Rectangle(324, 240, 270, 80));

		lblNombreSes = new Label(shell,SWT.NULL);
		lblNombreSes.setText(param.getProperty("etiqCabSesiones"));
		lblNombreSes.setBounds(new Rectangle(6, 47, 588, 20));

		butNuevaSesion = new Button(shell, SWT.CENTER);
		butNuevaSesion.setText(param.getProperty("botNueva"));
		butNuevaSesion.setBounds(new Rectangle(6, 176, 100, 24));
		butNuevaSesion.addSelectionListener(new FSesion_butNuevaSesion_selectionAdapter(this));

		butEliminarSesion = new Button(shell, SWT.CENTER);
		butEliminarSesion.setBounds(new Rectangle(126, 176, 100, 24));
		butEliminarSesion.addSelectionListener(new FSesion_butEliminarSesion_selectionAdapter(this));
		butEliminarSesion.setText(param.getProperty("botEliminar"));

		butEliminarParticipante = new Button(shell, SWT.CENTER);
		butEliminarParticipante.setText(param.getProperty("botDesconectar"));
		butEliminarParticipante.setBounds(new Rectangle(6, 334, 100, 24));
		butEliminarParticipante.addSelectionListener(new FSesion_butEliminarParticipante_selectionAdapter(this));

		butAccederSesion = new Button(shell, SWT.CENTER);
		butAccederSesion.setBounds(new Rectangle(366, 176, 100, 24));
		butAccederSesion.addSelectionListener(new FSesion_butAccederSesion_selectionAdapter(this));
		butAccederSesion.setText(param.getProperty("botAcceder"));

		butActualizar = new Button(shell, SWT.CENTER);
		butActualizar.setText(param.getProperty("botActualizar"));
		butActualizar.setBounds(new Rectangle(246, 176, 100, 24));
		butActualizar.addSelectionListener(new FSesion_butActualizar_selectionAdapter(this));

		label1 = new Label(shell,SWT.RIGHT);
		label1.setText(param.getProperty("etiqUsuario"));
		label1.setBounds(new Rectangle(296, 20, 116, 17));

		tUsuario = new Text(shell,SWT.SINGLE | SWT.BORDER);
		tUsuario.setText(usuario);
		tUsuario.setEditable(false);
		tUsuario.setEnabled(false);
		tUsuario.setBounds(new Rectangle(421, 20, 173, 18));

		label2 = new Label(shell,SWT.NULL);
		label2.setText(param.getProperty("etiqMiembros"));
		label2.setBounds(new Rectangle(324, 216, 270, 20));

		butAnadir = new Button(shell, SWT.CENTER);
		butAnadir.setText(param.getProperty("botAnadir"));
		butAnadir.setBounds(new Rectangle(495, 334, 100, 24));
		butAnadir.addSelectionListener(new FSesion_butUsuarioAnad_selectionAdapter(this));

		tUsuarioAnad = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tUsuarioAnad.setBounds(new Rectangle(324, 334, 148, 20));

		// Si no se es administrador, no se pueden crear ni eliminar sesiones,
		// Ni tampoco añadir usuarios, aunque si desconectar
		if (esAdmin != ConstAdmin.US_ES_ADMIN) {
			butNuevaSesion.setEnabled(false);
			butEliminarSesion.setEnabled(false);
			butAnadir.setEnabled(false);
			tUsuarioAnad.setEnabled(false);
		}
		if (aplicacion == null) // No hay aplicacion
			butAccederSesion.setEnabled(false);

		leerSesiones();
	}

	private void leerSesiones() {
		PreparedStatement ps;
		ResultSet rs;

		lstSesiones.removeAll();
		// Los administradores acceden a todas las sesiones (para poder añadir ses. y us.)
		// Los no admin. acceden a las publicas y a las suyas
		try {
			if (esAdmin == ConstAdmin.US_ES_ADMIN) { // Administrador
				ps = Agente.getBD().prepSentencia("select nombre,tipo,creador,fichero,privada,"+
						"dia_inic,dia_final,hora_inic,hora_final from sesiones order by nombre");
				rs = Agente.getBD().ejecutarSelect(ps);
				while (rs.next())
					listarSesion(rs);
				Agente.getBD().cerrarSentencia(rs,ps);
			} else { // No administrador
				// Publicas
				ps = Agente.getBD().prepSentencia("select nombre,tipo,creador,fichero,privada,"+
						"dia_inic,dia_final,hora_inic,hora_final from sesiones where not privada "+
						"order by nombre");
				rs = Agente.getBD().ejecutarSelect(ps);
				while(rs.next())
					listarSesion(rs);
				Agente.getBD().cerrarSentencia(rs,ps);
				// Privadas
				ps = Agente.getBD().prepSentencia("select nombre,tipo,creador,fichero,privada,"+
						"dia_inic,dia_final,hora_inic,hora_final from sesiones,miembros_sesion "+
						" where nombre=sesion and usuario='"+usuario+"' order by nombre");
				rs = Agente.getBD().ejecutarSelect(ps);
				while (rs.next())
					listarSesion(rs);
				Agente.getBD().cerrarSentencia(rs,ps);
			}
		} catch(Exception e) {
			e.printStackTrace();
			new DMensajesSWT(shell,"Error BD",e.toString());
		}
		lstSesiones.select(lstSesiones.getItemCount()-1);
		leerParticipantes();
		leerMiembros();
	}

	private void listarSesion(ResultSet rs) {
		String periodo, hora_inic, hora_fin;
		String privada;

		try {
			hora_inic = rs.getString(8);
			hora_inic = (hora_inic==null)?"":hora_inic;
			hora_fin = rs.getString(9);
			hora_fin = (hora_fin==null)?"":hora_fin;
			periodo = Util.convF8_f10(rs.getString(6))+","+hora_inic+"-"+Util.convF8_f10(rs.getString(7))+","+
					hora_fin;
			privada = (rs.getInt(5)==1 ? "-" : "+");
			lstSesiones.add(privada+rs.getString(1)+" ["+rs.getString(2)+"] ("+rs.getString(3)+") / "+
					rs.getString(4)+" / "+periodo);
		} catch(Exception e) {
			e.printStackTrace();
			new DMensajesSWT(shell,"Error BD",e.toString());
		}
	}

	private String extraerNombreSesion(String lineaSesion) {
		int iCorch=lineaSesion.indexOf("[");
		String nombre = "";

		if (iCorch > 0)
			nombre = lineaSesion.substring(1,iCorch-1);
		return nombre;
	}

	private void leerParticipantes() {
		int index;
		String sesion, usuario;
		PreparedStatement ps;
		ResultSet rs;
		index = lstSesiones.getSelectionIndex();
		if (index != -1) {
			try {
				lstParticipantes.removeAll();
				sesion = lstSesiones.getItem(index);
				sesion = extraerNombreSesion(sesion);
				ps = Agente.getBD().prepSentencia("select * from usuarios_sesion where sesion=? "+
						"order by sesion,usuario");
				ps.setString(1,sesion);
				rs = Agente.getBD().ejecutarSelect(ps);
				while (rs.next()) {
					usuario = rs.getString("usuario");
					lstParticipantes.add(usuario);
				}
				Agente.getBD().cerrarSentencia(rs,ps);
			} catch(Exception e) {
				e.printStackTrace();
				new DMensajesSWT(shell,"Error BD",e.toString());
			}
		}
	}

	private void leerMiembros() {
		int index;
		String sesion, usuario;
		PreparedStatement ps;
		ResultSet rs;

		index = lstSesiones.getSelectionIndex();
		if (index != -1) {
			try {
				lstMiembros.removeAll();
				sesion = lstSesiones.getItem(index);
				sesion = extraerNombreSesion(sesion);
				ps = Agente.getBD().prepSentencia("select usuario from miembros_sesion where sesion=? "+
						"order by sesion,usuario");
				ps.setString(1,sesion);
				rs = Agente.getBD().ejecutarSelect(ps);
				while (rs.next()) {
					usuario = rs.getString(1);
					lstMiembros.add(usuario);
				}
				Agente.getBD().cerrarSentencia(rs,ps);
			} catch(Exception e) {
				e.printStackTrace();
				new DMensajesSWT(shell,"Error BD",e.toString());
			}
		}
	}

	private void eliminarSesion() {
		int index;
		String sesion, creador;
		PreparedStatement ps, ps2;
		ResultSet rs, rs2=null;

		index = lstSesiones.getSelectionIndex();
		if (index != -1) {
			try {
				sesion = lstSesiones.getItem(index);
				sesion = extraerNombreSesion(sesion);
				// Buscar creador
				ps = Agente.getBD().prepSentencia("select creador from sesiones where nombre=?");
				ps.setString(1,sesion);
				rs = Agente.getBD().ejecutarSelect(ps);
				if (rs.next()) { // Se extrae el creador de la sesion
					creador = rs.getString(1);
					if (creador.equals(usuario)) { // El usuario es el creador (se borran conectados y miembros)
						ps2 = Agente.getBD().prepSentencia("delete from usuarios_sesion where sesion=?");
						ps2.setString(1,sesion);
						Agente.getBD().ejecutarUpdIns(ps2);
						ps2 = Agente.getBD().prepSentencia("delete from miembros_sesion where sesion=?");
						ps2.setString(1,sesion);
						Agente.getBD().ejecutarUpdIns(ps2);
						ps2 = Agente.getBD().prepSentencia("delete from sesiones where nombre=?");
						ps2.setString(1,sesion);
						Agente.getBD().ejecutarUpdIns(ps2);
						Agente.getBD().cerrarSentencia(rs2,ps2);
						lstSesiones.remove(index);
						leerSesiones();
					} else
						new DMensajesSWT(shell,"Error",param.getProperty("errUsuarioNoProp"));
				}
				Agente.getBD().cerrarSentencia(rs,ps);
			} catch(Exception e) {
				e.printStackTrace();
				new DMensajesSWT(shell,"Error BD",e.toString());
			}
		}
	}

	private void registrarEntrada(String sesion, String tipo, String usuario) {
		PreparedStatement ps;

		try {
			ps = Agente.getBD().prepSentencia("insert into accesos "+
					"(sesion,tipo,fecha,hora,usuario,acceso) values (?,?,CURRENT_DATE(),CURRENT_TIME(),?,?)");
			ps.setString(1,sesion);
			ps.setString(2,tipo);
			ps.setString(3,usuario);
			ps.setString(4,"E");
			Agente.ejecutarUpdIns(ps);
			Agente.getBD().cerrarSentencia(ps);
		} catch (Exception e) {
			e.printStackTrace();
			new DMensajesSWT(shell,"Error BD",e.toString());
		}
	}

	private void accederSesion() {
		String sesion;
		String tipo;
		String fichero;
		String dia_inic, dia_final, hora_inic, hora_final, hora, fecha;
		PreparedStatement ps, ps2;
		ResultSet rs, rs2;
		boolean okConex=false, privada, comprobarFecha=true;
		int index = lstSesiones.getSelectionIndex();
		if (index != -1)
			if (aplicacion != null) {
				sesion = extraerNombreSesion(lstSesiones.getItem(index));
				// Comprobar que no esta conectado
				try {
					ps = Agente.getBD().prepSentencia("select * from usuarios_sesion where sesion=? and usuario=?");
					ps.setString(1,sesion);
					ps.setString(2,usuario);
					rs = Agente.getBD().ejecutarSelect(ps);
					if (!rs.next())
						okConex = true;
					Agente.getBD().cerrarSentencia(rs,ps);
					if (!okConex) // Esta ya conectado
						new DMensajesSWT(shell,"Error",Util.sustituirParams(param.getProperty("errUsuarioConectSes"),usuario,sesion)).open();
					else { // No esta conectado
						// Buscar todos los datos de la sesion y abrir aplicacion si procede
						ps2 = Agente.getBD().prepSentencia("select tipo,fichero,privada,dia_inic,dia_final,"+
								"hora_inic,hora_final from sesiones where nombre=?");
						ps2.setString(1,sesion);
						rs2 = Agente.getBD().ejecutarSelect(ps2);
						if (rs2.next()) { // Se encuentra la sesion
							tipo = rs2.getString(1);
							fichero = rs2.getString(2);
							privada = (rs2.getInt(3)==1 ? true : false);
							dia_inic = rs2.getString(4);
							dia_final = rs2.getString(5);
							hora_inic = rs2.getString(6);
							hora_final = rs2.getString(7);
							Agente.getBD().cerrarSentencia(rs2,ps2);
							// Comprobar si el usuario es miembro, si la sesion es privada
							if (privada) {
								ps2 = Agente.getBD().prepSentencia("select usuario from miembros_sesion where "+
										"sesion=? and usuario=?");
								ps2.setString(1,sesion);
								ps2.setString(2,usuario);
								rs2 = Agente.getBD().ejecutarSelect(ps2);
								if (!rs2.next()) { // No es miembro: mostrar error y no comprobar fechas
									comprobarFecha = false;
									new DMensajesSWT(shell,"Error", Util.sustituirParams(param.getProperty("errUsuarioNoPrivada"),usuario,sesion));
								}
								Agente.getBD().cerrarSentencia(rs2,ps2);
							}

							// Comprobar intervalo de fechas/horas (si no hay error previo)
							if (comprobarFecha) {
								hora = Util.obtenerHora();
								hora = hora.substring(0,8);
								fecha = Util.obtenerFecha();
								fecha = fecha.substring(0,4)+fecha.substring(5,7)+fecha.substring(8,10);
								if ((dia_inic+hora_inic).compareTo(fecha+hora)<0 &&
										(fecha+hora).compareTo(dia_final+hora_final)<0) {
									// Se accede (se cierra la herram. de sesiones) y se inicia aplicacion
									registrarEntrada(sesion,tipo,usuario);
									cerrar();
									aplicacion.iniciarAplicacion(host,sesion,usuario,tipo,fichero,esAdmin==ConstAdmin.US_ES_ADMIN);
								} else
									new DMensajesSWT(shell,"Error",Util.sustituirParams(param.getProperty("errSesionNoDisp"),sesion));
							}
						} else {
							Agente.getBD().cerrarSentencia(rs2,ps2);
							new DMensajesSWT(shell,"Error",Util.sustituirParams(param.getProperty("errSesionNoEnc"),sesion));
						}
					}
					Agente.getBD().cerrarSentencia(rs,ps);
				} catch(Exception e) {
					e.printStackTrace();
					new DMensajesSWT(shell,"Error BD",e.toString());
				}
			}
	}

	private void desconectarUsuario() {
		int index;
		String sesion,creador,usuarioDesc;
		PreparedStatement ps;
		ResultSet rs;

		index = lstSesiones.getSelectionIndex();
		if (index != -1) { // Se extrae la sesion
			sesion = lstSesiones.getItem(index);
			sesion = extraerNombreSesion(sesion);
			index = lstParticipantes.getSelectionIndex();
			if (index != -1) { // Se extrae el usuario a desconectar
				usuarioDesc = lstParticipantes.getItem(index);
				// Buscar creador
				try {
					ps = Agente.getBD().prepSentencia("select creador from sesiones where nombre=?");
					ps.setString(1,sesion);
					rs = Agente.getBD().ejecutarSelect(ps);
					if (rs.next()) { // Se extrae el creador de la sesion
						creador = rs.getString(1);
						if (creador.equals(usuario)) { // El usuario es el creador
							ps = Agente.getBD().prepSentencia("delete from usuarios_sesion where sesion=? and usuario=?");
							ps.setString(1,sesion);
							ps.setString(2,usuarioDesc);
							Agente.getBD().ejecutarUpdIns(ps);
							leerParticipantes();
						} else
							new DMensajesSWT(shell,"Error",param.getProperty("errUsuarioNoProp"));
					}
					Agente.getBD().cerrarSentencia(rs,ps);
				} catch(Exception e) {
					e.printStackTrace();
					new DMensajesSWT(shell,"Error BD",e.toString());
				}
			}
		}
	}

	private void anadirMiembro() {
		String sesion;
		PreparedStatement ps;
		ResultSet rs;
		boolean privada;
		int index = lstSesiones.getSelectionIndex();
		if (index != -1) {
			sesion = extraerNombreSesion(lstSesiones.getItem(index));
			try {
				// Comprobar que sea privada
				ps = Agente.getBD().prepSentencia("select privada from sesiones where nombre=?");
				ps.setString(1,sesion);
				rs = Agente.getBD().ejecutarSelect(ps);
				if (rs.next()) {
					privada = (rs.getInt(1)==1 ? true : false);
					if (!privada)
						new DMensajesSWT(shell,"Error",param.getProperty("errNoPoderAnadirMiemb"));
					else {
						ps = Agente.getBD().prepSentencia("insert into miembros_sesion "+
								"(sesion,usuario) values (?,?)");
						ps.setString(1,sesion);
						ps.setString(2,tUsuarioAnad.getText());
						Agente.ejecutarUpdIns(ps);
						leerMiembros();
						tUsuarioAnad.setText("");
						//tUsuarioAnad.requestFocus();
					}
				}
				Agente.getBD().cerrarSentencia(rs,ps);
			} catch(Exception e) {
				e.printStackTrace();
				new DMensajesSWT(shell,"Error BD",e.toString());
			}
		}
	}

	private void nuevaSesion() {
		boolean administrador = (esAdmin==ConstAdmin.US_ES_ADMIN);
		new DSesionSWT(new Shell(),SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,"New Session",true,usuario,administrador,param).open();
		leerSesiones();
	}

	void lstSesiones_actionPerformed(SelectionEvent e) {
		leerParticipantes();
		leerMiembros();
	}

	void butEliminarParticipante_actionPerformed(ActionEvent e) {
		desconectarUsuario();
	}

	void cerrar() {
		shell.dispose();
	}

	void butNuevaSesion_actionPerformed(SelectionEvent e) {
		nuevaSesion();
	}

	// Recoger la sesion accedida y cerrar la ventana de sesion
	void butAccederSesion_actionPerformed(SelectionEvent e) {
		accederSesion();
	}

	void butEliminarSesion_actionPerformed(SelectionEvent e) {
		eliminarSesion();
	}

	void butActualizar_actionPerformed(SelectionEvent e) {
		leerSesiones();
	}

	void butEliminarParticipante_actionPerformed(SelectionEvent e) {
		desconectarUsuario();
	}

	void butAnadir_actionPerformed(SelectionEvent e) {
		anadirMiembro();
	}

	void this_windowClosing(Event e) {
		cerrar();
	}
}

class FSesion_butAccederSesion_selectionAdapter implements SelectionListener {
	FSesionSWT adaptee;

	FSesion_butAccederSesion_selectionAdapter(FSesionSWT adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.butAccederSesion_actionPerformed(e);
	}
}

class FSesion_butNuevaSesion_selectionAdapter implements SelectionListener {
	FSesionSWT adaptee;

	FSesion_butNuevaSesion_selectionAdapter(FSesionSWT adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.butNuevaSesion_actionPerformed(e);
	}
}

class FSesion_this_windowListener implements Listener {
	FSesionSWT adaptee;

	FSesion_this_windowListener(FSesionSWT sesionSWT) {
		this.adaptee = sesionSWT;
	}

	public void handleEvent(Event e) {
		adaptee.this_windowClosing(e);
	}
}

class FSesion_butEliminarSesion_selectionAdapter implements SelectionListener {
	FSesionSWT adaptee;

	FSesion_butEliminarSesion_selectionAdapter(FSesionSWT adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.butEliminarSesion_actionPerformed(e);
	}
}

class FSesion_butEliminarParticipante_selectionAdapter implements SelectionListener {
	FSesionSWT adaptee;

	FSesion_butEliminarParticipante_selectionAdapter(FSesionSWT adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.butEliminarParticipante_actionPerformed(e);
	}
}

class FSesion_butActualizar_selectionAdapter implements SelectionListener {
	private FSesionSWT adaptee;

	FSesion_butActualizar_selectionAdapter(FSesionSWT adaptee) {
		this.adaptee = adaptee;
	}
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.butActualizar_actionPerformed(e);
	}
}

class FSesion_butUsuarioAnad_selectionAdapter implements SelectionListener {
	private FSesionSWT adaptee;

	FSesion_butUsuarioAnad_selectionAdapter(FSesionSWT adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.butAnadir_actionPerformed(e);
	}
}

class FSesion_lstSesiones_selecctionAdapter implements SelectionListener {
	private FSesionSWT adaptee;

	FSesion_lstSesiones_selecctionAdapter(FSesionSWT adaptee) {
		this.adaptee = adaptee;
	}
	@Override
	public void widgetDefaultSelected(SelectionEvent arg0) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.lstSesiones_actionPerformed(e);
	}
}
