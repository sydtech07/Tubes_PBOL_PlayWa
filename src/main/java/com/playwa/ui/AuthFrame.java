package com.playwa.ui;

import com.playwa.model.Player;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

/** Satu jendela untuk masuk dan daftar. */
public class AuthFrame extends JFrame {
    private final Services s;
    private boolean registerMode;
    private final JTextField user = new JTextField(), hero = new JTextField();
    private final JPasswordField pass = new JPasswordField();
    private final JLabel subtitle = Theme.label("", Theme.BODY, Theme.MUTED), error = Theme.label(" ", Theme.SMALL, Theme.HP),
        heroLabel = Theme.label("Nama karakter", Theme.SMALL, Theme.MUTED);
    private final Theme.Btn submit = new Theme.Btn("", new Color(0x7A5A1E)), toggle = new Theme.Btn("", Theme.PANEL);

    public AuthFrame(Services s) {
        this.s = s;
        setTitle("Play W.A"); setSize(480, 620); setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE); setLocationRelativeTo(null);
        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                ((Graphics2D) g).setPaint(new GradientPaint(0, 0, new Color(0x1D2233), 0, getHeight(), Theme.BG));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(bg);

        Theme.Card card = new Theme.Card(new BorderLayout(), Theme.PANEL, 28);
        card.setPreferredSize(new Dimension(380, 480));
        JPanel f = new JPanel(); f.setOpaque(false); f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        JLabel title = Theme.label("PLAY W.A", Theme.TITLE, Theme.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT); subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        f.add(title); f.add(Box.createVerticalStrut(4)); f.add(subtitle); f.add(Box.createVerticalStrut(22));
        field(f, "Username", user); field(f, heroLabel, hero); field(f, "Password", pass);
        error.setAlignmentX(Component.LEFT_ALIGNMENT); f.add(error); f.add(Box.createVerticalStrut(8));
        submit.setAlignmentX(Component.LEFT_ALIGNMENT); submit.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        toggle.setAlignmentX(Component.LEFT_ALIGNMENT); toggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        toggle.setForeground(Theme.GOLD);
        f.add(submit); f.add(Box.createVerticalStrut(6)); f.add(toggle);
        card.add(f, BorderLayout.CENTER); bg.add(card);

        submit.addActionListener(e -> submit());
        toggle.addActionListener(e -> { registerMode = !registerMode; apply(); });
        getRootPane().setDefaultButton(submit);
        apply();
    }

    private void field(JPanel p, String label, JTextField f) { field(p, Theme.label(label, Theme.SMALL, Theme.MUTED), f); }

    private void field(JPanel p, JLabel label, JTextField f) {
        label.setAlignmentX(Component.LEFT_ALIGNMENT); Theme.style(f);
        f.setAlignmentX(Component.LEFT_ALIGNMENT); f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.add(label); p.add(Box.createVerticalStrut(4)); p.add(f); p.add(Box.createVerticalStrut(12));
        if (f == hero) { hero.putClientProperty("label", label); }
    }

    private void apply() {
        subtitle.setText(registerMode ? "Buat petualang barumu" : "Masuk ke dunia Haven");
        submit.setText(registerMode ? "Buat akun" : "Masuk");
        toggle.setText(registerMode ? "Sudah punya akun? Masuk" : "Belum punya akun? Daftar");
        heroLabel.setVisible(registerMode); hero.setVisible(registerMode); error.setText(" ");
    }

    private void submit() {
        try {
            Player p;
            if (registerMode) p = s.auth().register(user.getText(), new String(pass.getPassword()), hero.getText());
            else {
                Optional<Player> found = s.auth().login(user.getText(), new String(pass.getPassword()));
                if (found.isEmpty()) { error.setText("Username atau password salah."); return; }
                p = found.get();
            }
            new MainFrame(s, p).setVisible(true); dispose();
        } catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
    }
}
