package spaceclipse.ecf;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.eclipse.ecf.core.ContainerFactory;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;

import spaceclipse.sincronizacion.ICliente;
import spaceclipse.sincronizacion.Mensaje;
import spaceclipse.util.Agente;

public class SpaceClient {
	IContainer container = null;
	private IChannel channel = null;
	
	// Observador
	private ICliente aplicObservador;
	// Elementos que identifican la sesion, el usuario y el canal
	private String nombreSesion;
	private String tipoSesion;
	private String nombreCanalLocal;
	private String nombreCliente;
	private String nombreHost;
	
	public SpaceClient(ICliente observador, String host, String nombreSesion, String tipoSesion, String nombreCanal, String usuario) {
		aplicObservador = observador;
		this.nombreSesion = nombreSesion;
	    this.tipoSesion = tipoSesion;
	    nombreCanalLocal = nombreSesion+"-"+nombreCanal;
	    nombreCliente = usuario;
	    nombreHost = host;
	}
	
	protected void createChannel() throws ECFException {
		IChannelContainerAdapter channelContainer = (IChannelContainerAdapter) container.getAdapter(IChannelContainerAdapter.class);
		final ID channelID = IDFactory.getDefault().createID(channelContainer.getChannelNamespace(), nombreCanalLocal);
		// Configuramos el listener para poder recibir mensajes
		final IChannelListener channelListener = new IChannelListener() {
			@Override
			public void handleChannelEvent(IChannelEvent event) {
				if (event instanceof IChannelMessageEvent) {
					IChannelMessageEvent msg = (IChannelMessageEvent) event;
					aplicObservador.recibirDatos(msg.getData());
				}
			}
		};
		// Se crea el nuevo canal
		channel = channelContainer.createChannel(channelID,channelListener,new HashMap());
	}
	
	public void createAndConnect() throws Exception {
		// Creamos una instancia contenedor con ContainerFactory
		container = ContainerFactory.getDefault().createContainer("ecf.generic.client");
		// Creamos un canal
		createChannel();
		// Conectamos el contenedor a la ID creada
		String nombreServidor = "ecftcp://"+nombreHost+":3282/server";
		container.connect(IDFactory.getDefault().createID(container.getConnectNamespace(), nombreServidor), null);
				
		insClienteSesionBD(); 
	}
		
	public void enviarDatos(Mensaje m) {
		if (channel!=null && m!=null) {
			try {
				ByteArrayOutputStream bouts = new ByteArrayOutputStream();
				// Create a byte array from serialized Tool
				ObjectOutputStream douts = new ObjectOutputStream(bouts);
				douts.writeObject(m);

				// Send serialized tool to other clients
				channel.sendMessage(bouts.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void insClienteSesionBD() {
		PreparedStatement ps;
		ResultSet rs;
		try {
			ps = Agente.getBD().prepSentencia("select * from usuarios_sesion where sesion=? and usuario=?");
			ps.setString(1,nombreSesion);
			ps.setString(2,nombreCliente);
			rs = Agente.getBD().ejecutarSelect(ps);
			if(!rs.next()) {
				ps = Agente.getBD().prepSentencia("insert into usuarios_sesion (sesion,usuario) "+
							"values (?,?)");
				ps.setString(1,nombreSesion);
				ps.setString(2,nombreCliente);
				Agente.getBD().ejecutarUpdIns(ps);
			}
			Agente.getBD().cerrarSentencia(rs,ps);
		} catch(Exception e) {
			System.err.println("Error ISSC (insertar cliente): "+e.toString());
		}
	}

	private void elimClienteSesionBD() {
		PreparedStatement ps;
		try {
			ps = Agente.getBD().prepSentencia("delete from usuarios_sesion where usuario=? and sesion=?");
			ps.setString(1,nombreCliente);
			ps.setString(2,nombreSesion);
			Agente.getBD().ejecutarUpdIns(ps);
			Agente.getBD().cerrarSentencia(ps);
		} catch(Exception e) {
			System.err.println("Error ISSC (eliminar cliente): "+e.toString());
		}
	}
	  
	private void elimClienteSesionBD(String us) {
		PreparedStatement ps;
		try {
			ps = Agente.getBD().prepSentencia("delete from usuarios_sesion where usuario=? and sesion=?");
			ps.setString(1,us);
			ps.setString(2,nombreSesion);
			Agente.getBD().ejecutarUpdIns(ps);
			Agente.getBD().cerrarSentencia(ps);
		} catch(Exception e) {
			System.err.println("Error ISSC (eliminar cliente): "+e.toString());
		}
	}

	private void registrarSalida(String sesion, String tipo, String usuario) {
		PreparedStatement ps;
		try {
			ps = Agente.getBD().prepSentencia("insert into accesos "+
					"(sesion,tipo,fecha,hora,usuario,acceso) values (?,?,CURRENT_DATE(),CURRENT_TIME(),?,?)");
			ps.setString(1,sesion);
			ps.setString(2,tipo);
			ps.setString(3,usuario);
			ps.setString(4,"S");
			Agente.ejecutarUpdIns(ps);
			Agente.getBD().cerrarSentencia(ps);
		} catch (Exception e) {
			System.err.println("Error ISSC (registrar salida): "+e.toString());
		}
	}
	  
	// Metodo para desconectar al cliente de la sesion
	public void desconectarSesion() {
		elimClienteSesionBD();
		registrarSalida(nombreSesion,tipoSesion,nombreCliente);
		//channel.dispose();
		//container.disconnect();
	}
	
	public IContainer getContainer() {
		return container;
	}
	
	public String getCanalLocal() {
		return nombreCanalLocal;
	}

	public String getNombreSesion()	{
		return nombreSesion;
	}
	
}