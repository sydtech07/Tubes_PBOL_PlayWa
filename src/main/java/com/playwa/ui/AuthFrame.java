package com.playwa.ui;

import javax.swing.*;
import java.awt.*;

public class AuthFrame extends JFrame {

    public AuthFrame(Services s) {
        setTitle("Play W.A | World Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(520, 720);
        setMinimumSize(new Dimension(480, 680));
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(Theme.BG);

        setContentPane(new AuthPanel(s, p -> {
            MainFrame mainFrame = new MainFrame(s, p);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);

            dispose();
        }));
    }
}