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
		shell.setLayout(null);

		lblSesion = new Label(shell, SWT.NULL);
		lblSesion.setAlignment(SWT.RIGHT);
		lblSesion.setText(param.getProperty("etiqNombreSes"));
		lblSesion.setBounds(new Rectangle(5, 6, 64, 22));

		lblSesion1 = new Label(shell,SWT.NULL);
		lblSesion1.setBounds(new Rectangle(15, 35, 54, 22));
		lblSesion1.setText(param.getProperty("etiqTipoSesion"));
		lblSesion1.setAlignment(SWT.RIGHT);

		txtSesion = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtSesion.setBounds(new Rectangle(79, 7, 150, 21));

		txtTipo = new Text(shell, SWT.SINGLE | SWT.BORDER);
		txtTipo.setBounds(new Rectangle(79, 36, 150, 21));

		butAceptar = new Button(shell, SWT.PUSH);
		butAceptar.setText(param.getProperty("botAceptar"));
		butAceptar.setBounds(new Rectangle(8, 214, 89, 22));
		butAceptar.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				butAceptar_actionPerformed(e);
			}
		});

		butCancelar = new Button(shell, SWT.PUSH);
		butCancelar.setText(param.getProperty("botCancelar"));
		butCancelar.setBounds(new Rectangle(137, 214, 89, 22));
		butCancelar.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				butCancelar_actionPerformed(e);
			}
		});

		label1 = new Label(shell,SWT.NULL);
		label1.setAlignment(SWT.RIGHT);
		label1.setText(param.getProperty("etiqFichSesion"));
		label1.setBounds(new Rectangle(6, 62, 63, 17));

		tFichero = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tFichero.setBounds(new Rectangle(79, 58, 150, 21));

		label2 = new Label(shell,SWT.NULL);
		label2.setText(param.getProperty("etiqFechaInic"));
		label2.setBounds(14, 111, 116, 14);

		tFechaInic = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tFechaInic.setText("01/01/2018");
		tFechaInic.setBounds(new Rectangle(139, 111, 87, 21));

		label3 = new Label(shell,SWT.NULL);
		label3.setBounds(14, 135, 116, 14);
		label3.setText(param.getProperty("etiqHoraInic"));

		label4 = new Label(shell,SWT.NULL);
		label4.setBounds(14, 158, 116, 14);
		label4.setText(param.getProperty("etiqFechaFin"));

		label5 = new Label(shell,SWT.NULL);
		label5.setBounds(11, 182, 118, 17);
		label5.setText(param.getProperty("etiqHoraFin"));

		tFechaFin = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tFechaFin.setText("31/12/9999");
		tFechaFin.setBounds(new Rectangle(139, 158, 87, 21));

		tHoraInic = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tHoraInic.setText("00:00:00");
		tHoraInic.setBounds(new Rectangle(139, 135, 87, 21));

		tHoraFin = new Text(shell, SWT.SINGLE | SWT.BORDER);
		tHoraFin.setText("23:59:59");
		tHoraFin.setBounds(new Rectangle(139, 182, 87, 21));

		cbPrivada = new Button(shell,SWT.CHECK);
		cbPrivada.setText(param.getProperty("etiqPrivada"));
		cbPrivada.setBounds(new Rectangle(64, 81, 83, 25));
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