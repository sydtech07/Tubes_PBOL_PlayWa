package com.playwa.ui;

import com.playwa.model.Player;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Optional;
import java.util.function.Consumer;

/** Form masuk dan daftar, semuanya rata di tengah. */
class AuthPanel extends JPanel {
    private final Services s;
    private final Consumer<Player> onSuccess;
    private boolean registerMode;
    private final JTextField user = new JTextField(), hero = new JTextField();
    private final JPasswordField pass = new JPasswordField(), confirm = new JPasswordField();
    private final JLabel subtitle = Theme.label("", Theme.BODY, Theme.MUTED), error = Theme.label(" ", Theme.SMALL, Theme.HP),
            heroLabel = Theme.label("Nama karakter", Theme.SMALL, Theme.MUTED),
            confirmLabel = Theme.label("Konfirmasi password", Theme.SMALL, Theme.MUTED);
    private final Theme.Btn submit = new Theme.Btn("", new Color(0x8A6420)), toggle = new Theme.Btn("", Theme.PANEL);
    private final Theme.Card card = new Theme.Card(new GridBagLayout(), Theme.PANEL, 32);
    private int row;

    AuthPanel(Services s, Consumer<Player> onSuccess) {
        super(new GridBagLayout()); this.s = s; this.onSuccess = onSuccess;
        JLabel title = Theme.label("PLAY W.A", Theme.TITLE, Theme.TEXT);
        for (JLabel l : new JLabel[]{title, subtitle, error}) l.setHorizontalAlignment(SwingConstants.CENTER);
        for (JTextField f : new JTextField[]{user, hero, pass, confirm}) { Theme.style(f); f.setPreferredSize(new Dimension(300, 40)); f.addActionListener(e -> submit()); }
        submit.setPreferredSize(new Dimension(300, 44)); toggle.setForeground(Theme.GOLD); toggle.setFont(Theme.SMALL);

        put(new Emblem(), 0, GridBagConstraints.NONE); put(title, 10, GridBagConstraints.HORIZONTAL);
        put(subtitle, 2, GridBagConstraints.HORIZONTAL);
        put(Theme.label("Username", Theme.SMALL, Theme.MUTED), 22, GridBagConstraints.HORIZONTAL); put(user, 5, GridBagConstraints.HORIZONTAL);
        put(heroLabel, 14, GridBagConstraints.HORIZONTAL); put(hero, 5, GridBagConstraints.HORIZONTAL);
        put(Theme.label("Password", Theme.SMALL, Theme.MUTED), 14, GridBagConstraints.HORIZONTAL); put(pass, 5, GridBagConstraints.HORIZONTAL);
        put(confirmLabel, 14, GridBagConstraints.HORIZONTAL); put(confirm, 5, GridBagConstraints.HORIZONTAL);
        put(error, 10, GridBagConstraints.HORIZONTAL); put(submit, 6, GridBagConstraints.HORIZONTAL); put(toggle, 10, GridBagConstraints.HORIZONTAL);
        add(card);

        submit.addActionListener(e -> submit());
        toggle.addActionListener(e -> { registerMode = !registerMode; apply(); });
        apply();
    }

    private void put(Component c, int top, int fill) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = row++; g.weightx = 1; g.fill = fill; g.anchor = GridBagConstraints.CENTER; g.insets = new Insets(top, 0, 0, 0);
        card.add(c, g);
    }

    private void apply() {
        subtitle.setText(registerMode ? "Buat petualang barumu" : "Masuk ke dunia Haven");
        submit.setText(registerMode ? "Buat akun" : "Masuk");
        toggle.setText(registerMode ? "Sudah punya akun? Masuk" : "Belum punya akun? Daftar");
        heroLabel.setVisible(registerMode); hero.setVisible(registerMode);
        confirmLabel.setVisible(registerMode); confirm.setVisible(registerMode); confirm.setText("");
        error.setText(" ");
        card.revalidate(); revalidate(); repaint();
    }

    private void submit() {
        try {
            Player p;
            if (registerMode) p = s.auth().register(user.getText(), new String(pass.getPassword()), new String(confirm.getPassword()), hero.getText());
            else {
                Optional<Player> found = s.auth().login(user.getText(), new String(pass.getPassword()));
                if (found.isEmpty()) { error.setText("Username atau password salah."); return; }
                p = found.get();
            }
            onSuccess.accept(p);
        } catch (IllegalArgumentException ex) { error.setText(ex.getMessage()); }
    }

    @Override protected void paintComponent(Graphics g) {
        ((Graphics2D) g).setPaint(new GradientPaint(0, 0, new Color(0x1D2233), 0, getHeight(), Theme.BG));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    /** Lambang perisai emas. */
    private static class Emblem extends JComponent {
        Emblem() { setPreferredSize(new Dimension(64, 70)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); Theme.smooth(g2);
            int x = (getWidth() - 56) / 2;
            Path2D p = new Path2D.Float(); p.moveTo(x + 28, 3); p.lineTo(x + 54, 12); p.lineTo(x + 54, 32);
            p.quadTo(x + 54, 54, x + 28, 66); p.quadTo(x + 2, 54, x + 2, 32); p.lineTo(x + 2, 12); p.closePath();
            g2.setPaint(new GradientPaint(0, 0, new Color(0x2A2412), 0, 70, new Color(0x0F0E0A))); g2.fill(p);
            g2.setColor(Theme.GOLD); g2.setStroke(new BasicStroke(2f)); g2.draw(p);
            g2.setFont(new Font("Georgia", Font.BOLD, 17)); FontMetrics fm = g2.getFontMetrics();
            g2.drawString("W.A", x + 28 - fm.stringWidth("W.A") / 2, 40); g2.dispose();
        }
    }
}