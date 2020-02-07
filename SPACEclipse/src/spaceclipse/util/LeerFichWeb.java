package spaceclipse.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LeerFichWeb {

	public static String readfileContentFromWeb(String urlfile) throws Exception {
		String fileContent = "";

		try {
			URL url = new URL(urlfile);
			// Obtain the connection
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				throw new IOException("Something went wrong at the server. The response code was " +
						+ responseCode + ", " + connection.getResponseMessage());
			}
			InputStream input = connection.getInputStream();
			int maxBytes = 4096;
			byte[] buffer = new byte[maxBytes];
			ByteArrayOutputStream bufferArray = new ByteArrayOutputStream();
			int bytesRead = maxBytes;
			while(bytesRead > 0) {
				bytesRead = input.read(buffer);
				if(bytesRead > 0) {
					bufferArray.write(buffer, 0, bytesRead);
				}
			}
			fileContent=bufferArray.toString();
		} catch (MalformedURLException mfue) {
			System.err.println("Wrong server URL");
			throw mfue;
		} catch (IOException ie) {
			System.err.println("IOException during communication with bmv server");
			throw ie;
		}
		return fileContent;
	}

	public static InputStream openInputStreamFromWeb(String urlfile) throws Exception {
		try {
			URL url = new URL(urlfile);
			//obtain the connection
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.connect();
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				throw new IOException("Something went wrong at the server. The response code was " +
						+ responseCode + ", " + connection.getResponseMessage());
			}
			InputStream input = connection.getInputStream();
			return input;
		} catch (MalformedURLException mfue) {
			System.err.println("Wrong server URL");
			throw mfue;
		} catch (IOException ie) {
			System.err.println("IOException during communication with bmv server");
			throw ie;
		}
	}

}
