package com.playwa.ui;

import com.playwa.model.Player;
import com.playwa.service.AccountService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Halaman akun: info, ganti nama (berbayar), reset, dan hapus akun. */
class AccountPage extends JPanel implements Page {
    /** Hal yang dibutuhkan halaman ini dari jendela utama. */
    interface Host {
        Player player();
        Services services();
        void reload();
        void toast(String message);
        void signOut();
    }

    private final Host host;
    private final JLabel username = value(), hero = value(), level = value(), gold = value();
    private final JTextField newName = new JTextField();
    private final Theme.Btn renameBtn = new Theme.Btn("Ganti nama (" + AccountService.RENAME_COST + " gold)", new Color(0x7A5A1E));

    AccountPage(Host host) {
        super(new BorderLayout()); this.host = host; setOpaque(false);

        JPanel column = new JPanel(); column.setOpaque(false); column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.add(infoCard()); column.add(Box.createVerticalStrut(16));
        column.add(renameCard()); column.add(Box.createVerticalStrut(16));
        column.add(dangerCard());

        JPanel body = new Theme.ScrollBody(new BorderLayout());
        body.add(column, BorderLayout.NORTH);
        add(Theme.scroll(body), BorderLayout.CENTER);
    }

    private static JLabel value() { return Theme.label(" ", Theme.serif(Font.BOLD, 19), Theme.TEXT); }

    private Theme.Card box(String caption, JLabel value) {
        Theme.Card c = new Theme.Card(new BorderLayout(0, 3), Theme.PANEL2, 12);
        c.add(Theme.label(caption, Theme.SMALL, Theme.MUTED), BorderLayout.NORTH); c.add(value, BorderLayout.CENTER);
        return c;
    }

    private Theme.Card infoCard() {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 12), Theme.PANEL, 16);
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0)); row.setOpaque(false);
        row.add(box("Username", username)); row.add(box("Nama karakter", hero)); row.add(box("Level", level)); row.add(box("Gold", gold));
        card.add(Theme.label("Informasi Akun", Theme.H2, Theme.TEXT), BorderLayout.NORTH); card.add(row, BorderLayout.CENTER);
        return card;
    }

    private Theme.Card renameCard() {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 10), Theme.PANEL, 16);
        JLabel note = Theme.label("<html>Mengganti nama karakter dikenai biaya <b>" + AccountService.RENAME_COST
            + " gold</b>. Nama 3-20 karakter.</html>", Theme.BODY, Theme.MUTED);
        Theme.style(newName); newName.setPreferredSize(new Dimension(260, 40));
        newName.addActionListener(e -> rename());
        renameBtn.addActionListener(e -> rename());
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); row.setOpaque(false);
        row.add(newName); row.add(Box.createHorizontalStrut(10)); row.add(renameBtn);
        JPanel top = new JPanel(new BorderLayout(0, 6)); top.setOpaque(false);
        top.add(Theme.label("Ganti Nama", Theme.H2, Theme.TEXT), BorderLayout.NORTH); top.add(note, BorderLayout.CENTER);
        card.add(top, BorderLayout.NORTH); card.add(row, BorderLayout.CENTER);
        return card;
    }

    private Theme.Card dangerCard() {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 12), Theme.PANEL, 16).stripe(Theme.HP);
        JPanel row = new JPanel(new GridLayout(1, 2, 12, 0)); row.setOpaque(false);
        row.add(action("Reset akun", "Level, gold, equipment, dan seluruh inventori kembali ke awal. Nama dan password tetap.",
            "Reset akun", new Color(0x7A5A1E), this::reset));
        row.add(action("Hapus akun", "Akun dan semua datanya dihapus permanen. Tidak bisa dikembalikan.",
            "Hapus akun", new Color(0x8A2A32), this::delete));
        card.add(Theme.label("Zona Berbahaya", Theme.H2, Theme.HP), BorderLayout.NORTH); card.add(row, BorderLayout.CENTER);
        return card;
    }

    private Theme.Card action(String title, String desc, String button, Color color, Runnable r) {
        Theme.Card c = new Theme.Card(new BorderLayout(0, 10), Theme.PANEL2, 14);
        Theme.Btn b = new Theme.Btn(button, color); b.addActionListener(e -> r.run());
        c.add(Theme.label(title, Theme.BOLD, Theme.TEXT), BorderLayout.NORTH);
        c.add(Theme.label("<html><body style='width:230px'>" + desc + "</body></html>", Theme.SMALL, Theme.MUTED), BorderLayout.CENTER);
        c.add(b, BorderLayout.SOUTH);
        return c;
    }

    private void rename() {
        String name = newName.getText().trim();
        int ok = JOptionPane.showConfirmDialog(this, "Ganti nama menjadi \"" + name + "\"?\nBiaya " + AccountService.RENAME_COST + " gold.",
            "Ganti nama", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            String msg = host.services().account().renameCharacter(host.player(), name);
            newName.setText(""); host.reload(); host.toast(msg);
        } catch (IllegalArgumentException ex) { fail(ex.getMessage()); }
    }

    private void reset() {
        String pw = askPassword("Reset akun", "Semua progres dan isi inventori akan hilang. Lanjutkan?");
        if (pw == null) return;
        try { String msg = host.services().account().resetAccount(host.player(), pw); host.reload(); host.toast(msg); }
        catch (IllegalArgumentException ex) { fail(ex.getMessage()); }
    }

    private void delete() {
        String pw = askPassword("Hapus akun", "Akun akan dihapus PERMANEN beserta semua datanya. Lanjutkan?");
        if (pw == null) return;
        try {
            host.services().account().deleteAccount(host.player(), pw);
            JOptionPane.showMessageDialog(this, "Akunmu sudah dihapus. Sampai jumpa, petualang!", "Akun dihapus", JOptionPane.INFORMATION_MESSAGE);
            host.signOut();
        } catch (IllegalArgumentException ex) { fail(ex.getMessage()); }
    }

    private String askPassword(String title, String warning) {
        JPasswordField pf = new JPasswordField(); Theme.style(pf);
        Object[] msg = {warning, Box.createVerticalStrut(8), "Masukkan password untuk konfirmasi:", pf};
        int r = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        return r == JOptionPane.OK_OPTION ? new String(pf.getPassword()) : null;
    }

    private void fail(String message) { JOptionPane.showMessageDialog(this, message, "Gagal", JOptionPane.ERROR_MESSAGE); }

    @Override public void refresh() {
        Player p = host.player();
        username.setText(p.getUsername()); hero.setText(p.getName()); level.setText("" + p.getLevel()); gold.setText(p.getGold() + " gold");
        boolean can = p.getGold() >= AccountService.RENAME_COST;
        renameBtn.setEnabled(can);
        renameBtn.setToolTipText(can ? null : "Gold belum cukup");
    }
}
