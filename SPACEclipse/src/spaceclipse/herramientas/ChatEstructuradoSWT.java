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
import org.eclipse.swt.widgets.Combo;
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
	public final static String ID = "SPACEclipse.chatEstructurado";
	private final String canalLocal = "chat";
	private String usuario = "";
	private SpaceClienteCanal clienteCanal;
	private SpaceClient cliente;

	private int numResp = 0;
	private String remitente;
	private boolean repintar = false;
	private boolean pitar = true;
	private boolean hayMensEstructSelec = false;

	private int ancho, alto, numBotones=0, numMens=0, cont=0;
	private boolean soportarMensLibre=true;
	private boolean grande=true;
	private boolean respuesta=true;
	private String rutaXML;
	private String ruta;
	private ILog bd;
	private Vector mensajes = new Vector(5,3);
	private String[] requiererespuesta = new String[40];
	private String mensajeId[] = new String[12];
	private String textoMensaje[] = new String[12];
	private final boolean completarMensaje[] = new boolean[12];
	private int botonActivo = 0;
	private boolean selecLista = false;
	private String tokMensEstructSelec;

	private List lMensajes;
	private Button botonMensaje[] = new Button[12];
	private Combo lUltMensajes;
	private Combo lActosComunic;
	private Text tMensaje;

	private int anchoboton;
	private int anchomens;
	private int ochenta;

	private Composite parent;

	private Properties parametros;

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		parametros = abrirFicheroParametros("EN");
		ruta = parametros.getProperty("rutaSpace");

		// Lo de abajo iria en el constructor
		bd = new Interacciones();

		//this.grande = true;
		//this.soportarMensLibre = true;
		if (grande) {
			ancho = 560;
			alto = 180;
		} else {
			ancho = 236;
			alto = 80;
		}

		//respuesta = true;
		rutaXML = "resources/"+parametros.getProperty("xmlChat");

		//JGA 21/04/2010 Ruta dentro del plug-in
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		URL fullPathString = BundleUtility.find(bundle, rutaXML);

		// Definir los tipos de mensajes, procesando la especificacion
		ProcEspecifChat proc_chat = new ProcEspecifChat(fullPathString.toString());
		mensajes = proc_chat.procesarEspecif();
		//JGA 11/07/07 Se lee el numero de mensajes de la especificacion
		numMens = mensajes.size();
		numBotones = mensajes.size();


		short x,y,i;

		parent.setLayout(null);

		// Botones-mensajes
		ChatEstrMensaje mens;
		//int numMens = mensajes.size();
		x = 260+60;
		y = 2;
		//JGA 05/07/07 El numero de botones por columna no es fijo
		int numBotonesColumna = numBotones/2;
		//Correccion por si el numero es impar
		if (numBotones%2 == 1) 
			numBotonesColumna++;
		int tamanoBoton = 80/numBotonesColumna;
		for (i=0; i<numBotones; i++) {
			mens = (ChatEstrMensaje)mensajes.elementAt(i);
			mensajeId[i] = mens.getMensajeId();
			textoMensaje[i] = mens.getTexto();
			completarMensaje[i] = mens.getRequiereTexto();
			if (grande) {
				botonMensaje[i] = new Button(parent,SWT.TOGGLE);
				botonMensaje[i].setBackground(new Color(null,new RGB(250,250,250)));
				botonMensaje[i].setText(textoMensaje[i]);
				//botonMensaje[i].setBounds(new Rectangle(x, y, 98+20, tamanoBoton+(soportarMensLibre?0:4)));
				// Para el pantallazo
				botonMensaje[i].setBounds(new Rectangle(x+240, y, 98+20, tamanoBoton+(soportarMensLibre?0:4)));
				botonMensaje[i].addSelectionListener(new ChatEstructurado_botonMens_selectionAdapter(this,i,false,i));
				if (i == numBotonesColumna-1) {
					x = 360+80;
					y = 2;
				} else {
					y += tamanoBoton+(soportarMensLibre?0:4);
				}
			}
		}

		// Lista mensajes
		// JGA 06/07/2011 Scroll vertical
		lMensajes = new List(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		lMensajes.setBackground(new Color(null, new RGB(255,255,255)));

		if (grande){
			// Antes lMensajes.setBounds(new Rectangle(2, 2, 257+60, 117));
			// Para el pantallazo:
			lMensajes.setBounds(new Rectangle(2, 2, 257+300, 117+6));
		} else {
			lMensajes.setBounds(new Rectangle(2, 2, 233, 60));
		}
		// Ultimos mensajes
		lUltMensajes = new Combo(parent, SWT.READ_ONLY);

		lUltMensajes.addSelectionListener(new ChatEstructurado_lUltMensajes_itemAdapter(this));
		// Texto mensajes
		tMensaje = new Text(parent,SWT.SINGLE | SWT.BORDER);
		if (grande) {
			//tMensaje.setBounds(new Rectangle(320, 101, 238, 24));
			// Para el pantallazo
			tMensaje.setBounds(new Rectangle(240+320, 101, 238, 24));
		} else {
			tMensaje.setBounds(new Rectangle(1, 61, 124, 26));
		}
		tMensaje.addListener(SWT.DefaultSelection,new ChatEstructurado_tMensaje_selectionAdapter(this));
		if (!soportarMensLibre) {
			tMensaje.setVisible(false);
			lUltMensajes.setVisible(false);
		}
		// Mensajes fijos en lista
		lActosComunic = new Combo(parent, SWT.READ_ONLY);
		lActosComunic.addSelectionListener(new ChatEstructurado_lActosComunic_itemAdapter(this));
		if (!grande) {
			lActosComunic.setBounds(new Rectangle(126, 61, 92,17));
			for (i=0; i<numMens; i++)
				lActosComunic.add(textoMensaje[i]);
		}
		// Ultimos 5 mensajes
		if (grande) {
			//lUltMensajes.setBounds(new Rectangle(260+60, 84, 198+40, 16));
			// Para el pantallazo
			lUltMensajes.setBounds(new Rectangle(240+260+60, 84, 198+40, 16));
		} else {
			lUltMensajes.setBounds(new Rectangle(126, 61, 110, 17));
		}

		Extraer g = new Extraer(fullPathString.toString(),40);
		requiererespuesta = g.procesarEspecif(3);

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
		// Cliente
		this.usuario = usuario;
		this.cliente = cliente;
		clienteCanal = new SpaceClienteCanal(this,cliente,canalLocal);
	}

	public void cerrar() {
		if (clienteCanal != null)
			clienteCanal.desconectarCanal();
	}

	@Override
	public void setFocus() {}

	private void repintarbotones() {
		if (grande) {
			ancho = 560;
			alto = 120;
			ochenta = 80;
			anchoboton = 118;
			//altoboton;
			anchomens = 317;
			//altomens = 117;
			//ancholista = 238;
			//altolista = 16;
			//anchotexto = 238;
			//altotexto = 16;
		} else {
			ancho = 236;
			alto = 80;
			ochenta = 50;
			anchoboton = 50;
			//altoboton;
			anchomens = 133;
			//altomens = 76;
			//ancholista = 99;
			//altolista = 13;
			//anchotexto = 99;
			//altotexto = 13;
		}

		int x,y;
		short i;
		x = anchomens+1;
		y = 2;

		int numBotonesColumna = numBotones/2;

		// Correccion por si el numero es impar
		if (numBotones%2 == 1) numBotonesColumna++;
		int tamanoBoton = ochenta/numBotonesColumna;

		// Extraer los nombres de los botones y ver si requieren texto adicional

		String[] requiereadicional = new String[numMens];

		if (rutaXML != null) {	
			Extraer e = new Extraer(rutaXML,numMens);
			Extraer f = new Extraer(rutaXML,numMens);
			Extraer g = new Extraer(rutaXML,40);
			//Extraer h = new Extraer(rutaXML,40);

			textoMensaje = e.procesarEspecif(1);
			//idboton = h.procesarEspecif(4);
			requiereadicional = f.procesarEspecif(2);
			requiererespuesta = g.procesarEspecif(3);

			for (int k=0; k<numMens; k++) {
				completarMensaje[k] = requiereadicional[k].equals("true") ? true : false;
			}
		}        
		// Repintar los botones
		for (i=0; i<numBotones; i++) {
			botonMensaje[i] = new Button(parent,SWT.PUSH);
			botonMensaje[i].setText(textoMensaje[i]);
			//botonMensaje[i].setBounds(new Rectangle(x, y, 98+20, tamanoBoton+(textlibre?0:4)));
			botonMensaje[i].setBounds(new Rectangle(x, y, anchoboton, tamanoBoton+(soportarMensLibre?0:4)));
			botonMensaje[i].addSelectionListener(new ChatEstructurado_botonMens_selectionAdapter(this,i,false,i));
			//botonMensaje[i].setToolTipText(textoMensaje[i]);
			//botonMensaje[i].repaint();

			if (i == numBotonesColumna-1) {
				//x = 360+ochenta;
				x = anchomens+anchoboton+1;
				y = 2;
			} else
				y += tamanoBoton;
		}

		// Repintar la barra desplegable
		if (!soportarMensLibre) {
			tMensaje.setVisible(false);
			lUltMensajes.setVisible(false);
		} else {
			tMensaje.setVisible(true);
			lUltMensajes.setVisible(true);
		}
		//repaint();
	}

	private void repintarListaMens() {
		for (int i=0; i<numMens; i++)
			lActosComunic.add(textoMensaje[i]);
	}

	// Enviar caja de texto libre (puede completar a un mensaje estructurado)
	private void enviarMensajeLibre() {
		MensajeChat m;
		String tipoMensaje;
		if (!hayMensEstructSelec) {
			// Es un texto libre independiente
			m = new MensajeChat(ConstChat.MSJ_TEXTO_LIBRE,usuario);
			if (repintar) {
				m.setTextoAdic(tMensaje.getText()+" (responde al mensaje "+numResp+" de "+remitente+")");
				if (grande) {
					borrarbotones(numBotones);
					repintarbotones();
				} else {
					borrarListaMens();
					repintarListaMens();
				}
				repintar = false;
			} else {
				m.setTextoAdic(tMensaje.getText());
			}
			clienteCanal.enviar(m);
			procesarMensaje(m,usuario);
			tipoMensaje = selecLista?"U":"L"; // El texto puede ser uno de los 5 ult.
		} else {
			// Se ha enviado un mensaje estructurado con texto
			if (grande)	
				botonMensaje[botonActivo].setForeground(new Color(null,new RGB(0,0,0)));
			if (!soportarMensLibre) // Se oculta si no estan permitidos mensajes libres
				tMensaje.setVisible(false);
			m = new MensajeChat(ConstChat.MSJ_ESTR_TEXT,usuario);
			m.setMensajeId(tokMensEstructSelec);
			if (repintar) {
				m.setTextoAdic(tMensaje.getText()+" (responde al mensaje "+numResp+" de "+remitente+")");
				if (grande) {
					borrarbotones(numBotones);
					repintarbotones();
				} else {
					borrarListaMens();
					repintarListaMens();
				}
				repintar = false;
			} else {
				m.setTextoAdic(tMensaje.getText());
			}

			// Boton nuevo
			botonMensaje[botonActivo].setSelection(false);
			clienteCanal.enviar(m);
			procesarMensaje(m,usuario);
			tipoMensaje = selecLista ? "U":"E";
		}

		// Guardar mensaje
		bd.registrarMensaje(clienteCanal.getNombreContenedor(), usuario,m.getMensajeId(),tipoMensaje,m.getTextoAdic());
		// Borrar campo; foco; desasignar mensaje estructurado seleccionado
		tMensaje.setText("");
		//tMensaje.requestFocus();
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
		//if(repintar && !grande) numMensaje=getnumMensaje(lActosComunic.getItem(lActosComunic.getSelectionIndex()));
		// Si no hay que completar se envia el token y si hay que completar se completa
		if (!completarMensaje[numMensaje]) {
			MensajeChat m;
			if (repintar) {
				m = new MensajeChat(ConstChat.MSJ_ESTR_TEXT,usuario);
				m.setMensajeId(mensajeId[numMensaje]);
				m.setTextoAdic(" (responde al mensaje "+numResp+" de "+remitente+")");
				if(grande){
					borrarbotones(numBotones);
					repintarbotones();
				} else {
					borrarListaMens();
					repintarListaMens();
				}
				repintar = false;
			} else {
				m = new MensajeChat(ConstChat.MSJ_ESTRUCT,usuario);
				m.setMensajeId(mensajeId[numMensaje]);
			}

			// Boton nuevo
			botonMensaje[botonActivo].setSelection(false);
			clienteCanal.enviar(m);
			procesarMensaje(m,usuario);
			// Guardar mensaje
			bd.registrarMensaje(clienteCanal.getNombreContenedor(),
					usuario,m.getMensajeId(),"E","");     
		} else { // Hay que completar (mostrar caja texto si el chat no admite texto libre)
			if (grande){
				botonMensaje[botonActivo].setForeground(new Color(null,new RGB(255,255,255)));
			}
			if (!soportarMensLibre) // se visualiza si no estan permitidos mensajes libres
				tMensaje.setVisible(true);
			hayMensEstructSelec = true;
			//numMensEstructSelec = numMensaje;
			tokMensEstructSelec = mensajeId[numMensaje];
			//tMensaje.setText(tokMensEstructSelec+" ");
			//tMensaje.requestFocus();
		}
	}

	private void asignarUltTextoMensaje() {
		tMensaje.setText(lUltMensajes.getText());
		selecLista = true;
	}

	void lActosComunic_itemStateChanged(SelectionEvent e) {
		enviarMensajeEstructurado((short)lActosComunic.getSelectionIndex());
	}

	void botonMens_selectionPerformed(SelectionEvent e, short numMens,int pos) {	
		if (grande) {
			botonMensaje[botonActivo].setForeground(new Color(null,new RGB(0,0,0)));
		}
		if(botonActivo != pos){
			botonMensaje[botonActivo].setSelection(false);
		}
		botonActivo = pos;  
		enviarMensajeEstructurado(numMens);
	}

	void lUltMensajes_itemStateChanged(SelectionEvent e) {
		asignarUltTextoMensaje();
	}

	void tMensaje_actionPerformed(Event e) {
		enviarMensajeLibre();
	}

	private void borrarbotones(int a) {
		for(int j=0; j<a; j++){
			botonMensaje[j].setVisible(false);
			//this.remove(botonMensaje[j]);
		}	  
	}

	public void borrarListaMens() {
		lActosComunic.removeAll();
	}

	private void pintarsolorespuesta(String id,String aux,String quien){
		boolean enc = false;
		short n = 0,i;
		numResp = cont;
		remitente = quien;
		if (grande) {
			ancho = 560;
			alto = 120;
			ochenta = 80;
			anchoboton = 118;
			//altoboton;
			anchomens = 317;
			//altomens=117;
			//ancholista=238;
			//altolista=16;
			//anchotexto=238;
			//altotexto=16;
			borrarbotones(numBotones);
			//tienequeborrar=true;
			aux = aux+"...";
			String textoboton = "";
			for(i=0; i<mensajeId.length && !enc; i++) {
				if (id.equals(mensajeId[i])) {
					enc = true;
					n = i;
					textoboton = textoMensaje[i];
				}
			}
			//borrarbotones(numBotones);
			repintar = true;
			int x,y;
			x = anchomens+1;
			y = 2;
			int numBotonesColumna = numBotones/2;
			// Correccion por si el numero es impar
			if (numBotones%2==1) 
				numBotonesColumna++;
			int tamanoBoton = ochenta/numBotonesColumna;

			//Extraer f=new Extraer(rutaXML,40);
			//requiereadicionalrespuesta=f.procesarEspecif(5);
			//id requiere respuesta?
			/*String adic="";
		      for (int z=0;z<requiereadicionalrespuesta.length;z++){
		    	  if(id.equals(requiereadicionalrespuesta[z])){
		    		  z++;
		     		  adic=requiereadicionalrespuesta[z];
		    	  }
		    	  z++;
		      }*/
			//boolean adicional=adic.equals("true") ? true : false;
			// Repintar los botones
			for (i=0; i<numBotones; i++) {
				botonMensaje[i] = new Button(parent,SWT.PUSH);
				if (i==0) {
					botonMensaje[i].setText(textoboton);
				} else {
					botonMensaje[i].setText("------");
					//botonMensaje[i].setContentAreaFilled(false);
				}
				botonMensaje[i].setBounds(new Rectangle(x, y, anchoboton, tamanoBoton+(soportarMensLibre?0:4)));
				if (i==0) {
					botonMensaje[i].addSelectionListener(new ChatEstructurado_botonMens_selectionAdapter(this,n,true,i));
				}
				// Lo quite yo botonMensaje[i].setToolTipText(textoboton);
				if (i == numBotonesColumna-1) {
					x = anchomens+anchoboton+1;
					y = 2;
				} else
					y+=tamanoBoton;
			}
		} else {
			ancho = 236;
			alto = 80;
			ochenta = 50;
			anchoboton = 50;
			anchomens = 133;
			borrarListaMens();
			String textoboton = "";
			for (i=0; i<mensajeId.length && !enc; i++) {
				if (id.equals(mensajeId[i])) {
					enc = true;
					n = i;
					textoboton = textoMensaje[i];
				}
			}
			lActosComunic.add(textoboton);
			repintar = true;
		}  
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
		//JGA 06/07/2011 Metodo setSelection en lugar de select
		lMensajes.setSelection(lMensajes.getItemCount()-1);

		//ESTO es lo nuevo
		if (respuesta) {
			String idsolorespuesta = "";
			String resp = ((MensajeChat)m).getMensajeId();
			boolean haz = false;
			// Resp tiene que ser de los que requiere respuesta
			String[] compr = new String[40];

			for (int j=0; j<requiererespuesta.length; j++){
				compr[j] = requiererespuesta[j];
				j++;
			}

			for (int j=0;j<compr.length;j++) {
				if (resp.equals(compr[j])) {
					haz = true;
				}
			}

			if ((haz) && (!(quienEnvia.equals(usuario)))) {
				for (int j=0; j<requiererespuesta.length; j++) {
					if (resp.equals(requiererespuesta[j])) {
						j++;
						idsolorespuesta = requiererespuesta[j];
					}
					j++;
				}
				pintarsolorespuesta(idsolorespuesta,((MensajeChat)m).getTextoAdic(),quienEnvia);
			}
		}
	}

	@Override
	public void recibirDatos(byte[] message) {
		ByteArrayInputStream bins = new ByteArrayInputStream(message);
		// DataInputStream dins = new DataInputStream(bins);
		try {
			ObjectInputStream ois = new ObjectInputStream(bins);
			final MensajeChat m = (MensajeChat) ois.readObject();
			// Apply the tool to the local canvas.
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					procesarMensaje(m,m.getSender());
				}

			});
			//procesarMensaje(m,usuario);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	//JGA 01/12/2009 Getters y setters para modificar las propiedades
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

	public boolean isRespuesta() {
		return respuesta;
	}

	public void setRespuesta(boolean respuesta) {
		this.respuesta = respuesta;
	}

}

class ChatEstructurado_botonMens_selectionAdapter implements SelectionListener {
	private ChatEstructuradoSWT adaptee;
	private short numMens;
	private int pos;

	ChatEstructurado_botonMens_selectionAdapter(ChatEstructuradoSWT adaptee, short numMens, boolean resp,int pos)   {
		this.adaptee = adaptee;
		this.numMens = numMens;
		this.pos = pos;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.botonMens_selectionPerformed(e,numMens,pos);
	}
}

class ChatEstructurado_lActosComunic_itemAdapter implements SelectionListener {
	private ChatEstructuradoSWT adaptee;

	ChatEstructurado_lActosComunic_itemAdapter(ChatEstructuradoSWT adaptee) {
		this.adaptee = adaptee;
	}
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.lActosComunic_itemStateChanged(e);
	}

}

class ChatEstructurado_lUltMensajes_itemAdapter implements SelectionListener {
	private ChatEstructuradoSWT adaptee;

	ChatEstructurado_lUltMensajes_itemAdapter(ChatEstructuradoSWT adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		adaptee.lUltMensajes_itemStateChanged(e);
	}

}

class ChatEstructurado_tMensaje_selectionAdapter implements Listener {
	private ChatEstructuradoSWT adaptee;

	ChatEstructurado_tMensaje_selectionAdapter(ChatEstructuradoSWT adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void handleEvent(Event event) {
		adaptee.tMensaje_actionPerformed(event);
	}

}