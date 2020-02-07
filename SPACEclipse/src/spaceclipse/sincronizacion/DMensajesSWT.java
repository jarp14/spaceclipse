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

public class DMensajesSWT extends Dialog {

	private String message;

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
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents(final Shell shell) {
		shell.setLayout(null);

		Text textArea = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
		textArea.setText(message);
		textArea.setBounds(new Rectangle(5, 6, 228, 114));

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.setBounds(new Rectangle(74, 127, 90, 24));
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		shell.setDefaultButton(ok);
	}

}
