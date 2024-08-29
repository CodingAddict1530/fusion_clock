package com.project.fusion_clock.main;

import javax.swing.*;
import java.awt.*;

public class Launcher implements Runnable {

    @Override
    public void run() {

        JFrame frame = Controller.getJFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Fusion Clock");
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        frame.setBackground(new Color(14, 16, 20));
        frame.setVisible(true);
        Controller.awake();

    }

}
