package spaceclipse.herramientas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.part.ViewPart;

import spaceclipse.ecf.SpaceClient;
import spaceclipse.ecf.SpaceClienteCanal;
import spaceclipse.sincronizacion.ICliente;
import spaceclipse.sincronizacion.Mensaje;
import spaceclipse.sincronizacion.ProcesoPropuestaOkPanelTurno;
import spaceclipse.util.Util;

public abstract class PanelPropuestas extends ViewPart implements ICliente {
	protected SemaforoProp semafTurno;
	protected String usuario;
	protected boolean pitar = true;
	
	protected List liEdicion;
	protected Button bTurno, bDar, bNoDar;
	protected Label lEditor, lLeader;
	protected Combo combo;
	protected Properties parametros;
	
	protected SpaceClienteCanal clienteCanal = null;
	protected SpaceClient cliente = null;
	//protected final String canalLocal = "turno";
	
	protected ProcesoPropuestaOkPanelTurno procesoPropuestaOk;
	protected ICoordinacion coordinador;
	
	protected boolean eLider;
	
	//JGA 11/02/2010 El nombre del canal se lee desde un metodo para que sea abstracto
	protected abstract String getCanalLocal();

	Properties abrirFicheroParametros(String idioma) {
		Properties prop = new Properties();
		try {
			if (idioma.equals("EN"))
				prop.load(getClass().getResourceAsStream("panelturno_en.properties"));
			else
				prop.load(getClass().getResourceAsStream("panelturno_sp.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			// Ponerlo con una ventanita como abajo
			//Util.DialogoMsjError(this,"Fichero de parametros no encontrado","Error");
		}
		return prop;
	}

	public void setPitidoAwareness(boolean pitido) {
		pitar = pitido;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		parametros = abrirFicheroParametros("EN");
		if (parametros.getProperty("etiquetaLider").equals("Si")) {
			eLider = true;
		} else {
			eLider = false;
		}
		
		parent.setLayout(null);
		
		bTurno = new Button(parent, SWT.PUSH);
		liEdicion = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		/*if(parametros.getProperty("perspectives").equals("Si")) {
			bTurno.setBounds(new Rectangle(6, 10, 85, 31));
			combo = new Combo(parent, SWT.READ_ONLY);
		    combo.setBounds(6, 50, 110, 65);
		    String ids = parametros.getProperty("idsPerspectives");
		    String items[] = ids.split(", ");
		    
		    combo.setItems(items);
		    combo.select(0); // Para que este seleccionado alguno
			perspectivas = true;
			liEdicion.setBounds(new Rectangle(6, 75, 187, 165));
		} else {
			bTurno.setBounds(new Rectangle(6, 10, 85, 31));
		    perspectivas = false;
		    liEdicion.setBounds(new Rectangle(5, 50, 187, 182));
		}*/
		//bTurno.setText(parametros.getProperty("buttonTurno"));
	    bTurno.addSelectionListener(new bTurno_actionAdapter(this));
	    
	    bDar = new Button(parent, SWT.PUSH);
	    bDar.setText(parametros.getProperty("buttonOk"));
	    bDar.setBounds(new Rectangle(5, 245, 57, 26));
	    bDar.addSelectionListener(new bDar_selectionAdapter(this));
	    
	    bNoDar = new Button(parent, SWT.PUSH);
	    bNoDar.setText(parametros.getProperty("buttonNotOk"));
	    bNoDar.setBounds(new Rectangle(135, 245, 57, 26));
	    bNoDar.addSelectionListener(new bNoDar_selectionAdapter(this));
		
	    lEditor = new Label(parent, SWT.NONE);
	    lLeader = new Label(parent, SWT.NONE);
	    if (eLider) {
		    lEditor.setBounds(5, 245+60, 40, 26);
		    lEditor.setText("Editor: ");
		    lLeader.setBounds(5+40, 245+60, 57, 26);
	    }	    
	    //TRAMPAAAA
	    /*String[] items = { "rafa>Request the edition turn", "cresi>Give the turn to Rafa", "jesus>Give the turn to Rafa", "rafa>Editing...",
	    	    "cresi>Request the edition turn", "rafa>Give the turn to cresi", "jesus>Give the turn to cresi", "cresi>Editing..."	};
	    liEdicion.setItems(items);
	    lLeader.setText("Cresi");
	    */
	    semafTurno = new SemaforoProp(parent);
	    semafTurno.setBounds(new Rectangle(176, 15, 16, 21));
	}
	
	public void iniciarConexion(SpaceClient c, String u, ICoordinacion e) {
		// Cliente
	    usuario = u;
	    cliente = c;
	    coordinador = e;
		//JGA 11/02/2010 El nombre del canal se lee desde un metodo para que sea abstracto
	    clienteCanal = new SpaceClienteCanal(this, cliente, getCanalLocal());
	    procesoPropuestaOk = new ProcesoPropuestaOkPanelTurno(usuario, clienteCanal, this);
	}
	
	@Override
	public void setFocus() {}
	
	public abstract void bTurno_actionPerformed(SelectionEvent e); 
	
	protected String extraerNombUsuario(String linea) {
	    int posMay = linea.indexOf(">");
	    String nomb = "";

	    if(posMay >= 0)
	      nomb = linea.substring(0,posMay);

	    return nomb;
	}
	
	public String extraerCambio(String linea){
		String[] pal = linea.split(" ");
		return pal[pal.length-1];
	}
	
	public abstract void bDar_selectionPerformed(SelectionEvent e);
	public abstract void bNoDar_selectionPerformed(SelectionEvent e);
	
	public void pitar(String us) {
		if (pitar & !us.equals(usuario))
		      Util.pitido();
	}
	
	private void borrarLista(List lista) {
		lista.removeAll(); // Estaba comentado
	}
	
	private void insMensajeLista(List lista, String mensaje){
		lista.add(mensaje);
		lista.setSelection(lista.getSize().y-1);
		try { Thread.sleep(100); } catch (Exception e) {} // Espera para que se desplace bien la lista
	    
	}
	
	protected abstract void editarLeader(String edit,String p);

	public void procesarMensajeTurno(Mensaje m, String quienEnvia) {
		MensajeTurno mt;
		switch (m.getTipo()) {
		//----- propuestas -----
		case ConstPanelTurno.TURNO_PEDIR:
			pitar(quienEnvia);
			// Borrar lista si semaforo rojo (para que no sea confusa)
			if (semafTurno.estaApagado()) {
				borrarLista(liEdicion);
			}
			//FGG 15/12/2008 Se omite la opcion de un unico usuario
			//if (!usuarioUnico)
			mt = (MensajeTurno) m;
			//JGA 30/12/2009 El mensaje es distinto segun el tipo de panel
			//if (this instanceof PanelPropuestasTurno){
			insMensajeLista(liEdicion,quienEnvia+"> "+parametros.getProperty("msjTurno")+" "+mt.getCambio());
			//}
			//else{ // No me interesa este panel
			//	insMensajeLista(liEdicion,quienEnvia+"> "+parametros.getProperty("msjPerspective")+" "+mt.getCambio());
			//}
			if (procesoPropuestaOk != null) {
				if (quienEnvia.equals(usuario)) { // Iniciar proceso de votacion (cuenta el propio usuario)
					procesoPropuestaOk.incluirVotacion(ConstPanelTurno.TURNO_PEDIR, ConstPanelTurno.TURNO_OK,
							ConstPanelTurno.TURNO_NOOK, ConstPanelTurno.TURNO_ASIGNAR,
							ConstPanelTurno.TURNO_ASIGNAR_NO, mt.getCambio());
				} else { // Otro usuario cuenta (se activa semaforo)
					semafTurno.insertarProceso(quienEnvia);
				}
			}
			break;
		case ConstPanelTurno.TURNO_OK: {
			pitar(quienEnvia);
			String usTurno = ((MensajeTurno)m).getUsuario();
			String cambio = ((MensajeTurno)m).getCambio();
			//FGG 15/12/2008 Se omite la opcion de un unico usuario
			//if (!usuarioUnico)
			insMensajeLista(liEdicion,quienEnvia+"> "+parametros.getProperty("msjDar")+" "+usTurno);
			if (procesoPropuestaOk != null && usTurno.equals(usuario)) { // El iniciador vot. cuenta voto ok
				procesoPropuestaOk.contarVotosProcProp(ConstPanelTurno.TURNO_PEDIR,ConstPanelTurno.TURNO_OK, quienEnvia, cambio);
			}
			break;
		}
		case ConstPanelTurno.TURNO_NOOK: {
			pitar(quienEnvia);
			String usTurno = ((MensajeTurno)m).getUsuario();
			String cambio = ((MensajeTurno)m).getCambio();
			//FGG 15/12/2008 Se omite la opcion de un unico usuario
			//if (!usuarioUnico)
			insMensajeLista(liEdicion,quienEnvia+"> "+parametros.getProperty("msjNoDar")+" "+usTurno);
			if (procesoPropuestaOk != null && usTurno.equals(usuario)) // El iniciador vot. cuenta voto ok
				procesoPropuestaOk.contarVotosProcProp(ConstPanelTurno.TURNO_PEDIR, ConstPanelTurno.TURNO_NOOK,
						quienEnvia,cambio);
			break;
		}
		case ConstPanelTurno.TURNO_ASIGNAR: {
			pitar(quienEnvia);
			mt = ((MensajeTurno)m);
			String usTurno = mt.getUsuario();
			editarLeader(usTurno,mt.getCambio());
			semafTurno.eliminarProceso(usTurno);
			//JGA 30/12/2009 El mensaje es distinto segun el tipo de panel
			//if (this instanceof PanelPropuestasTurno) {
			insMensajeLista(liEdicion,parametros.getProperty("msjAsignarTurno")+" "+usTurno);
			//} else{
			//insMensajeLista(liEdicion,parametros.getProperty("msjAsignarPerspective")+" "+usTurno);
			//}
			break;
		}
		case ConstPanelTurno.TURNO_ASIGNAR_NO: {
			mt = ((MensajeTurno)m);
			String usTurno = mt.getUsuario();
			semafTurno.eliminarProceso(usTurno);
			break;
		}
		}
	}
	
	@Override
	public void recibirDatos(byte[] message) {
		ByteArrayInputStream bins = new ByteArrayInputStream(message);
		// DataInputStream dins = new DataInputStream(bins);
		try {
			ObjectInputStream ois = new ObjectInputStream(bins);
			final Mensaje m = (Mensaje) ois.readObject();

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					procesarMensajeTurno(m,m.getSender());
				}

			});
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

class bDar_selectionAdapter implements SelectionListener {
	private PanelPropuestas adaptee;

	bDar_selectionAdapter(PanelPropuestas adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.bDar_selectionPerformed(e);
	}
}

class bNoDar_selectionAdapter implements SelectionListener {
	private PanelPropuestas adaptee;

	bNoDar_selectionAdapter(PanelPropuestas adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.bNoDar_selectionPerformed(e);
	}
}

class bTurno_actionAdapter implements SelectionListener {
	private PanelPropuestas adaptee;

	public bTurno_actionAdapter(PanelPropuestas vista) {
		adaptee = vista;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.bTurno_actionPerformed(e);
	}
	
}