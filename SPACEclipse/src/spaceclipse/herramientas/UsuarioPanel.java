package spaceclipse.herramientas;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.ViewPart;

public class UsuarioPanel {
	private String nombre;
	private Image foto;
	private byte color;
	private String estado;
	private String estadoGlobal;

	private String imagenAnonimo[] = {"resources/anonimo1.jpg", "resources/anonimo2.jpg",
			"resources/anonimo3.jpg", "resources/anonimo4.jpg", "resources/anonimo5.jpg"};

	public UsuarioPanel(String fichFoto, byte color, ViewPart comp, String nombre) {
		boolean hayFoto = true;
		this.nombre = nombre;
		this.foto = null;
		URL url;
		
		if (fichFoto != null) {
			if (!fichFoto.equals("")) {
				try {
					url = new URL(fichFoto);
					foto = ImageDescriptor.createFromURL(url).createImage();
				} catch(Exception e) {
					hayFoto = false;
				}
			} else {
				hayFoto = false;
			}
		} else {
			hayFoto = false;
		}

		if (!hayFoto) {
			Properties parametros = abrirFicheroParametros("SP");
			String ruta = parametros.getProperty("rutaSpace");
			try {
				URL urlAnonimo = new URL (ruta+imagenAnonimo[color]);
				foto = ImageDescriptor.createFromURL(urlAnonimo).createImage();
			} catch (MalformedURLException e) {
				System.err.println("Error al crear la imagen por defecto");
				e.printStackTrace();
			}
		}
		this.color = color;
		this.estado = null;
		this.estadoGlobal = null;
	}

	public void setEstado(String estado) { this.estado = estado; }
	public void setEstadoGlobal(String estado) { this.estadoGlobal = estado; }

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
