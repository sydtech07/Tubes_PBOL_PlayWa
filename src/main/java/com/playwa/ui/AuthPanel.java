
package com.playwa.ui;

import com.playwa.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Form masuk dan daftar, semuanya rata di tengah.
 */
class AuthPanel extends JPanel {

    private final Services s;
    private final Consumer<Player> onSuccess;

    private boolean registerMode;

    private final JTextField user = new JTextField();
    private final JTextField hero = new JTextField();

    private final JPasswordField pass = new JPasswordField();
    private final JPasswordField confirmPass = new JPasswordField();

    private final JLabel subtitle =
            Theme.label("", Theme.BODY, Theme.MUTED);

    private final JLabel error =
            Theme.label(" ", Theme.SMALL, Theme.HP);

    private final JLabel heroLabel =
            Theme.label("Nama karakter", Theme.SMALL, Theme.MUTED);

    private final JLabel confirmLabel =
            Theme.label("Konfirmasi password", Theme.SMALL, Theme.MUTED);

    private final Theme.Btn submit =
            new Theme.Btn("", new Color(0x8A6420));

    private final Theme.Btn toggle =
            new Theme.Btn("", Theme.PANEL);

    private final Theme.Card card =
            new Theme.Card(new GridBagLayout(), Theme.PANEL, 32);

    private int row;

    AuthPanel(Services s, Consumer<Player> onSuccess) {
        super(new GridBagLayout());

        this.s = s;
        this.onSuccess = onSuccess;

        JLabel title = Theme.label(
                "PLAY W.A",
                Theme.TITLE,
                Theme.TEXT
        );

        for (JLabel label : new JLabel[]{title, subtitle, error}) {
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        for (JTextField field : new JTextField[]{
                user, hero, pass, confirmPass
        }) {
            Theme.style(field);
            field.setPreferredSize(new Dimension(300, 40));
            field.addActionListener(e -> submit());
        }

        submit.setPreferredSize(new Dimension(300, 44));

        toggle.setForeground(Theme.GOLD);
        toggle.setFont(Theme.SMALL);

        put(new Emblem(), 0, GridBagConstraints.NONE);

        put(title, 10, GridBagConstraints.HORIZONTAL);
        put(subtitle, 2, GridBagConstraints.HORIZONTAL);

        put(
                Theme.label("Username", Theme.SMALL, Theme.MUTED),
                22,
                GridBagConstraints.HORIZONTAL
        );

        put(user, 5, GridBagConstraints.HORIZONTAL);

        put(heroLabel, 14, GridBagConstraints.HORIZONTAL);
        put(hero, 5, GridBagConstraints.HORIZONTAL);

        put(
                Theme.label("Password", Theme.SMALL, Theme.MUTED),
                14,
                GridBagConstraints.HORIZONTAL
        );

        put(pass, 5, GridBagConstraints.HORIZONTAL);

        put(confirmLabel, 14, GridBagConstraints.HORIZONTAL);
        put(confirmPass, 5, GridBagConstraints.HORIZONTAL);

        put(error, 10, GridBagConstraints.HORIZONTAL);
        put(submit, 6, GridBagConstraints.HORIZONTAL);
        put(toggle, 10, GridBagConstraints.HORIZONTAL);

        add(card);

        submit.addActionListener(e -> submit());

        toggle.addActionListener(e -> {
            registerMode = !registerMode;
            apply();
        });

        apply();
    }

    private void put(Component component, int top, int fill) {
        GridBagConstraints g = new GridBagConstraints();

        g.gridx = 0;
        g.gridy = row++;
        g.weightx = 1;
        g.fill = fill;
        g.anchor = GridBagConstraints.CENTER;
        g.insets = new Insets(top, 0, 0, 0);

        card.add(component, g);
    }

    private void apply() {
        subtitle.setText(
                registerMode
                        ? "Buat petualang barumu"
                        : "Masuk ke dunia Haven"
        );

        submit.setText(
                registerMode ? "Buat akun" : "Masuk"
        );

        toggle.setText(
                registerMode
                        ? "Sudah punya akun? Masuk"
                        : "Belum punya akun? Daftar"
        );

        heroLabel.setVisible(registerMode);
        hero.setVisible(registerMode);

        confirmLabel.setVisible(registerMode);
        confirmPass.setVisible(registerMode);

        error.setText(" ");

        card.revalidate();
        card.repaint();

        revalidate();
        repaint();
    }

    private void submit() {
        try {
            Player p;

            String username = user.getText();
            String password = new String(pass.getPassword());

            if (registerMode) {
                String confirmation =
                        new String(confirmPass.getPassword());

                p = s.auth().register(
                        username,
                        password,
                        confirmation,
                        hero.getText()
                );

            } else {
                Optional<Player> found =
                        s.auth().login(username, password);

                if (found.isEmpty()) {
                    error.setText("Username atau password salah.");
                    return;
                }

                p = found.get();
            }

            onSuccess.accept(p);

        } catch (IllegalArgumentException ex) {
            error.setText(ex.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setPaint(
                new GradientPaint(
                        0,
                        0,
                        new Color(0x1D2233),
                        0,
                        getHeight(),
                        Theme.BG
                )
        );

        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    /**
     * Lambang perisai emas.
     */
    private static class Emblem extends JComponent {

        Emblem() {
            setPreferredSize(new Dimension(64, 70));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            Theme.smooth(g2);

            int x = (getWidth() - 56) / 2;

            Path2D p = new Path2D.Float();

            p.moveTo(x + 28, 3);
            p.lineTo(x + 54, 12);
            p.lineTo(x + 54, 32);
            p.quadTo(x + 54, 54, x + 28, 66);
            p.quadTo(x + 2, 54, x + 2, 32);
            p.lineTo(x + 2, 12);
            p.closePath();

            g2.setPaint(
                    new GradientPaint(
                            0,
                            0,
                            new Color(0x2A2412),
                            0,
                            70,
                            new Color(0x0F0E0A)
                    )
            );

            g2.fill(p);

            g2.setColor(Theme.GOLD);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(p);

            g2.setFont(
                    new Font("Georgia", Font.BOLD, 17)
            );

            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(
                    "W.A",
                    x + 28 - fm.stringWidth("W.A") / 2,
                    40
            );

            g2.dispose();
        }
    }
}
