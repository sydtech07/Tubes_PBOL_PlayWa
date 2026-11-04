package com.playwa.ui;

import javax.swing.*;
import java.awt.*;

/** Jendela masuk/daftar; setelah berhasil membuka MainFrame. */
public class AuthFrame extends JFrame {
    public AuthFrame(Services s) {
        setTitle("Play W.A"); setSize(500, 700); setMinimumSize(new Dimension(460, 680));
        setDefaultCloseOperation(EXIT_ON_CLOSE); setLocationRelativeTo(null);
        setContentPane(new AuthPanel(s, p -> { new MainFrame(s, p).setVisible(true); dispose(); }));
    }
}
