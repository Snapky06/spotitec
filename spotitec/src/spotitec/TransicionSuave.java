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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

public class TransicionSuave {

    public static void fadeIn(JFrame frame) {
        frame.setOpacity(0f);
        frame.setVisible(true);

        Timer timer = new Timer(15, new ActionListener() {
            private float opacity = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    ((Timer) e.getSource()).stop();
                }
                frame.setOpacity(opacity);
            }
        });
        timer.start();
    }

    public static void fadeOut(JFrame ventanaActual, Runnable proximaAccion) {
        Timer timer = new Timer(15, new ActionListener() {
            private float opacity = 1f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    ((Timer) e.getSource()).stop();
                    ventanaActual.dispose();
                    
                    if (proximaAccion != null) {
                        proximaAccion.run();
                    }
                }
                ventanaActual.setOpacity(opacity);
            }
        });
        timer.start();
    }
}
