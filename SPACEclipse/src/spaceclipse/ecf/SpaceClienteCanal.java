package spaceclipse.ecf;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.IDFactory;
import org.eclipse.ecf.core.sharedobject.ISharedObjectContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannel;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.datashare.IChannelListener;
import org.eclipse.ecf.datashare.events.IChannelEvent;
import org.eclipse.ecf.datashare.events.IChannelMessageEvent;

import spaceclipse.sincronizacion.ICliente;
import spaceclipse.sincronizacion.Mensaje;

public class SpaceClienteCanal {
	// Observador
	private ICliente herrObservador;
	// Cliente y canal
	private SpaceClient cliente;
	private IChannel canal = null;
	
	public SpaceClienteCanal(ICliente observador, SpaceClient cliente, String nombreCanal) {
		herrObservador = observador;
	    this.cliente = cliente;
	    // Crear cliente, sesion y canal
	    String canalLocal = cliente.getCanalLocal()+"-"+nombreCanal;
	    IChannelContainerAdapter channelContainer = (IChannelContainerAdapter) cliente.getContainer().getAdapter(IChannelContainerAdapter.class);
	    // Crea un ID para el canal que contenga la cadena local
		ID channelID;
		try {
			channelID = IDFactory.getDefault().createID(channelContainer.getChannelNamespace(), canalLocal);
			// Configuramos el listaner para poder recibir mensajes
			final IChannelListener channelListener = new IChannelListener() {
				@Override
				public void handleChannelEvent(IChannelEvent event) {
					if (event instanceof IChannelMessageEvent) {
						IChannelMessageEvent msg = (IChannelMessageEvent) event;
						herrObservador.recibirDatos(msg.getData());
					}
				}
			};
			// Se crea el nuevo canal
			canal = channelContainer.createChannel(channelID, channelListener, new HashMap());
		} catch (IDCreateException e) {
			e.printStackTrace();
		} catch (ECFException e) {
			e.printStackTrace();
		}
	}
	
	public void enviar(Mensaje m) {
		if (canal!=null && m!=null) {
			try {
				ByteArrayOutputStream bouts = new ByteArrayOutputStream();
				// Create a byte array from serialized Tool
				ObjectOutputStream douts = new ObjectOutputStream(bouts);
				douts.writeObject(m);
				// Send serialized tool to other clients.
				canal.sendMessage(bouts.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getParticipantes() {
		int num = 0;
		IContainer c = getCliente().getContainer();
		ISharedObjectContainer sharedObjectContainer = (ISharedObjectContainer) c.getAdapter(ISharedObjectContainer.class);
		ID[] ids = sharedObjectContainer.getGroupMemberIDs();
		num = ids.length;

		return (num-1);
	}
	
	public void desconectarCanal() {
		System.out.println("Falta la desconexion del canal y contenedor en Cliente Canal");
		cliente.desconectarSesion();
	}
	
	public SpaceClient getCliente() {
		return cliente;
	}

	public String getNombreContenedor() {
		return cliente.getNombreSesion();
	}
}