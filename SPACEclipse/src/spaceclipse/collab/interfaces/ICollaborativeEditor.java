package spaceclipse.collab.interfaces;

import java.util.Vector;

import spaceclipse.herramientas.IEditor;
import spaceclipse.herramientas.UsuarioPanel;

// Interfaz con los metodos que deberan tener los editores generados
public interface ICollaborativeEditor extends IEditor {
	public void copiaFichero(char[] archivoFichero, String rutaArchivo);
	public void copiaModelo(char[] archivoModelo, char[] archivoDiagrama, String rutaArchivoModelo, String rutaArchivoDiagrama);
	public void mueveTelepuntero(String usuario, int x, int y);
	public void actualizaTelepunteros(Vector<UsuarioPanel> listaUsuarios);
}