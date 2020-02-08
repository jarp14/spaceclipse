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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

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
		shell.setLayout(new GridLayout(3, false));
		
				lblSesion1 = new Label(shell,SWT.NULL);
				lblSesion1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblSesion1.setText(parametros.getProperty("etiqLogin"));
				lblSesion1.setAlignment(SWT.RIGHT);

		txtLogin = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtLogin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
				lblSesion = new Label(shell,SWT.NULL);
				lblSesion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblSesion.setAlignment(SWT.RIGHT);
				lblSesion.setText(parametros.getProperty("etiqNombre"));

		txtNombre = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtNombre.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
				lblClave = new Label(shell,SWT.NULL);
				lblClave.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblClave.setText(parametros.getProperty("etiqClave"));
				lblClave.setAlignment(SWT.RIGHT);

		txtClave = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtClave.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
				lblFoto = new Label(shell,SWT.NULL);
				lblFoto.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblFoto.setAlignment(SWT.RIGHT);
				lblFoto.setText(parametros.getProperty("etiqURLFoto"));

		txtFoto = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtFoto.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
				lblIP = new Label(shell,SWT.NULL);
				lblIP.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				lblIP.setText(parametros.getProperty("etiqIP"));
				lblIP.setAlignment(SWT.RIGHT);

		txtIP = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtIP.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		txtIP.setText(Util.obtenerIP());
		
				cbAdministrador = new Button(shell,SWT.CHECK);
				cbAdministrador.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1));
				cbAdministrador.setText(parametros.getProperty("etiqAdministrador"));
				
						cbAdministrador.setEnabled(administrador);

		Button butAceptar = new Button(shell, SWT.PUSH);
		butAceptar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		butAceptar.setText(parametros.getProperty("botAceptar"));
		butAceptar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				butAceptar_SelectionPerformed(e);
			}
		});
		Button butCancelar=new Button(shell, SWT.PUSH);
		butCancelar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		butCancelar.setText(parametros.getProperty("botCancelar"));
		butCancelar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				butCancelar_SelectionPerformed(e);
			}
		});
	}

}