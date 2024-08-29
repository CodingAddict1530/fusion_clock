package com.project.fusion_clock.custom_classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CityCard extends JPanel {

    private Label city;
    private Label time;
    private boolean isHovered = false;
    private Timer timer;
    private int startY;
    private int endY;
    private int currentY;
    private static final int HOVER_OFFSET = 10;
    private static final int DURATION = 300;

    public CityCard(Label city, Label time) {

        this.city = city;
        this.time = time;

        setPreferredSize(new Dimension(100, 15));
        setLayout(new GridBagLayout());
        setOpaque(false);

        city.setFont(new Font("Arial", Font.BOLD, 20));
        time.setFont(new Font("Arial", Font.BOLD, 20));
        city.setBackground(new Color(145, 217, 152));
        time.setBackground(new Color(145, 217, 152));
        city.setForeground(new Color(255, 255, 255));
        time.setForeground(new Color(255, 255, 255));
        city.setPreferredSize(new Dimension(80, 25));
        time.setPreferredSize(new Dimension(80, 25));
        city.setAlignment(Label.CENTER);
        time.setAlignment(Label.CENTER);

        // Create a GridBagConstraints to position labels in the center
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(city, gbc);

        gbc.gridy = 1;
        add(time, gbc);

        // Add mouse listener for hover effect
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                //startAnimation(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {

                //startAnimation(false);
            }

        });

    }

    public Label getCity() {

        return this.city;
    }

    public void setCity(Label city) {

        this.city = city;
    }

    public Label getTime() {

        return this.time;
    }

    public void setTime(Label time) {

        this.time = time;
    }

}
