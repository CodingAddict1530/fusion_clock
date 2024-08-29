package com.project.fusion_clock.main;

import com.project.fusion_clock.custom_classes.CityCard;
import com.project.fusion_clock.utility.DatabaseUtility;
import com.project.fusion_clock.utility.MainUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    private static final JFrame frame = new JFrame();

    public static void awake() {

        init();
        if (!MainUtility.checkIfTableExists(DatabaseUtility.TABLE_NAME)) {
            try {
                createTable(DatabaseUtility.TABLE_NAME).join();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        if (!MainUtility.checkIfTableExists(DatabaseUtility.TABLE_NAME_DST)) {
            createTable(DatabaseUtility.TABLE_NAME_DST);
        }

    }

    private static void init() {

        frame.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                int width = getWidth();
                int height = getHeight();

                // Create a gradient from blue to green
                GradientPaint gradient = new GradientPaint(0, 0, Color.BLUE, width, height, Color.GREEN);

                // Set the paint of the Graphics2D object
                g2d.setPaint(gradient);

                // Fill the entire panel with the gradient
                g2d.fillRect(0, 0, width, height);

            }

        };

        JLabel place = new JLabel("Ottawa, Canada");
        JLabel time = new JLabel("12:20");
        JLabel date = new JLabel("Thursday, 20th 2024");

        place.setAlignmentX(Component.CENTER_ALIGNMENT);
        time.setAlignmentX(Component.CENTER_ALIGNMENT);
        date.setAlignmentX(Component.CENTER_ALIGNMENT);

        place.setFont(new Font("Arial", Font.PLAIN, 20));
        time.setFont(new Font("Arial", Font.BOLD, 60));
        date.setFont(new Font("Arial", Font.PLAIN, 20));

        place.setForeground(Color.WHITE);
        time.setForeground(Color.WHITE);
        date.setForeground(Color.WHITE);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setOpaque(false);

        labelPanel.add(Box.createVerticalGlue());
        labelPanel.add(place);
        labelPanel.add(Box.createVerticalStrut(10));  // 10px space between labels
        labelPanel.add(time);
        labelPanel.add(Box.createVerticalStrut(10));
        labelPanel.add(date);

        topPanel.setLayout(new BorderLayout());
        topPanel.add(labelPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.CENTER);
        //CityCard cityCard = ;
        //cityCard.setBounds(50, 50, 200, 250);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        for (int i = 0; i < 20; i++) {
            bottomPanel.add(new CityCard(new Label("Texas"), new Label("13:00")));
        }
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(bottomPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUI(new BasicScrollBarUI() {

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(128, 128, 128, 100));
                g2d.fill(thumbBounds);
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // No background
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }

        });

        labelPanel.add(Box.createVerticalStrut(100));
        labelPanel.add(scrollPane);
        labelPanel.add(Box.createVerticalGlue());

    }

    private static Thread createTable(String table) {

        Thread importThread = new Thread(() -> {
            try {
                if (DatabaseUtility.createTable(table) == 0) {
                    logger.info("Created table");
                    if ((table.equals(DatabaseUtility.TABLE_NAME) ?
                            DatabaseUtility.importCSV(DatabaseUtility.CSV_FILE_PATH, table) :
                            DatabaseUtility.importCSV(DatabaseUtility.CSV_FILE_PATH_DST, table)) == 0) {
                        logger.error("Inserted into the table: {}", table);
                    } else {
                        logger.error("Something went wrong while importing from the csv file.");
                    }
                } else {
                    logger.error("Something went wrong while creating the table.");
                }

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });

        importThread.setDaemon(true);
        importThread.start();
        return importThread;

    }

    public static JFrame getJFrame() {

        return frame;
    }

}
