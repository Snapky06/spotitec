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
import javax.sound.sampled.*;
import javazoom.jl.converter.Converter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReproductorMusicaGUI extends JFrame {

    private static final Color COLOR_FONDO = new Color(18, 18, 18);
    private static final Color COLOR_PANEL = new Color(24, 24, 24);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_SELECCION = new Color(30, 215, 96);
    private static final Font FONT_BOTON = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_TIEMPO = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_INFO = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_LISTA = new Font("Segoe UI", Font.PLAIN, 18);
    private JButton btnPlayPause, btnStop, btnBack, btnRemove;
    private JList<String> listaReproduccionVisual;
    private JLabel lblCaratula, lblInfoCancion;
    private DefaultListModel<String> listModel;
    private JSlider barraProgreso;
    private JLabel lblTiempoActual, lblTiempoTotal;
    private Timer timer;
    private ListaEnlazadaSimple playlist;
    private PlaylistManager playlistManager;
    private Clip clip;
    private int cancionSeleccionadaIndex = -1;
    private int cancionEnReproduccionIndex = -1;
    private long clipTimePosition;

    public ReproductorMusicaGUI(PlaylistManager manager) throws IOException {
        this.playlistManager = manager;
        this.playlist = new ListaEnlazadaSimple();
        ArrayList<Cancion> cancionesCargadas = playlistManager.cargarCanciones();
        for (Cancion c : cancionesCargadas) {
            playlist.agregar(c);
        }
        
        this.listModel = new DefaultListModel<>();

        setTitle("Spotitec - Reproductor");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(new Color(0,0,0,0));

        JPanel mainPanel = new JPanel(new BorderLayout(30, 20));
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JPanel panelCentral = new JPanel(new BorderLayout(20, 20));
        panelCentral.setOpaque(false);
        
        JPanel panelInferior = new JPanel(new BorderLayout(0, 10));
        panelInferior.setOpaque(false);

        JPanel panelProgreso = new JPanel(new BorderLayout(10, 0));
        panelProgreso.setOpaque(false);
        barraProgreso = new JSlider(0, 100, 0);
        barraProgreso.setEnabled(false);
        lblTiempoActual = new JLabel("00:00");
        lblTiempoTotal = new JLabel("00:00");
        lblTiempoActual.setForeground(COLOR_TEXTO);
        lblTiempoTotal.setForeground(COLOR_TEXTO);
        lblTiempoActual.setFont(FONT_TIEMPO);
        lblTiempoTotal.setFont(FONT_TIEMPO);
        panelProgreso.add(lblTiempoActual, BorderLayout.WEST);
        panelProgreso.add(barraProgreso, BorderLayout.CENTER);
        panelProgreso.add(lblTiempoTotal, BorderLayout.EAST);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelControles.setOpaque(false);
        
        btnPlayPause = new JButton("\u25B6 Play");
        btnStop = new JButton("\u25A0 Stop");
        btnBack = new JButton("Regresar al Menu");
        personalizarBoton(btnPlayPause);
        personalizarBoton(btnStop);
        personalizarBoton(btnBack);
        panelControles.add(btnPlayPause);
        panelControles.add(btnStop);
        panelControles.add(btnBack);

        panelInferior.add(panelProgreso, BorderLayout.NORTH);
        panelInferior.add(panelControles, BorderLayout.CENTER);

        lblCaratula = new JLabel();
        lblCaratula.setHorizontalAlignment(SwingConstants.CENTER);
        lblCaratula.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        lblCaratula.setText("Sin caratula");
        lblCaratula.setForeground(COLOR_TEXTO);

        lblInfoCancion = new JLabel("Seleccione una cancion", SwingConstants.CENTER);
        lblInfoCancion.setForeground(COLOR_TEXTO);
        lblInfoCancion.setFont(FONT_INFO);
        
        panelCentral.add(lblInfoCancion, BorderLayout.NORTH);
        panelCentral.add(lblCaratula, BorderLayout.CENTER);
        panelCentral.add(panelInferior, BorderLayout.SOUTH);

        JPanel panelLista = new JPanel(new BorderLayout(10, 10));
        panelLista.setOpaque(false);
        panelLista.setPreferredSize(new Dimension(450, 0));

        listaReproduccionVisual = new JList<>(listModel);
        listaReproduccionVisual.setBackground(COLOR_PANEL);
        listaReproduccionVisual.setForeground(COLOR_TEXTO);
        listaReproduccionVisual.setSelectionBackground(COLOR_SELECCION);
        listaReproduccionVisual.setSelectionForeground(Color.BLACK);
        listaReproduccionVisual.setFont(FONT_LISTA);
        listaReproduccionVisual.setFixedCellHeight(40);
        JScrollPane scrollPane = new JScrollPane(listaReproduccionVisual);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        
        btnRemove = new JButton("Eliminar Seleccion");
        personalizarBoton(btnRemove);
        panelLista.add(scrollPane, BorderLayout.CENTER);
        panelLista.add(btnRemove, BorderLayout.SOUTH);
        
        mainPanel.add(panelLista, BorderLayout.WEST);
        mainPanel.add(panelCentral, BorderLayout.CENTER);
        
        add(mainPanel);

        timer = new Timer(500, e -> actualizarBarraDeProgreso());
        actualizarListaVisual();
        configurarListeners();
    }
    
    private void personalizarBoton(JButton button) {
        button.setFont(FONT_BOTON);
        button.setBackground(new Color(40, 40, 40));
        button.setForeground(COLOR_TEXTO);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void configurarListeners() {
        btnPlayPause.addActionListener(e -> {
            if (clip != null && clip.isRunning()) {
                pausarCancion();
            } else {
                prepararYReproducirCancion();
            }
        });
        btnStop.addActionListener(e -> detenerCancion());
        btnRemove.addActionListener(e -> remove());
        btnBack.addActionListener(e -> {
            detenerCancion();
            if (timer.isRunning()) timer.stop();
            
            Runnable abrirMenu = () -> {
                MainMenu menu = new MainMenu();
                TransicionSuave.fadeIn(menu);
            };
            TransicionSuave.fadeOut(this, abrirMenu);
        });
        listaReproduccionVisual.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                }
                resetearBarra();
                cancionEnReproduccionIndex = -1;
                clipTimePosition = 0;
                cancionSeleccionadaIndex = listaReproduccionVisual.getSelectedIndex();
                if (cancionSeleccionadaIndex != -1) {
                    mostrarInfoCancion(playlist.obtener(cancionSeleccionadaIndex));
                }
            }
        });
    }

    private void remove() {
        int indexAEliminar = listaReproduccionVisual.getSelectedIndex();
        if (indexAEliminar == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una cancion para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (indexAEliminar == cancionEnReproduccionIndex) {
            detenerCancion();
        }
        try {
            playlistManager.eliminarCancion(indexAEliminar);
            playlist.eliminar(indexAEliminar);
            actualizarListaVisual();
            JOptionPane.showMessageDialog(this, "Cancion eliminada con exito.");
            if (playlist.estaVacia()) {
                lblCaratula.setIcon(null);
                lblCaratula.setText("Sin caratula");
                lblInfoCancion.setText("Playlist vacia");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la cancion del archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void prepararYReproducirCancion() {
        if (cancionSeleccionadaIndex == -1) { 
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una cancion.", "Aviso", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        if (clip != null && !clip.isRunning() && clipTimePosition > 0 && cancionEnReproduccionIndex == cancionSeleccionadaIndex) {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
            timer.start();
            btnPlayPause.setText("\u275A\u275A Pause");
            return;
        }
        lblInfoCancion.setText("Cargando y convirtiendo...");
        setControlesEnabled(false);
        SwingWorker<File, Void> converterWorker = new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                Cancion cancionSeleccionada = playlist.obtener(cancionSeleccionadaIndex);
                File archivoMp3 = new File(cancionSeleccionada.rutaArchivo);
                return convertirMp3AWav(archivoMp3);
            }
            @Override
            protected void done() {
                try {
                    File archivoWav = get();
                    if (archivoWav == null) throw new Exception("La conversion fallo.");
                    cargarYWavEnClip(archivoWav);
                    clip.setMicrosecondPosition(0);
                    clipTimePosition = 0;
                    barraProgreso.setMaximum((int) (clip.getMicrosecondLength() / 1000));
                    lblTiempoTotal.setText(formatearTiempo(clip.getMicrosecondLength()));
                    clip.start();
                    timer.start();
                    mostrarInfoCancion(playlist.obtener(cancionSeleccionadaIndex));
                    btnPlayPause.setText("\u275A\u275A Pause");
                    cancionEnReproduccionIndex = cancionSeleccionadaIndex;
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al convertir o reproducir el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                    lblInfoCancion.setText("Error al cargar");
                } finally {
                    setControlesEnabled(true);
                }
            }
        };
        converterWorker.execute();
    }
    private void pausarCancion() {
        if (clip != null && clip.isRunning()) {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
            timer.stop();
            btnPlayPause.setText("\u25B6 Play");
        }
    }
    private void detenerCancion() {
        if (clip != null && clip.isOpen()) {
            clip.stop();
            clip.close();
        }
        if(timer.isRunning()){
            timer.stop();
        }
        resetearBarra();
        cancionEnReproduccionIndex = -1;
    }
    private void resetearBarra(){
        clipTimePosition = 0;
        btnPlayPause.setText("\u25B6 Play");
        barraProgreso.setValue(0);
        lblTiempoActual.setText("00:00");
        lblTiempoTotal.setText("00:00");
    }
    private void actualizarBarraDeProgreso() {
        if (clip != null && clip.isRunning()) {
            long currentMicroseconds = clip.getMicrosecondPosition();
            barraProgreso.setValue((int) (currentMicroseconds / 1000));
            lblTiempoActual.setText(formatearTiempo(currentMicroseconds));
            if (currentMicroseconds >= clip.getMicrosecondLength()) {
                detenerCancion();
            }
        }
    }
    private String formatearTiempo(long microsegundos) {
        long minutos = TimeUnit.MICROSECONDS.toMinutes(microsegundos);
        long segundos = TimeUnit.MICROSECONDS.toSeconds(microsegundos) % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }
    private void setControlesEnabled(boolean enabled) {
        btnPlayPause.setEnabled(enabled);
        btnStop.setEnabled(enabled);
        btnBack.setEnabled(enabled);
        btnRemove.setEnabled(enabled);
    }
    private void mostrarInfoCancion(Cancion cancion) {
        if (cancion.imagenAlbum != null) { 
            ImageIcon scaledIcon = new ImageIcon(cancion.imagenAlbum.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH));
            lblCaratula.setIcon(scaledIcon); 
            lblCaratula.setText("");
        } else { 
            lblCaratula.setIcon(null); 
            lblCaratula.setText("Sin caratula"); 
        }
        lblInfoCancion.setText("<html><div style='text-align: center;'><b>" + cancion.nombre + "</b><br>" + cancion.artista + "</div></html>");
    }
    private void actualizarListaVisual() {
        listModel.clear();
        for (int i = 0; i < playlist.getTamano(); i++) { listModel.addElement(playlist.obtener(i).toString()); }
        listaReproduccionVisual.setModel(listModel);
    }
    private File convertirMp3AWav(File archivoMp3) {
        try {
            File tempWav = File.createTempFile("spotitec_temp", ".wav");
            tempWav.deleteOnExit();
            Converter converter = new Converter();
            converter.convert(archivoMp3.getAbsolutePath(), tempWav.getAbsolutePath());
            return tempWav;
        } catch (Exception e) { e.printStackTrace(); return null; }
    }
    private void cargarYWavEnClip(File archivoWav) throws Exception {
        if (clip != null && clip.isOpen()) { 
            clip.close(); 
        }
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoWav);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
    }
}