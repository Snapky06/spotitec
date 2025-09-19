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
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainMenu extends JFrame {

    private PlaylistManager playlistManager;

    private static final Color COLOR_FONDO = new Color(25, 25, 25);
    private static final Color COLOR_BOTON = new Color(50, 50, 50);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_SPOTITEC_GREEN = new Color(30, 215, 96);
    private static final Font FONT_TITULO = new Font("Arial Black", Font.BOLD, 72);
    private static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 20);

    public MainMenu() {
        this.playlistManager = new PlaylistManager();

        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        setBackground(new Color(0,0,0,0));
        
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        JLabel titleLabel = new JLabel("Spotitec", SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITULO);
        titleLabel.setForeground(COLOR_SPOTITEC_GREEN);
        
        JButton btnAdd = new JButton("Agregar Cancion");
        JButton btnSelect = new JButton("Abrir Reproductor");
        JButton btnExit = new JButton("Salir");

        personalizarBoton(btnAdd);
        personalizarBoton(btnSelect);
        personalizarBoton(btnExit);

        btnAdd.addActionListener(e -> add());
        btnSelect.addActionListener(e -> select());
        btnExit.addActionListener(e -> TransicionSuave.fadeOut(this, () -> System.exit(0) ));

        mainPanel.add(titleLabel);
        mainPanel.add(btnAdd);
        mainPanel.add(btnSelect);
        mainPanel.add(btnExit);
        
        add(mainPanel);
    }

    private void personalizarBoton(JButton button) {
        button.setFont(FONT_BOTON);
        button.setBackground(COLOR_BOTON);
        button.setForeground(COLOR_TEXTO);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60,60,60), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
    }

    private void add() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Audio MP3", "mp3"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getPath();
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
            int result = JOptionPane.showConfirmDialog(this, panel, "Datos de la Cancion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String nombre = nombreField.getText().trim();
                String artista = artistaField.getText().trim();
                String genero = generoField.getText().trim();
                if (nombre.isEmpty() || artista.isEmpty() || genero.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe llenar todos los campos de texto.", "Error de Validacion", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JFileChooser imageChooser = new JFileChooser();
                imageChooser.setDialogTitle("Seleccione la imagen de la caratula");
                imageChooser.setFileFilter(new FileNameExtensionFilter("Imagenes (JPG, PNG)", "jpg", "png"));
                String rutaImagen = "";
                if (imageChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    rutaImagen = imageChooser.getSelectedFile().getPath();
                }
                if (rutaImagen.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar una imagen para la caratula.", "Error de Validacion", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Cancion nuevaCancion = new Cancion(nombre, artista, "N/A", genero, rutaArchivo, rutaImagen);
                try {
                    playlistManager.agregarCancion(nuevaCancion);
                    JOptionPane.showMessageDialog(this, "Cancion agregada con exito.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al guardar la cancion.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void select() {
        Runnable abrirReproductor = () -> {
            try {
                ReproductorMusicaGUI reproductor = new ReproductorMusicaGUI(playlistManager);
                TransicionSuave.fadeIn(reproductor);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "No se pudo iniciar el reproductor.", "Error", JOptionPane.ERROR_MESSAGE);
                new MainMenu().setVisible(true); 
            }
        };
        TransicionSuave.fadeOut(this, abrirReproductor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FondoGUI().setVisible(true); 
            MainMenu menu = new MainMenu();
            TransicionSuave.fadeIn(menu); 
        });
    }
}