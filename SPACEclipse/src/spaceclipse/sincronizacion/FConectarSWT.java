package spaceclipse.sincronizacion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import spaceclipse.herramientas.IAplicacion;
import spaceclipse.util.Agente;
import spaceclipse.util.Util;

public class FConectarSWT extends Dialog {
	private Label lblLogin;
	private Text txtLogin;
	private Label lblClave;
	private Text txtClave ;
	private Button cbServSpace;
	private Button cbServOtro;
	private Text txtServidor;
	private Button butConectar;
	private Button butNuevo;
	private Button butEliminar;

	private IAplicacion aplicacion;
	private Properties parametros;

	private Shell shell;
	private String idioma;
	private String hostArg;

	public static void main(String[] args) {
		FConectarSWT FConectar = new FConectarSWT(new Shell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, null, "", "EN");
		try {
			FConectar.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FConectarSWT(Shell parent, int style, IAplicacion aplicacion, String hostArg, String idioma) {
		super(parent, style);
		this.aplicacion = aplicacion;
		this.shell = parent;	
		this.idioma = idioma;
		this.hostArg = hostArg;  
	}

	public void open() throws Exception {
		Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setSize(388, 194);
		shell.setText(getText());
		createContents(shell);
		Display display = getParent().getDisplay();
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	private void createContents(Shell shell) throws Exception {
		shell.setLayout(null);  
		shell.setEnabled(true);
		shell.setText("Login");

		// Apertura fichero de propiedades
		parametros = abrirFicheroParametros(idioma);

		lblLogin = new Label(shell,SWT.NULL);
		lblLogin.setAlignment(SWT.RIGHT);
		lblLogin.setText(parametros.getProperty("etiqLogin"));
		lblLogin.setBounds(new Rectangle(5, 20, 70, 20));
		txtLogin = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtLogin.setBounds(new Rectangle(76, 20, 166, 20));

		lblClave = new Label(shell,SWT.NULL);
		lblClave.setAlignment(SWT.RIGHT);
		lblClave.setText(parametros.getProperty("etiqClave"));
		lblClave.setBounds(new Rectangle(5, 52, 70, 20));
		txtClave = new Text(shell, SWT.SINGLE | SWT.BORDER );
		txtClave.setEchoChar('*');
		txtClave.setBounds(76, 52, 166, 20);

		cbServSpace = new Button(shell, SWT.RADIO);
		cbServSpace.setText(parametros.getProperty("etiqServidor"));
		cbServSpace.setBounds(new Rectangle(6, 85, 260, 25));
		cbServSpace.setSelection(true);

		cbServOtro = new Button(shell, SWT.RADIO);
		cbServOtro.setText(parametros.getProperty("etiqOtroServidor"));
		cbServOtro.setBounds(new Rectangle(6, 111, 103, 25));
		cbServOtro.setSelection(false);	    
		txtServidor = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtServidor.setText(Util.obtenerIP());
		txtServidor.setBounds(112,113,130,20);
		txtServidor.addMouseListener((MouseListener) new FConectar_txtServidor_mouseListener(this));
		txtServidor.addModifyListener(new FConectar_txtServidor_text(this));
		txtServidor.addSelectionListener((SelectionListener) new FConectar_txtServidor_SelectionAdapter(this));

		butConectar = new Button(shell, SWT.CENTER);
		butConectar.setText(parametros.getProperty("botConectar"));
		butConectar.setBounds(10, 147, 85, 25);
		butConectar.addSelectionListener(new FConectar_butConectar_SelectionListener(this));

		butNuevo = new Button(shell, SWT.CENTER);
		butNuevo.setBounds(new Rectangle(100, 147, 75, 25));
		butNuevo.addSelectionListener(new FConectar_butNuevo_SelectioListener(this));
		butNuevo.setText(parametros.getProperty("botNuevo"));

		butEliminar = new Button(shell, SWT.CENTER);
		butEliminar.setText(parametros.getProperty("botEliminar"));
		butEliminar.setBounds(new Rectangle(180, 147, 75, 25));
		butEliminar.addSelectionListener(new FConectar_butEliminar_selectionAdapter(this));

		shell.addListener(SWT.Close, new FConectar_this_windowListener(this));

		if (!hostArg.equals("")) {
			shell.setSize(shell.getSize().x, shell.getSize().y-20);
			cbServSpace.setVisible(false);
			cbServOtro.setEnabled(false);
			cbServOtro.setLocation(cbServOtro.getLocation().x,cbServOtro.getLocation().y-20);
			txtServidor.setEnabled(false);
			txtServidor.setText(hostArg);
			txtServidor.setLocation(txtServidor.getLocation().x,txtServidor.getLocation().y-20);
			butConectar.setLocation(butConectar.getLocation().x,butConectar.getLocation().y-20);
			butNuevo.setLocation(butNuevo.getLocation().x,butNuevo.getLocation().y-20);
			butEliminar.setLocation(butEliminar.getLocation().x,butEliminar.getLocation().y-20);
		}	
	}

	private void conectar() {
		hacerMinusculasUsuario();
		String servidor = obtenerServidor();
		boolean conexion = conectarServidor();
		if (conexion)
			accederSesion(txtLogin.getText(), txtClave.getText(), servidor);
	}

	private String obtenerServidor() {
		//FGG 12/12/2008 Ponemos la nueva IP del servidor space
		String servidor = cbServSpace.getSelection() ? parametros.getProperty("Ipservidor") : txtServidor.getText();
		return servidor;
	}

	private boolean conectarServidor() {
		String servidor = obtenerServidor();
		boolean conexion = false;

		if (!txtLogin.getText().equals("") && !servidor.equals("")) {
			// Cambiar servidor bd
			try {
				//JGA 06/07/2011 Se leen los datos del fichero de propiedades
				Agente.cambiarServidor(servidor, parametros.getProperty("DBName"), parametros.getProperty("DBUser"), parametros.getProperty("DBPassword"));
				conexion = true;
			} catch(Exception e) {
				e.printStackTrace();
				new DMensajesSWT(shell,"Error BD", e.toString()).open();
			}
		} else
			new DMensajesSWT(shell, "Error", parametros.getProperty("errIntrodNombres")).open();
		return conexion;
	}

	private byte esAdministrador(String login, String clave) {
		PreparedStatement ps;
		ResultSet rs;
		byte esAdm = ConstAdmin.US_NO_ENCONTRADO;
		boolean admin;

		try {
			// comprobar id y clave
			ps = Agente.getBD().prepSentencia("select admin from usuarios where id=? and clave=?");
			ps.setString(1,login);
			ps.setString(2,clave);
			rs = Agente.getBD().ejecutarSelect(ps);
			if (rs.next()) { // encontrado
				admin=(rs.getInt(1)==1 ? true : false);
				esAdm=(admin)? ConstAdmin.US_ES_ADMIN : ConstAdmin.US_ENCONTRADO_NO_ADMIN;
			}
			Agente.getBD().cerrarSentencia(rs,ps);
		} catch(Exception e) {
			new DMensajesSWT(shell,"Error BD",e.toString()).open();
		}

		return esAdm;
	}

	void cerrar() {
		shell.dispose();
	}

	private void accederSesion(String login, String clave, String servidor) {
		byte esAdmin = esAdministrador(txtLogin.getText(), txtClave.getText());

		if (esAdmin == ConstAdmin.US_ES_ADMIN || esAdmin == ConstAdmin.US_ENCONTRADO_NO_ADMIN) {
			FSesionSWT sesion = new FSesionSWT(new Shell(),SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,aplicacion,servidor,txtLogin.getText(),esAdmin, parametros);
			cerrar();
			sesion.open();
		} else
			new DMensajesSWT(shell,"Error", parametros.getProperty("errUsuarioNoVal")).open();
	}

	protected void hacerMinusculasUsuario() {
		txtLogin.setText(txtLogin.getText().toLowerCase());
		txtClave.setText(txtClave.getText().toLowerCase());
	}

	private void nuevoUsuario() {
		hacerMinusculasUsuario();
		boolean conectar = conectarServidor();
		if (conectar) {
			byte esAdmin = esAdministrador(txtLogin.getText(), txtClave.getText());
			boolean administrador = (esAdmin==ConstAdmin.US_ES_ADMIN?true:false);
			new DNuevoSWT(shell, parametros.getProperty("titNuevoUsuario"), administrador, parametros).open();
		}
	}

	private void eliminarUsuario() {
		hacerMinusculasUsuario();
		boolean conectar = conectarServidor();
		if (conectar) {
			// Se requiere conocer un login y su clave para poder eliminarlos,
			// Da igual ser administrador
			byte esAdmin = esAdministrador(txtLogin.getText(), txtClave.getText());

			if (esAdmin == ConstAdmin.US_NO_ENCONTRADO) {
				new DMensajesSWT(shell, "Error", parametros.getProperty("errUsuarioNoValElim")).open();
			} else {
				try {
					PreparedStatement ps;
					ps = Agente.getBD().prepSentencia("delete from usuarios where id=?");
					ps.setString(1, txtLogin.getText());
					Agente.getBD().ejecutarUpdIns(ps);
					Agente.getBD().cerrarSentencia(ps);
					txtLogin.setText("");
					txtClave.setText("");
				} catch(Exception e) {
					new DMensajesSWT(shell, "Error BD", e.toString()).open();
				}
			}
		} 
	}

	Properties abrirFicheroParametros(String idioma) {
		Properties prop = new Properties();
		try {
			if (idioma.equals("EN")) {
				prop.load(getClass().getResourceAsStream("issc_en.properties"));
			} else {
				prop.load(getClass().getResourceAsStream("issc_sp.properties"));
				//prop.load(LeerFichWeb.openInputStreamFromWeb("http://space.inf-cr.uclm.es:8080/college/resources/issc_sp.properties"));
			}
		} catch (Exception e) {
			new DMensajesSWT(shell, "Error", "Fichero de parametros no encontrado").open();
			shell.dispose();
		}
		return prop;
	}

	void txtServidor_mouseClicked(org.eclipse.swt.events.MouseEvent e) {
		cbServSpace.setSelection(false);
		cbServOtro.setSelection(true);
	}

	void txtServidor_textValueChanged(ModifyEvent e) {
		cbServSpace.setSelection(false);
		cbServOtro.setSelection(true);
	}

	void txtServidor_actionPerformed(SelectionEvent e) {
		cbServSpace.setSelection(false);
		cbServOtro.setSelection(true);
	}

	void butEliminar_SelectionPerformed(SelectionEvent e) {
		eliminarUsuario();
	}

	void butNuevo_actionPerformed(SelectionEvent e) {
		nuevoUsuario();
	}

	protected void butConectar_actionPerformed(SelectionEvent e) {
		conectar();
	}

	void this_windowClosing(Event e) {
		cerrar();
	}
}

class FConectar_txtServidor_text implements ModifyListener {
	private FConectarSWT adaptee;

	FConectar_txtServidor_text(FConectarSWT adaptee) {
		this.adaptee = adaptee;
	}

	public void modifyText(ModifyEvent e) {
		adaptee.txtServidor_textValueChanged(e);
	}
}

class FConectar_txtServidor_mouseListener implements MouseListener {
	private FConectarSWT adaptee;

	FConectar_txtServidor_mouseListener(FConectarSWT adaptee) {
		this.adaptee = adaptee;
	}

	public void mouseDown(org.eclipse.swt.events.MouseEvent e) {}

	public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {}

	public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
		adaptee.txtServidor_mouseClicked(e);
	}
}

class FConectar_butConectar_SelectionListener implements SelectionListener {
	private FConectarSWT adaptee;

	FConectar_butConectar_SelectionListener(FConectarSWT adaptee) {
		this.adaptee = adaptee;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent e) {
		adaptee.butConectar_actionPerformed(e);
	}
}

class FConectar_txtServidor_SelectionAdapter implements SelectionListener {
	private FConectarSWT adaptee;

	FConectar_txtServidor_SelectionAdapter(FConectarSWT adaptee) {
		this.adaptee = adaptee;
	}

	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent e) {
		adaptee.txtServidor_actionPerformed(e);
	}
}

class FConectar_butNuevo_SelectioListener implements SelectionListener {
	private FConectarSWT adaptee;

	public FConectar_butNuevo_SelectioListener(FConectarSWT adaptee) {
		this.adaptee = adaptee;
	}
	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent e) {
		adaptee.butNuevo_actionPerformed(e);
	}
}

class FConectar_butEliminar_selectionAdapter implements SelectionListener {
	private FConectarSWT adaptee;

	FConectar_butEliminar_selectionAdapter(FConectarSWT adaptee) {
		this.adaptee = adaptee;
	}
	public void widgetDefaultSelected(SelectionEvent arg0) {}

	public void widgetSelected(SelectionEvent e) {
		adaptee.butEliminar_SelectionPerformed(e);
	}
}

class FConectar_this_windowListener implements Listener {
	FConectarSWT adaptee;

	FConectar_this_windowListener(FConectarSWT adaptee) {
		this.adaptee = adaptee;
	}

	public void handleEvent(Event e) {
		adaptee.this_windowClosing(e);
	}
}