/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spotitec;

/**
 *
 * @author saidn
 */
import javax.swing.ImageIcon;

public class Cancion {
    String nombre;
    String artista;
    String duracion;
    String genero;
    String rutaArchivo;
    String rutaImagen; 
    ImageIcon imagenAlbum;

    public Cancion(String nombre, String artista, String duracion, String genero, String rutaArchivo, String rutaImagen) {
        this.nombre = nombre;
        this.artista = artista;
        this.duracion = duracion;
        this.genero = genero;
        this.rutaArchivo = rutaArchivo;
        this.rutaImagen = rutaImagen; 

        if (rutaImagen != null && !rutaImagen.isEmpty()) {
            this.imagenAlbum = new ImageIcon(new ImageIcon(rutaImagen).getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH));
        } else {
            this.imagenAlbum = null;
        }
    }

    @Override
    public String toString() {
        return this.nombre + " - " + this.artista;
    }
}