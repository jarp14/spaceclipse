package spaceclipse.herramientas;

import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ProcEspecifChat extends DefaultHandler {
	String fich;
	SAXParser parser = null;
	Vector mensajes = new Vector(6,4);
  
	public ProcEspecifChat(String fichEspecif) {
		fich = fichEspecif;
	}

	public Vector procesarEspecif() { 
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance ();
			parserFactory.setValidating(false); // Validacion mediante dtd: no
			parser = parserFactory.newSAXParser ();
			if (parser != null)
				parser.parse(fich, this);
		} catch (Exception e) {
			System.err.println("Problemas al procesar la especificacion XML de mensajes de chat");
			e.printStackTrace();
		}
		return mensajes;
	}
  
	public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String id, resp, texto, req;
		boolean reqText;

		if (qName.equals("message")) {
			id = attributes.getValue("id");
			texto = attributes.getValue("text");
			req = attributes.getValue("requiresText");
			reqText = false;
			if (req != null)
				reqText = req.equals("true") ? true : false;
			resp = attributes.getValue("repliesTo");
			ChatEstrMensaje mens = new ChatEstrMensaje(id,texto,resp,reqText);
			mensajes.addElement(mens);
		}
	}
	
}