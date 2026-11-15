
package com.playwa.ui;

import javax.swing.*;
import java.awt.*;

public class GameBackground extends JPanel {

    public GameBackground(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        Theme.smooth(g2);

        g2.setPaint(
                new GradientPaint(
                        0,
                        0,
                        new Color(0x1B2030),
                        0,
                        getHeight(),
                        Theme.BG
                )
        );

        g2.fillRect(
                0,
                0,
                getWidth(),
                getHeight()
        );

        g2.setColor(new Color(229, 185, 87, 15));

        g2.fillOval(
                getWidth() / 2 - 300,
                getHeight() / 2 - 300,
                600,
                600
        );

        g2.setColor(new Color(229, 185, 87, 35));
        g2.setStroke(new BasicStroke(1.2f));

        g2.drawOval(
                getWidth() / 2 - 260,
                getHeight() / 2 - 260,
                520,
                520
        );

        g2.dispose();
    }
}
