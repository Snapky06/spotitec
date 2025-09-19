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
import javax.sound.sampled.*;
import javazoom.jl.converter.Converter;
import java.util.concurrent.TimeUnit;

public class ReproductorMusicaGUI extends JFrame {
    
    private static final Color COLOR_FONDO = new Color(45, 45, 45);
    private static final Color COLOR_PANEL = new Color(60, 60, 60);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_SELECCION = new Color(0, 120, 215);
    private static final Font FONT_BOTON = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_TIEMPO = new Font("Segoe UI", Font.PLAIN, 12);

    private JButton btnPlayPause, btnStop, btnBack;
    private JList<String> listaReproduccionVisual;
    private JLabel lblCaratula, lblInfoCancion;
    private DefaultListModel<String> listModel;

    private JSlider barraProgreso;
    private JLabel lblTiempoActual, lblTiempoTotal;
    private Timer timer;

    private ListaEnlazadaSimple playlist;
    private Clip clip;
    private int cancionSeleccionadaIndex = -1;
    private long clipTimePosition;
    
    public ReproductorMusicaGUI(ListaEnlazadaSimple playlist) {
        this.playlist = playlist;
        this.listModel = new DefaultListModel<>();

        setTitle("spotitec - Reproductor");
        setSize(800, 500); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel panelIzquierdo = new JPanel(new BorderLayout(10, 10));
        panelIzquierdo.setOpaque(false);
        
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setOpaque(false);

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelControles.setOpaque(false);
        
        lblCaratula = new JLabel();
        lblCaratula.setPreferredSize(new Dimension(180, 180));
        lblCaratula.setHorizontalAlignment(SwingConstants.CENTER);
        lblCaratula.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        lblCaratula.setText("Sin caratula");
        lblCaratula.setForeground(COLOR_TEXTO);

        lblInfoCancion = new JLabel("Seleccione una cancion", SwingConstants.CENTER);
        lblInfoCancion.setForeground(COLOR_TEXTO);
        lblInfoCancion.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel panelProgreso = new JPanel(new BorderLayout(5, 0));
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

        JPanel infoYProgresoPanel = new JPanel(new BorderLayout(0, 10));
        infoYProgresoPanel.setOpaque(false);
        infoYProgresoPanel.add(lblInfoCancion, BorderLayout.NORTH);
        infoYProgresoPanel.add(panelProgreso, BorderLayout.SOUTH);

        panelIzquierdo.add(lblCaratula, BorderLayout.CENTER);
        panelIzquierdo.add(infoYProgresoPanel, BorderLayout.SOUTH);

        listaReproduccionVisual = new JList<>(listModel);
        listaReproduccionVisual.setBackground(COLOR_PANEL);
        listaReproduccionVisual.setForeground(COLOR_TEXTO);
        listaReproduccionVisual.setSelectionBackground(COLOR_SELECCION);
        listaReproduccionVisual.setSelectionForeground(COLOR_TEXTO);
        listaReproduccionVisual.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(listaReproduccionVisual);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        
        panelDerecho.add(scrollPane, BorderLayout.CENTER);
        
        btnPlayPause = new JButton("Play");
        btnStop = new JButton("Stop");
        btnBack = new JButton("Regresar al Menu");

        personalizarBoton(btnPlayPause);
        personalizarBoton(btnStop);
        personalizarBoton(btnBack);

        panelControles.add(btnPlayPause);
        panelControles.add(btnStop);
        panelControles.add(btnBack);
        
        mainPanel.add(panelIzquierdo, BorderLayout.WEST);
        mainPanel.add(panelDerecho, BorderLayout.CENTER);
        mainPanel.add(panelControles, BorderLayout.SOUTH);

        add(mainPanel);
        
        timer = new Timer(1000, e -> actualizarBarraDeProgreso());
        
        actualizarListaVisual();
        configurarListeners();
    }
    
    private void personalizarBoton(JButton button) {
        button.setFont(FONT_BOTON);
        button.setBackground(new Color(75, 75, 75));
        button.setForeground(COLOR_TEXTO);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
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

        btnBack.addActionListener(e -> {
            detenerCancion();
            if (timer.isRunning()) timer.stop();
            new MainMenu().setVisible(true);
            this.dispose();
        });

        listaReproduccionVisual.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                detenerCancion();
                cancionSeleccionadaIndex = listaReproduccionVisual.getSelectedIndex();
                if (cancionSeleccionadaIndex != -1) {
                    mostrarInfoCancion(playlist.obtener(cancionSeleccionadaIndex));
                }
            }
        });
    }

    private void prepararYReproducirCancion() {
        if (cancionSeleccionadaIndex == -1) { 
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una cancion.", "Aviso", JOptionPane.WARNING_MESSAGE); 
            return; 
        }

        if (clip != null && !clip.isRunning() && clipTimePosition > 0) {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
            timer.start();
            btnPlayPause.setText("Pause");
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
                    if (archivoWav == null) throw new Exception("La conversión fallo.");
                    
                    cargarYWavEnClip(archivoWav);
                    clip.setMicrosecondPosition(0);
                    clipTimePosition = 0;
                    
                    barraProgreso.setMaximum((int) (clip.getMicrosecondLength() / 1000));
                    lblTiempoTotal.setText(formatearTiempo(clip.getMicrosecondLength()));
                    
                    clip.start();
                    timer.start(); 
                    mostrarInfoCancion(playlist.obtener(cancionSeleccionadaIndex));
                    btnPlayPause.setText("Pause");
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
            btnPlayPause.setText("Play");
        }
    }

    private void detenerCancion() {
        if (clip != null) {
            clip.stop();
            clipTimePosition = 0;
            clip.setMicrosecondPosition(0);
            timer.stop();
            btnPlayPause.setText("Play");
            barraProgreso.setValue(0);
            lblTiempoActual.setText("00:00");
        }
    }
    
    private void actualizarBarraDeProgreso() {
        if (clip != null && clip.isRunning()) {
            int posicionActualMs = (int) (clip.getMicrosecondPosition() / 1000);
            barraProgreso.setValue(posicionActualMs);
            lblTiempoActual.setText(formatearTiempo(clip.getMicrosecondPosition()));
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
    }
    private void mostrarInfoCancion(Cancion cancion) {
        if (cancion.imagenAlbum != null) { lblCaratula.setIcon(cancion.imagenAlbum); lblCaratula.setText("");
        } else { lblCaratula.setIcon(null); lblCaratula.setText("Sin carátula"); }
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
        if (clip != null) { clip.close(); }
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoWav);
        clip = AudioSystem.getClip();
        clip.open(audioStream);
    }
}