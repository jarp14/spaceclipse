package spaceclipse.herramientas;

import spaceclipse.ecf.SpaceClient;

public class CoordinarTurno implements ICoordinacion {
	private IEditor editor;
	private String usuario;
	private SpaceClient cliente;
	
	public CoordinarTurno(IEditor e, SpaceClient c, String u) {
		editor = e;
		usuario = u;
		cliente = c;
	}
	
	@Override
	public void deshacerCambios() {
		editor.deshabilitar();
	}

	@Override
	public void hacerCambios(String p) {
		MensajeEstado m = new MensajeEstado(ConstPanelTurno.ESTADO_EDICION, usuario);
	    m.setUsuarioEstado(usuario);
	    m.setEstado("Editing.."); // Esto hay q ponerlo en el archivo de configuracion
	    m.setBorrarOtros(true);
	    cliente.enviarDatos(m);
	    
		editor.habilitar();
	}

}
