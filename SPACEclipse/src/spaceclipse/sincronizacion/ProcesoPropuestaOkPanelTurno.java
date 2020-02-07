package spaceclipse.sincronizacion;

import java.util.Vector;

import spaceclipse.ecf.SpaceClienteCanal;
import spaceclipse.herramientas.MensajeTexto;
import spaceclipse.herramientas.MensajeTurno;
import spaceclipse.herramientas.PanelPropuestas;

public class ProcesoPropuestaOkPanelTurno {
	protected Vector<VotacionTurno> votaciones = new Vector<VotacionTurno>(3,2);
	protected SpaceClienteCanal cliente;
	// El usuario es el propietario de la votacion y el que cuenta
	protected String usuario;
	private PanelPropuestas vista;

	//private ILog bd;

	public ProcesoPropuestaOkPanelTurno(String usuario, SpaceClienteCanal cliente/*,ILog b*/,PanelPropuestas v) {
		this.cliente = cliente;
		this.usuario = usuario;
		vista = v;
		//bd = b;
	}

	// Añade una votacion a la lista de votaciones
	public void incluirVotacion(short idVotacion, short votoOk, short votoNoOk, short accionOk, short accionNo, String cambio) {
		boolean enc = false;

		// Buscar a ver si hay ya alguna del mismo tipo y borrarla
		for (int i=0; i<votaciones.size() && !enc; i++)
			if (((VotacionTurno)votaciones.elementAt(i)).idVotacion == idVotacion) {
				votaciones.removeElementAt(i);
				enc = true;
			}
		// Añadir la votacion a la lista; insertar propuesta en bd
		votaciones.addElement(new VotacionTurno(idVotacion,votoOk,votoNoOk,accionOk,accionNo));
		// DUDAA  bd.registrarMensaje(cliente.getNombreSesion(), usuario,Short.toString(idVotacion),usuario,"");
		// El propio usuario que propone vota con un primer ok
		contarVotosProcProp(idVotacion,votoOk,usuario,cambio);
	}

	// Cuenta el voto y si todo el mundo esta de acuerdo envia la accion correspondiente,
	// Eliminando la votacion
	public void contarVotosProcProp(short idVotacion, short voto, String quien, String cambio) {
		boolean enc=false, encVoto=false;
		VotacionTurno votacion;
		// Buscar primero la votacion
		for (int i=0; i<votaciones.size() && !enc; i++) {
			votacion = (VotacionTurno)votaciones.elementAt(i);
			if (votacion.idVotacion == idVotacion) { // Encontrada
				enc = true;
				if (votacion.votoOk == voto) { // Voto ok
					encVoto = insertarVotoOk(votacion.votantesOk,quien);
					// Insertar voto en bd (si el usuario no habia ya votado de esa forma)
					/*if(!encVoto)
	            		bd.registrarMensaje(cliente.getNombreSesion(),
	              								quien,Short.toString(votacion.votoOk),usuario,"");*/
				} else {
					if (votacion.votoNoOk == voto) { // Voto no ok
						encVoto = insertarVotoOk(votacion.votantesNoOk,quien);
						// Insertar voto en bd (si el usuario no habia ya votado de esa forma)
						/* if(!encVoto)
		              			bd.registrarMensaje(cliente.getNombreSesion(),
		                								quien,Short.toString(votacion.votoNoOk),usuario,"");*/
					}
				}
				// Comprobar todos han votado ok
				if (votacion.votantesOk.size() >= cliente.getParticipantes()) { // Todo el mundo esta ok
					// Enviar accion (el propietario es "elegido")
					MensajeTurno m = new MensajeTurno(votacion.accionOk,usuario);
					m.setUsuario(usuario);
					m.setCambio(cambio);
					cliente.enviar(m);
					vista.procesarMensajeTurno(m, m.getSender());
					// Insertar eleccion en bd
					//bd.registrarMensaje(cliente.getNombreSesion(),usuario,Short.toString(votacion.accionOk),usuario,"");
					// Eliminar votacion
					votaciones.removeElementAt(i);
				} else{
					if(votacion.votantesOk.size()+votacion.votantesNoOk.size() >=
							cliente.getParticipantes()){ // Todos han votado pero hay algun no ok
						// Enviar accion (fin votacion pero no hay eleccion)
						MensajeTexto m = new MensajeTexto(votacion.accionNo,usuario); //Creo que es aqui
						m.setTexto(usuario);
						cliente.enviar(m);
						vista.procesarMensajeTurno(m, m.getSender());
					}
				}
			}
		}
	}

	// Inserta un voto ok o no ok, segun el vector votantes
	protected boolean insertarVotoOk(Vector votantes, String votante) {
		boolean enc = false;

		// Buscar al votante, y si no se encuentra añadirlo
		for (int i=0; i<votantes.size() && !enc; i++)
			if (((String)votantes.elementAt(i)).equals(votante))
				enc = true;
		if (!enc)
			votantes.addElement(votante);

		return enc;
	}
}

class VotacionTurno {
	protected short idVotacion;
	protected short votoOk;
	protected short votoNoOk;
	protected short accionOk;
	protected short accionNo;
	protected Vector votantesOk, votantesNoOk;

	VotacionTurno(short idVotacion, short votoOk, short votoNoOk, short accionOk, short accionNo) {
		this.idVotacion = idVotacion;
		this.votoOk = votoOk;
		this.votoNoOk = votoNoOk;
		this.accionOk = accionOk;
		this.accionNo = accionNo;
		votantesOk = new Vector(3,2);
		votantesNoOk = new Vector(3,2);
	}
}
