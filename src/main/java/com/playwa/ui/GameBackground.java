package com.playwa.ui;

import javax.swing.*;
import java.awt.*;

/** Latar game: gradasi gelap dengan cahaya emas lembut di pojok kanan atas. */
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
        g2.setPaint(new GradientPaint(0, 0, new Color(0x1B2030), 0, getHeight(), Theme.BG));
        g2.fillRect(0, 0, getWidth(), getHeight());
        float radius = Math.max(getWidth(), getHeight()) * 0.55f;
        g2.setPaint(new RadialGradientPaint(getWidth() * 0.85f, 0, radius, new float[]{0f, 1f},
            new Color[]{new Color(229, 185, 87, 22), new Color(229, 185, 87, 0)}));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
