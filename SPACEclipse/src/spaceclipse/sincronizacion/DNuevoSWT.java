package spaceclipse.sincronizacion;

import java.sql.PreparedStatement;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import spaceclipse.util.Agente;
import spaceclipse.util.Util;

public class DNuevoSWT extends Dialog {
	private boolean administrador;
	private Properties parametros;
	private Shell shell;

	private Text txtFoto;
	private Text txtLogin;
	private Text txtIP;
	private Text txtNombre;
	private Text txtClave;

	private Label lblFoto;
	private Label lblClave;
	private Label lblSesion;
	private Label lblSesion1;
	private Label lblIP;

	private Button cbAdministrador;

	public DNuevoSWT(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	public DNuevoSWT(Shell parent,String titulo,boolean admin,Properties parametros) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,titulo, admin, parametros);
	}

	public DNuevoSWT(Shell parent, int style) {
		super(parent, style);  
	}

	public DNuevoSWT(Shell parent, int style,String titulo,boolean admin,Properties parametros) {
		super(parent, style);
		setText(titulo);
		this.administrador = admin;
		this.parametros = parametros;    
	}

	public void open() {
		shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents();
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	void aceptar() {
		String loginUsuario;
		String nombreUsuario;
		String claveUsuario;
		String fotoUsuario;
		String ipUsuario;
		PreparedStatement ps;

		if (txtLogin.getText().equals("") || txtClave.getText().equals(""))
			new DMensajesSWT(shell,"Error",parametros.getProperty("errLoginBlanco"));
		else {
			try {
				if (Agente.getBD().existeReg("usuarios","id",txtLogin.getText()))
					new DMensajesSWT(shell,"Error",parametros.getProperty("errUsuarioYaExiste"));
				else {
					loginUsuario = txtLogin.getText();
					nombreUsuario = txtNombre.getText();
					claveUsuario = txtClave.getText();
					fotoUsuario = txtFoto.getText();
					ipUsuario = txtIP.getText();
					ps = Agente.getBD().prepSentencia("insert into usuarios "+
							"(id,nombre,clave,admin,foto,ip) values (?,?,?,?,?,?)");
					ps.setString(1,loginUsuario);
					ps.setString(2,nombreUsuario);
					ps.setString(3,claveUsuario);
					ps.setInt(4,cbAdministrador.getSelection()?1:0);
					ps.setString(5,fotoUsuario);
					ps.setString(6,ipUsuario);
					Agente.ejecutarUpdIns(ps);
					Agente.getBD().cerrarSentencia(ps);
					shell.dispose();
				}
			} catch(Exception e) {
				new DMensajesSWT(shell,"Error BD",e.toString());
			}
		}
	}

	void cancelar() {
		shell.dispose();
	}

	void butAceptar_SelectionPerformed(SelectionEvent e) {
		aceptar();
	}

	void butCancelar_SelectionPerformed(SelectionEvent e) {
		cancelar();
	}

	private void createContents() {
		shell.setLayout(null);

		txtLogin = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtLogin.setBounds(new Rectangle(81, 13, 150, 21));

		txtNombre = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtNombre.setBounds(new Rectangle(81, 44, 150, 21));

		txtClave = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtClave.setBounds(new Rectangle(81, 74, 150, 21));

		txtFoto = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtFoto.setBounds(new Rectangle(81, 105, 150, 21));

		txtIP = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtIP.setBounds(new Rectangle(81, 134, 150, 21));
		txtIP.setText(Util.obtenerIP());

		Button butAceptar = new Button(shell, SWT.PUSH);
		butAceptar.setText(parametros.getProperty("botAceptar"));
		butAceptar.setBounds(new Rectangle(8, 194, 87, 22));
		butAceptar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				butAceptar_SelectionPerformed(e);
			}
		});
		Button butCancelar=new Button(shell, SWT.PUSH);
		butCancelar.setText(parametros.getProperty("botCancelar"));
		butCancelar.setBounds(new Rectangle(146, 195, 87, 22));
		butCancelar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				butCancelar_SelectionPerformed(e);
			}
		});

		lblSesion = new Label(shell,SWT.NULL);
		lblSesion.setAlignment(SWT.RIGHT);
		lblSesion.setText(parametros.getProperty("etiqNombre"));
		lblSesion.setBounds(new Rectangle(6, 43, 64, 22));

		lblClave = new Label(shell,SWT.NULL);
		lblClave.setBounds(new Rectangle(16, 73, 54, 22));
		lblClave.setText(parametros.getProperty("etiqClave"));
		lblClave.setAlignment(SWT.RIGHT);

		lblFoto = new Label(shell,SWT.NULL);
		lblFoto.setAlignment(SWT.RIGHT);
		lblFoto.setText(parametros.getProperty("etiqURLFoto"));
		lblFoto.setBounds(new Rectangle(6, 104, 64, 22));

		lblSesion1 = new Label(shell,SWT.NULL);
		lblSesion1.setBounds(new Rectangle(6, 12, 64, 22));
		lblSesion1.setText(parametros.getProperty("etiqLogin"));
		lblSesion1.setAlignment(SWT.RIGHT);

		lblIP = new Label(shell,SWT.NULL);
		lblIP.setBounds(new Rectangle(6, 132, 64, 22));
		lblIP.setText(parametros.getProperty("etiqIP"));
		lblIP.setAlignment(SWT.RIGHT);

		cbAdministrador = new Button(shell,SWT.CHECK);
		cbAdministrador.setText(parametros.getProperty("etiqAdministrador"));
		cbAdministrador.setBounds(new Rectangle(64, 159, 120, 25));

		cbAdministrador.setEnabled(administrador);
	}

}