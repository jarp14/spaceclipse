package spaceclipse.herramientas;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Extraer extends DefaultHandler {

	String fich;
	SAXParser parser = null;
	String[] mensajes;
	int i = 0;
	int numeromens;
	int solonombre;
	
	public Extraer(String fichEspecif,int nmens) {
		fich = fichEspecif;
		numeromens = nmens;
		mensajes = new String[nmens];
	}

	public String[] procesarEspecif(int control) { 
		solonombre = control;
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance ();
			parserFactory.setValidating (false); // Validacion mediante dtd: no
			parser = parserFactory.newSAXParser();
			if (parser != null)
				parser.parse(fich, this);
		} catch (Exception e) {
			System.err.println("Problemas al procesar la especificacion XML de mensajes de chat");
			e.printStackTrace();
		}

		return mensajes;
	}

	public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {
		String texto;
		String req;
		String resp;
		String id;
		String idresp;

		if (solonombre == 1) {
			if (i < numeromens) {
				if (qName.equals("message")) {
					texto = attributes.getValue("text");
					mensajes[i] = texto;
					i++;
				}
			}
		}

		if (solonombre == 2) {
			if (i < numeromens) {
				if (qName.equals("message")) {
					req = attributes.getValue("requiresText");
					if (req == null)
						req = "false";
					mensajes[i] = req;
					i++;
				}
			}
		}

		if (solonombre == 3) {
			//   if(i<numeromens){
			if (qName.equals("message")) {
				idresp = attributes.getValue("id");
				resp = attributes.getValue("repliesTo");
				if (resp != null) {
					mensajes[i] = resp;
					i++;
					mensajes[i] = idresp;
					i++;
				}
			}
			//}
		}

		if (solonombre == 4) {
			//  if(i<numeromens){
			if(qName.equals("message")) {
				id = attributes.getValue("id");
				texto = attributes.getValue("text");
				if (id != null) {
					mensajes[i] = id;
					i++;
					mensajes[i] = texto;
					i++;
				}

			}
			// }
		}

		if (solonombre == 5) {
			//if(i<numeromens) {
			if (qName.equals("message")) {
				id = attributes.getValue("id");
				req = attributes.getValue("requiresText");
				if (req == null) 
					req = "false";
				mensajes[i] = id;
				i++;
				mensajes[i] = req;
				i++;
			}
			//}
		}
	}

}