/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spotitec;

/**
 *
 * @author saidn
 */
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainMenu extends JFrame {

    private PlaylistManager playlistManager;

    private static final Color COLOR_FONDO = new Color(25, 25, 25);
    private static final Color COLOR_BOTON = new Color(50, 50, 50);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_SPOTITEC_GREEN = new Color(30, 215, 96);
    private static final Font FONT_TITULO = new Font("Arial Black", Font.BOLD, 36);
    private static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 16);

    public MainMenu() {
        this.playlistManager = new PlaylistManager();

        setTitle("spotitec - Menú Principal");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Spotitec", SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITULO);
        titleLabel.setForeground(COLOR_SPOTITEC_GREEN);
        
        JButton btnAdd = new JButton("Agregar Canción");
        JButton btnRemove = new JButton("Eliminar Canción");
        JButton btnPlay = new JButton("Abrir Reproductor");
        JButton btnExit = new JButton("Salir");

        personalizarBoton(btnAdd);
        personalizarBoton(btnRemove);
        personalizarBoton(btnPlay);
        personalizarBoton(btnExit);

        btnAdd.addActionListener(e -> abrirDialogoAgregar());
        btnRemove.addActionListener(e -> abrirDialogoEliminar());
        btnPlay.addActionListener(e -> abrirReproductor());
        btnExit.addActionListener(e -> System.exit(0));

        mainPanel.add(titleLabel);
        mainPanel.add(btnAdd);
        mainPanel.add(btnRemove);
        mainPanel.add(btnPlay);
        mainPanel.add(btnExit);
        
        add(mainPanel);
    }

    private void personalizarBoton(JButton button) {
        button.setFont(FONT_BOTON);
        button.setBackground(COLOR_BOTON);
        button.setForeground(COLOR_TEXTO);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void abrirDialogoAgregar() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Seleccione el archivo de audio MP3");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Audio MP3", "mp3"));

    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File archivoSeleccionado = fileChooser.getSelectedFile();

        JTextField nombreField = new JTextField();
        JTextField artistaField = new JTextField();
        JTextField generoField = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nombre de la cancion:"));
        panel.add(nombreField);
        panel.add(new JLabel("Artista:"));
        panel.add(artistaField);
        panel.add(new JLabel("Genero:"));
        panel.add(generoField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Metadatos de la Canción", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            String artista = artistaField.getText().trim();
            String genero = generoField.getText().trim();

            if (nombre.isEmpty() || artista.isEmpty() || genero.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar todos los campos de texto.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
                return; 
            }

            JFileChooser imageChooser = new JFileChooser();
            imageChooser.setDialogTitle("Seleccione la imagen de la caratula");
            imageChooser.setFileFilter(new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "png"));
            
            if (imageChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File imagenSeleccionada = imageChooser.getSelectedFile();

                try {
                    File carpetaDestino = new File("Canciones");
                    if (!carpetaDestino.exists()) {
                        carpetaDestino.mkdir();
                    }

                    File archivoAudioDestino = new File(carpetaDestino.getPath(), archivoSeleccionado.getName());
                    Files.copy(archivoSeleccionado.toPath(), archivoAudioDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    File archivoImagenDestino = new File(carpetaDestino.getPath(), imagenSeleccionada.getName());
                    Files.copy(imagenSeleccionada.toPath(), archivoImagenDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    String rutaRelativaAudio = archivoAudioDestino.getPath();
                    String rutaRelativaImagen = archivoImagenDestino.getPath();

                    Cancion nuevaCancion = new Cancion(nombre, artista, "N/A", genero, rutaRelativaAudio, rutaRelativaImagen);
                    playlistManager.agregarCancion(nuevaCancion);
                    JOptionPane.showMessageDialog(this, "Canción agregada con éxito.");

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al copiar los archivos al proyecto.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }

            } else {
                 JOptionPane.showMessageDialog(this, "Debe seleccionar una imagen para la carátula.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
  
private void abrirDialogoEliminar() {
    try {
        ArrayList<Cancion> canciones = playlistManager.cargarCanciones();
        if (canciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La playlist está vacía.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opciones = canciones.stream().map(Cancion::toString).toArray(String[]::new);
        String cancionSeleccionadaStr = (String) JOptionPane.showInputDialog(this, "Seleccione la canción a eliminar:", "Eliminar Canción", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        
        if (cancionSeleccionadaStr != null) {
            int indexAEliminar = -1;
            for (int i = 0; i < canciones.size(); i++) {
                if (canciones.get(i).toString().equals(cancionSeleccionadaStr)) {
                    indexAEliminar = i;
                    break;
                }
            }

            if (indexAEliminar != -1) {

                Cancion cancionAEliminar = canciones.get(indexAEliminar);


                playlistManager.eliminarCancion(indexAEliminar);

                File archivoAudio = new File(cancionAEliminar.rutaArchivo);
                File archivoImagen = new File(cancionAEliminar.rutaImagen);

                if (archivoAudio.exists()) {
                    archivoAudio.delete();
                }
                if (archivoImagen.exists()) {
                    archivoImagen.delete();
                }

                JOptionPane.showMessageDialog(this, "Canción eliminada con éxito.");
                
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar o eliminar la playlist.", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    private void abrirReproductor() {
        try {
            ArrayList<Cancion> canciones = playlistManager.cargarCanciones();
            ListaEnlazadaSimple playlist = new ListaEnlazadaSimple();
            for(Cancion c : canciones) {
                playlist.agregar(c);
            }
            ReproductorMusicaGUI reproductor = new ReproductorMusicaGUI(playlist);
            reproductor.setVisible(true);
            this.dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la playlist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}