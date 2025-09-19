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

public class FondoGUI extends JFrame {

    public FondoGUI() {
        setTitle("Spotitec Background");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(new Color(18, 18, 18));

        JLabel tituloFondo = new JLabel("Spotitec");
        tituloFondo.setFont(new Font("Arial Black", Font.BOLD, 120));
        tituloFondo.setForeground(new Color(30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(tituloFondo, gbc);

        add(backgroundPanel);
        
        setBackground(new Color(0,0,0,0)); 
    }
}