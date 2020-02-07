package spaceclipse.herramientas;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.ViewPart;

//JGA 05/03/2010 La clase pasa a ser publica
public class UsuarioPanel {
	private String nombre;
	private Image foto;
	private byte color;
	private String estado;
	private String estadoGlobal;

	//JGA 22/07/09 Se eliminan los "/" del inicio de las rutas para unificar
	private String imagenAnonimo[] = {"resources/anonimo1.jpg","resources/anonimo2.jpg",
			"resources/anonimo3.jpg","resources/anonimo4.jpg","resources/anonimo5.jpg"};

	UsuarioPanel(String fichFoto, byte color, ViewPart comp, String nombre) {
		boolean hayFoto = true;
		this.nombre = nombre;
		URL url;
		foto = null;
		if (fichFoto != null) {
			if (!fichFoto.equals(""))
				try {
					url = new URL(fichFoto);
					//foto = comp.getToolkit().createImage(url);
					foto = ImageDescriptor.createFromURL(url).createImage();
					// Sino asi
					/*try {
						URL url = new URL("http://www.google.com/intl/en/images/logo.gif";);
						InputStream is = url.openStream();
						Image image = new Image(Display.getCurrent(), is);
					} catch (Exception e) {
				   		e.printStackTrace();
					} finally {
				   		is.close();
					} */
				} catch(Exception e) {
					hayFoto = false;
				} else {
					hayFoto = false;
				}
		} else {
			hayFoto = false;
		}

		if (!hayFoto) {
			//foto = comp.getToolkit().createImage(this.getClass().getResource(imagenAnonimo[color]));
			//JGA 22/07/2009 Se coge el directorio del fichero de propiedades
			Properties parametros = abrirFicheroParametros("SP");
			String ruta = parametros.getProperty("rutaSpace");
			try {
				URL urlAnonimo = new URL (ruta+imagenAnonimo[color]);
				foto = ImageDescriptor.createFromURL(urlAnonimo).createImage();
			} catch (MalformedURLException e) {
				System.err.println("Error al crear la imagen por defecto");
				e.printStackTrace();
			}
			//foto = new Image(Display.getCurrent(),"."+imagenAnonimo[color]); // Probar y sino quitar lo de null y poner un display
			//foto = new Image(new Display(),imagenAnonimo[color]);
		}
		this.color = color;
		estado = null;
		estadoGlobal = null;
	}

	public void setEstado(String estado) { this.estado = estado; }
	public void setEstadoGlobal(String e) { this.estadoGlobal = e; }

	public byte getColor() { return color; }
	public String getEstado() { return estado; }
	public String getEstadoGlobal() { return estadoGlobal; }
	public Image getFoto() { return foto; }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String n) {
		nombre = n;
	}

	public boolean equals(UsuarioPanel p) {
		return (p.getNombre()).equals(this.getNombre());
	}

	//JGA 22/07/2009 Temporalmente, se copia aqui este metodo
	// TODO: Esta tambien en ChatEstructuradoSWT, asi que habria que unificarlo
	Properties abrirFicheroParametros(String idioma) {
		Properties prop = new Properties();
		try {
			if (idioma.equals("EN"))
				prop.load(getClass().getResourceAsStream("space_en.properties"));
			else
				prop.load(getClass().getResourceAsStream("space_sp.properties"));
			//prop.load(LeerFichWeb.openInputStreamFromWeb("http://172.20.48.27:8080/collece/resources/college_sp.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
  	
}
