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

public class DSesionSWT extends Dialog {

	private String creador;
	private boolean administrador;
	private Properties param;
	private Shell shell;

	private Label lblSesion;
	private Label lblSesion1;
	private Label label1;
	private Label label2;
	private Label label3;
	private Label label4;
	private Label label5;
	private Text txtSesion;
	private Text txtTipo;
	private Text tFichero;
	private Text tFechaInic;
	private Text tFechaFin;
	private Text tHoraInic;
	private Text tHoraFin;
	private Button butAceptar;
	private Button butCancelar;
	private Button cbPrivada;

	public DSesionSWT(Shell parent, int style, String title, boolean modal, String creador,	boolean administrador, Properties param) {
		super(parent,style);
		setText(title);
		this.creador = creador;
		this.administrador = administrador;
		this.param = param;
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

	private void createContents() {
		shell.setLayout(new GridLayout(3, false));

		lblSesion = new Label(shell, SWT.NULL);
		lblSesion.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSesion.setAlignment(SWT.RIGHT);
		lblSesion.setText(param.getProperty("etiqNombreSes"));

		txtSesion = new Text(shell, SWT.SINGLE | SWT.BORDER);
		GridData gd_txtSesion = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_txtSesion.widthHint = 147;
		txtSesion.setLayoutData(gd_txtSesion);
		
		lblSesion1 = new Label(shell,SWT.NULL);
		lblSesion1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSesion1.setText(param.getProperty("etiqTipoSesion"));
		lblSesion1.setAlignment(SWT.RIGHT);

		txtTipo = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtTipo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
																						
		label1 = new Label(shell,SWT.NULL);
		label1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label1.setAlignment(SWT.RIGHT);
		label1.setText(param.getProperty("etiqFichSesion"));
																				
		tFichero = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tFichero.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
																		
		cbPrivada = new Button(shell,SWT.CHECK);
		cbPrivada.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1));
		cbPrivada.setText(param.getProperty("etiqPrivada"));
																
		label2 = new Label(shell,SWT.NULL);
		label2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		label2.setText(param.getProperty("etiqFechaInic"));
														
		tFechaInic = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tFechaInic.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		tFechaInic.setText("01/01/2018");
												
		label3 = new Label(shell,SWT.NULL);
		label3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		label3.setText(param.getProperty("etiqHoraInic"));
										
		tHoraInic = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tHoraInic.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		tHoraInic.setText("00:00:00");
								
		label4 = new Label(shell,SWT.NULL);
		label4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		label4.setText(param.getProperty("etiqFechaFin"));
						
		tFechaFin = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tFechaFin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		tFechaFin.setText("31/12/9999");
				
		label5 = new Label(shell,SWT.NULL);
		label5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		label5.setText(param.getProperty("etiqHoraFin"));
		
		tHoraFin = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tHoraFin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		tHoraFin.setText("23:59:59");

		butAceptar = new Button(shell, SWT.PUSH);
		butAceptar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		butAceptar.setText(param.getProperty("botAceptar"));
		butAceptar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				butAceptar_actionPerformed(e);
			}
		});

		butCancelar = new Button(shell, SWT.PUSH);
		butCancelar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		butCancelar.setText(param.getProperty("botCancelar"));
		butCancelar.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				butCancelar_actionPerformed(e);
			}
		});
		// Si no es administrador, no se puede cambiar sesion privada/publica
		if (!administrador)
			cbPrivada.setEnabled(false);
	}

	private void aceptar() {
		PreparedStatement ps;

		if (txtSesion.getText().equals("") || txtTipo.getText().equals(""))
			new DMensajesSWT(shell,"Error",param.getProperty("errNombreSesion"));
		else
			if (!Util.fechaCorrecta(tFechaInic.getText()) || !Util.fechaCorrecta(tFechaFin.getText()) ||
					!Util.horaCorrecta(tHoraInic.getText()) || !Util.horaCorrecta(tHoraFin.getText()))
				new DMensajesSWT(shell,"Error",param.getProperty("errFechaHoraIncorr"));
			else
				try {
					if (Agente.getBD().existeReg("sesiones","nombre",txtSesion.getText()))
						new DMensajesSWT(shell,"Error",param.getProperty("errSesionExiste"));
					else {
						ps = Agente.getBD().prepSentencia("insert into sesiones "+
								"(nombre,tipo,creador,fichero,privada,dia_inic,dia_final,hora_inic,hora_final) "+
								"values (?,?,?,?,?,?,?,?,?)");
						ps.setString(1,txtSesion.getText());
						ps.setString(2,txtTipo.getText());
						ps.setString(3,creador);
						ps.setString(4,tFichero.getText());
						ps.setInt(5,cbPrivada.getSelection()?1:0);
						ps.setString(6,Util.convF10_f8(tFechaInic.getText()));
						ps.setString(7,Util.convF10_f8(tFechaFin.getText()));
						ps.setString(8,tHoraInic.getText());
						ps.setString(9,tHoraFin.getText());
						Agente.ejecutarUpdIns(ps);
						Agente.getBD().cerrarSentencia(ps);
						shell.dispose();
					}
				} catch(Exception e) {
					new DMensajesSWT(shell,"Error BD",e.toString());
				}
	}

	private void cancelar() {
		shell.dispose();
	}

	void butAceptar_actionPerformed(SelectionEvent e) {
		aceptar();
	}

	void butCancelar_actionPerformed(SelectionEvent e) {
		cancelar();
	}

}