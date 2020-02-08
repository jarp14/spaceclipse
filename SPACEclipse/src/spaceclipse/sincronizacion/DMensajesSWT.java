package spaceclipse.sincronizacion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class DMensajesSWT extends Dialog {

	private String message;
	private Shell shell1;

	public DMensajesSWT(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	public DMensajesSWT(Shell parent,String titulo,String msj) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL,titulo,msj);
	}

	public DMensajesSWT(Shell parent, int style) {
		super(parent, style); 
	}

	public DMensajesSWT(Shell parent, int style,String titulo,String msj) {
		super(parent, style);
		setText(titulo);
		setMessage(msj);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void open() {
		shell1 = new Shell(getParent(), getStyle());
		shell1.setSize(246, 184);
		shell1.setText(getText());
		createContents(shell1);
		shell1.pack();
		shell1.open();
		Display display = getParent().getDisplay();
		while (!shell1.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents(final Shell shell) {
		shell1.setLayout(new GridLayout(1, false));

		Text textArea = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.CENTER | SWT.MULTI);
		textArea.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		textArea.setText(message);

		Button ok = new Button(shell, SWT.PUSH);
		ok.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		ok.setText("OK");
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		shell.setDefaultButton(ok);
	}

}
