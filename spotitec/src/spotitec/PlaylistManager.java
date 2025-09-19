/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spotitec;

/**
 *
 * @author saidn
 */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class PlaylistManager {
    private static final String PLAYLIST_FILE = "playlist.dat";
    private static final int NAME_SIZE = 50;
    private static final int ARTIST_SIZE = 50;
    private static final int GENRE_SIZE = 30;
    private static final int PATH_SIZE = 150;
    private static final int IMG_PATH_SIZE = 150;
    private static final int RECORD_SIZE = (NAME_SIZE + ARTIST_SIZE + GENRE_SIZE + PATH_SIZE + IMG_PATH_SIZE) * 2;

    public void agregarCancion(Cancion cancion) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(PLAYLIST_FILE, "rw")) {
            raf.seek(raf.length());
            escribirStringFijo(raf, cancion.nombre, NAME_SIZE);
            escribirStringFijo(raf, cancion.artista, ARTIST_SIZE);
            escribirStringFijo(raf, cancion.genero, GENRE_SIZE);
            escribirStringFijo(raf, cancion.rutaArchivo, PATH_SIZE);
            escribirStringFijo(raf, cancion.rutaImagen, IMG_PATH_SIZE);
        }
    }

    public ArrayList<Cancion> cargarCanciones() throws IOException {
        ArrayList<Cancion> canciones = new ArrayList<>();
        if (!new File(PLAYLIST_FILE).exists()) {
            return canciones;
        }

        try (RandomAccessFile raf = new RandomAccessFile(PLAYLIST_FILE, "r")) {
            long numCanciones = raf.length() / RECORD_SIZE;
            for (int i = 0; i < numCanciones; i++) {
                raf.seek(i * RECORD_SIZE);
                String nombre = leerStringFijo(raf, NAME_SIZE);
                String artista = leerStringFijo(raf, ARTIST_SIZE);
                String genero = leerStringFijo(raf, GENRE_SIZE);
                String rutaArchivo = leerStringFijo(raf, PATH_SIZE);
                String rutaImagen = leerStringFijo(raf, IMG_PATH_SIZE);
                canciones.add(new Cancion(nombre, artista, "N/A", genero, rutaArchivo, rutaImagen));
            }
        }
        return canciones;
    }

    public void eliminarCancion(int index) throws IOException {
        ArrayList<Cancion> canciones = cargarCanciones();
        if (index >= 0 && index < canciones.size()) {
            canciones.remove(index);
            reescribirPlaylist(canciones);
        }
    }

    private void reescribirPlaylist(ArrayList<Cancion> canciones) throws IOException {
        File file = new File(PLAYLIST_FILE);
        if (file.exists()) {
            file.delete();
        }
        for (Cancion cancion : canciones) {
            agregarCancion(cancion);
        }
    }
    
    private void escribirStringFijo(RandomAccessFile raf, String str, int size) throws IOException {
        StringBuffer sb = new StringBuffer(str != null ? str : "");
        sb.setLength(size);
        raf.writeChars(sb.toString());
    }

    private String leerStringFijo(RandomAccessFile raf, int size) throws IOException {
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            chars[i] = raf.readChar();
        }
        return new String(chars).trim();
    }
}
