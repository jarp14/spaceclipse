package spaceclipse.herramientas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

import spaceclipse.Activator;
import spaceclipse.ecf.SpaceClient;
import spaceclipse.ecf.SpaceClienteCanal;
import spaceclipse.sincronizacion.ICliente;
import spaceclipse.sincronizacion.Mensaje;
import spaceclipse.util.Util;

public class ChatEstructuradoSWT extends ViewPart implements ICliente{
	public static final String ID = "SPACEclipse.chatEstructurado";
	private final String canalLocal = "chat";
	private String usuario = "";
	private SpaceClient cliente;
	private SpaceClienteCanal clienteCanal;
	
	private boolean pitar = true;
	private boolean grande = true;
	private boolean soportarMensLibre = true;

	private int numBotones = 0;
	private int numMens = 0;
	private int cont = 0;
	private String rutaXML;
	private ILog bd;
	private Vector mensajes = new Vector(5,3);
	private String mensajeId[] = new String[12];
	private String textoMensaje[] = new String[12];
	private final boolean completarMensaje[] = new boolean[12];
	private int botonActivo = 0;
	private boolean selecLista = false;
	private boolean hayMensEstructSelec = false;
	private String tokMensEstructSelec;

	private List lMensajes;
	private Button botonMensaje[] = new Button[12];
	private Text tMensaje;

	private Composite parent;
	private Properties parametros;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		parametros = abrirFicheroParametros("EN");
		rutaXML = "resources/"+parametros.getProperty("xmlChat");

		bd = new Interacciones();

		// Ruta dentro del plug-in
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL fullPathString = BundleUtility.find(bundle, rutaXML);

		// Definir los tipos de mensajes, procesando la especificacion
		ProcEspecifChat procChat = new ProcEspecifChat(fullPathString.toString());
		mensajes = procChat.procesarEspecif();
		
		// Se lee el numero de mensajes de la especificacion
		numMens = mensajes.size();
		numBotones = mensajes.size();

		short x;
		short y;
		short i;

		parent.setLayout(null);

		// Botones-mensajes
		ChatEstrMensaje mens;

		x = 320;
		y = 2;
		
		int numBotonesColumna = numBotones/2;
		if (numBotones%2 == 1) 
			numBotonesColumna++;
		int tamanoBoton = 100/numBotonesColumna;
		for (i=0; i<numBotones; i++) {
			mens = (ChatEstrMensaje)mensajes.elementAt(i);
			mensajeId[i] = mens.getMensajeId();
			textoMensaje[i] = mens.getTexto();
			completarMensaje[i] = mens.getRequiereTexto();
			if (grande) {
				botonMensaje[i] = new Button(parent, SWT.TOGGLE);
				botonMensaje[i].setBackground(new Color(null, new RGB(250,250,250)));
				botonMensaje[i].setText(textoMensaje[i]);
				botonMensaje[i].setBounds(new Rectangle(x+240, y, 170, tamanoBoton));
				botonMensaje[i].addSelectionListener(new ChatEstructuradoBotonMensSelectionAdapter(this, i, i));
				if (i == numBotonesColumna-1) {
					x += 170;
					y = 2;
				} else {
					y += tamanoBoton;
				}
			}
		}

		// Lista mensajes
		lMensajes = new List(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		lMensajes.setBackground(new Color(null, new RGB(255,255,255)));
		if (grande) {
			lMensajes.setBounds(new Rectangle(2, 2, 556, 130));
		} else {
			lMensajes.setBounds(new Rectangle(2, 2, 278, 65));
		}

		// Texto mensajes
		tMensaje = new Text(parent,SWT.SINGLE | SWT.BORDER);
		if (grande) {
			tMensaje.setBounds(new Rectangle(565, 110, 330, 22));
		} else {
			tMensaje.setBounds(new Rectangle(1, 75, 278, 22));
		}
		
		tMensaje.addListener(SWT.DefaultSelection, new ChatEstructuradoTMensajeSelectionAdapter(this));
		if (!soportarMensLibre) {
			tMensaje.setVisible(false);
		}

		Extraer g = new Extraer(fullPathString.toString(),40);

		getSite().getPage().addPartListener(new IPartListener2() {
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {}
			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {}
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				salir();
			}
			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {}
			@Override
			public void partHidden(IWorkbenchPartReference partRef) {}
			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {}
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {}
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {}

		});
	}

	Properties abrirFicheroParametros(String idioma) {
		Properties prop = new Properties();
		try {
			if (idioma.equals("EN"))
				prop.load(getClass().getResourceAsStream("space_en.properties"));
			else
				prop.load(getClass().getResourceAsStream("space_sp.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}

	public void salir() {
		cerrar();	   
	}

	public void iniciarConexion(SpaceClient cliente, String usuario){
		this.usuario = usuario;
		this.cliente = cliente;
		clienteCanal = new SpaceClienteCanal(this, cliente, canalLocal);
	}

	public void cerrar() {
		if (clienteCanal != null)
			clienteCanal.desconectarCanal();
	}

	@Override
	public void setFocus() {}

	// Enviar caja de texto libre (puede completar a un mensaje estructurado)
	private void enviarMensajeLibre() {
		MensajeChat m;
		String tipoMensaje;
		if (!hayMensEstructSelec) {
			// Es un texto libre independiente
			m = new MensajeChat(ConstChat.MSJ_TEXTO_LIBRE, usuario);
			m.setTextoAdic(tMensaje.getText());
			
			clienteCanal.enviar(m);
			procesarMensaje(m, usuario);
			tipoMensaje = selecLista ? "U":"L";
		} else {
			if (grande)
				botonMensaje[botonActivo].setForeground(new Color(null,new RGB(0,0,0)));
			if (!soportarMensLibre) // Se oculta si no estan permitidos mensajes libres
				tMensaje.setVisible(false);
			m = new MensajeChat(ConstChat.MSJ_ESTR_TEXT, usuario);
			m.setMensajeId(tokMensEstructSelec);
			m.setTextoAdic(tMensaje.getText());

			// Boton nuevo
			botonMensaje[botonActivo].setSelection(false);
			clienteCanal.enviar(m);
			procesarMensaje(m, usuario);
			tipoMensaje = selecLista ? "U":"E";
		}

		// Guardar mensaje
		bd.registrarMensaje(clienteCanal.getNombreContenedor(), usuario, m.getMensajeId(), tipoMensaje, m.getTextoAdic());
		// Borrar campo; foco; desasignar mensaje estructurado seleccionado
		tMensaje.setText("");
		hayMensEstructSelec = false;
		selecLista = false;
	}

	public short getnumMensaje(String s) {
		for (short i=0; i<numMens; i++) {
			if (textoMensaje[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}

	private void enviarMensajeEstructurado(short numMensaje) {
		// Si no hay que completar se envia el token y si hay que completar se completa
		if (!completarMensaje[numMensaje]) {
			MensajeChat m;
			m = new MensajeChat(ConstChat.MSJ_ESTRUCT, usuario);
			m.setMensajeId(mensajeId[numMensaje]);

			botonMensaje[botonActivo].setSelection(false);
			clienteCanal.enviar(m);
			procesarMensaje(m, usuario);

			bd.registrarMensaje(clienteCanal.getNombreContenedor(),
					usuario, m.getMensajeId(), "E", "");     
		} else {
			if (grande) {
				botonMensaje[botonActivo].setForeground(new Color(null,new RGB(255,255,255)));
			}
			if (!soportarMensLibre)
				tMensaje.setVisible(true);
			hayMensEstructSelec = true;
			tokMensEstructSelec = mensajeId[numMensaje];
		}
	}

	void botonMensSelectionPerformed(SelectionEvent e, short numMens,int pos) {	
		if (grande) {
			botonMensaje[botonActivo].setForeground(new Color(null,new RGB(0,0,0)));
		}
		if(botonActivo != pos){
			botonMensaje[botonActivo].setSelection(false);
		}
		botonActivo = pos;  
		enviarMensajeEstructurado(numMens);
	}

	void tMensajeActionPerformed(Event e) {
		enviarMensajeLibre();
	}

	private void procesarMensaje(Mensaje m, String quienEnvia) {
		boolean enc = false;
		short i;
		String id;
		cont = lMensajes.getItemCount()+1;
		if (m.getTipo() == ConstChat.MSJ_TEXTO_LIBRE)
			// Texto libre
			lMensajes.add(cont+" "+quienEnvia+"> "+((MensajeChat)m).getTextoAdic());
		else
			if(m.getTipo() == ConstChat.MSJ_ESTRUCT || m.getTipo() == ConstChat.MSJ_ESTR_TEXT) {
				// Mensajes estructurado con/sin texto adicional
				// Buscar mensaje estructurado
				id = ((MensajeChat)m).getMensajeId();
				for (i=0; i<mensajeId.length && !enc; i++) {
					if (id.equals(mensajeId[i])) {
						enc = true;
						if (!completarMensaje[i]) // Mensaje que no hay que completar
							lMensajes.add(cont+" "+quienEnvia+"> "+textoMensaje[i]+" "+((MensajeChat)m).getTextoAdic());
						else { // Mensaje a completar
							// Quitar puntos suspensivos
							String txt = textoMensaje[i].substring(0, textoMensaje[i].length()-3);
							lMensajes.add(cont+" "+quienEnvia+"> "+txt+" "+((MensajeChat)m).getTextoAdic());
						}
					}
				}
			}   
		// Pitar y mostrar ultimo
		if (pitar & !quienEnvia.equals(usuario))
			Util.pitido();
		// Metodo setSelection en lugar de select
		lMensajes.setSelection(lMensajes.getItemCount()-1);
	}

	@Override
	public void recibirDatos(byte[] message) {
		ByteArrayInputStream bins = new ByteArrayInputStream(message);
		try {
			ObjectInputStream ois = new ObjectInputStream(bins);
			final MensajeChat m = (MensajeChat) ois.readObject();
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					procesarMensaje(m, m.getSender());
				}

			});
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean isSoportarMensLibre() {
		return soportarMensLibre;
	}

	public void setSoportarMensLibre(boolean soportarMensLibre) {
		this.soportarMensLibre = soportarMensLibre;
	}

	public boolean isGrande() {
		return grande;
	}

	public void setGrande(boolean grande) {
		this.grande = grande;
	}

}

class ChatEstructuradoBotonMensSelectionAdapter implements SelectionListener {
	private ChatEstructuradoSWT adaptee;
	private short numMens;
	private int pos;

	ChatEstructuradoBotonMensSelectionAdapter(ChatEstructuradoSWT adaptee, short numMens, int pos)   {
		this.adaptee = adaptee;
		this.numMens = numMens;
		this.pos = pos;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.botonMensSelectionPerformed(e, numMens, pos);
	}
}

class ChatEstructuradoTMensajeSelectionAdapter implements Listener {
	private ChatEstructuradoSWT adaptee;

	ChatEstructuradoTMensajeSelectionAdapter(ChatEstructuradoSWT adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void handleEvent(Event event) {
		adaptee.tMensajeActionPerformed(event);
	}

}