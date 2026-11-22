package com.playwa.ui;

import com.playwa.activity.Adventure;
import com.playwa.activity.Hunting;
import com.playwa.model.Item;
import com.playwa.model.Player;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Beranda: status karakter, equipment, aksi cepat, dan catatan petualangan. */
class HomePage extends JPanel implements Page {
    private static final String REST_DESC = "Pulihkan stamina dan HP";
    private static final Color DEF_BLUE = new Color(0x6EA8FF);

    private final MainFrame m;
    private final Theme.Log log;
    private final JLabel name = Theme.label("", Theme.serif(Font.BOLD, 30), Theme.TEXT);
    private final JLabel attack = stat(), defense = stat(), level = stat(), gold = stat();
    private final JLabel expText = Theme.label("", Theme.SMALL, Theme.MUTED);
    private final JLabel equipCount = Theme.label("", Theme.SMALL, Theme.GOLD);
    private final Theme.Bar exp = new Theme.Bar(Theme.EXP);
    private final JPanel equip = new JPanel(new GridLayout(2, 2, 12, 12));
    private final Theme.Btn restBtn;

    HomePage(MainFrame m, Theme.Log log) {
        super(new BorderLayout(18, 0));
        this.m = m; this.log = log; setOpaque(false);
        restBtn = action("Rest", REST_DESC, Theme.BLUE, e -> m.rest(log));

        JPanel left = new JPanel(new BorderLayout(0, 16)); left.setOpaque(false);
        left.add(overview(), BorderLayout.NORTH); left.add(equipment(), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(0, 16)); right.setOpaque(false);
        right.setPreferredSize(new Dimension(410, 0));
        right.add(actions(), BorderLayout.NORTH); right.add(log, BorderLayout.CENTER);

        add(left, BorderLayout.CENTER); add(right, BorderLayout.EAST);
    }

    private static JLabel stat() { return Theme.label(" ", Theme.BIG, Theme.TEXT); }

    // ---- ringkasan karakter ----
    private JPanel overview() {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 18), Theme.PANEL, 20);
        JPanel head = new JPanel(); head.setOpaque(false); head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(Theme.label("CHARACTER OVERVIEW", Theme.SMALL, Theme.GOLD)); head.add(Box.createVerticalStrut(4)); head.add(name);

        JPanel stats = new JPanel(new GridLayout(1, 4, 10, 0)); stats.setOpaque(false);
        stats.add(statCard("ATTACK", attack, Theme.HP)); stats.add(statCard("DEFENSE", defense, DEF_BLUE));
        stats.add(statCard("LEVEL", level, Theme.EXP)); stats.add(statCard("GOLD", gold, Theme.GOLD));

        JPanel xp = new JPanel(new BorderLayout(0, 8)); xp.setOpaque(false);
        JPanel xpHead = new JPanel(new BorderLayout()); xpHead.setOpaque(false);
        xpHead.add(Theme.label("EXPERIENCE", Theme.SMALL, Theme.MUTED), BorderLayout.WEST); xpHead.add(expText, BorderLayout.EAST);
        exp.setPreferredSize(new Dimension(100, 10));
        xp.add(xpHead, BorderLayout.NORTH); xp.add(exp, BorderLayout.CENTER);

        card.add(head, BorderLayout.NORTH); card.add(stats, BorderLayout.CENTER); card.add(xp, BorderLayout.SOUTH);
        return card;
    }

    private Theme.Card statCard(String title, JLabel value, Color accent) {
        Theme.Card c = new Theme.Card(new BorderLayout(0, 6), Theme.PANEL2, 14).stripe(accent);
        value.setForeground(accent == Theme.GOLD ? Theme.GOLD : Theme.TEXT);
        c.add(Theme.label(title, Theme.SMALL, Theme.MUTED), BorderLayout.NORTH); c.add(value, BorderLayout.CENTER);
        return c;
    }

    // ---- equipment ----
    private JPanel equipment() {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 14), Theme.PANEL, 18);
        JPanel head = new JPanel(new BorderLayout()); head.setOpaque(false);
        head.add(Theme.label("Equipment", Theme.H2, Theme.TEXT), BorderLayout.WEST); head.add(equipCount, BorderLayout.EAST);
        equip.setOpaque(false);
        card.add(head, BorderLayout.NORTH); card.add(equip, BorderLayout.CENTER);
        return card;
    }

    private Theme.Card slot(String key, String label, String itemName) {
        Theme.Card c = new Theme.Card(new BorderLayout(0, 6), Theme.PANEL2, 14);
        JLabel cap = Theme.label(label.toUpperCase(), Theme.SMALL, Theme.MUTED);
        if (itemName == null) {
            c.dashed();
            JPanel mid = new JPanel(); mid.setOpaque(false); mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
            mid.add(Theme.label("Kosong", Theme.serif(Font.ITALIC, 16), Theme.MUTED)); mid.add(Box.createVerticalStrut(3));
            mid.add(Theme.label("Pakai item dari Inventory", Theme.SMALL, new Color(0x5F6676)));
            c.add(cap, BorderLayout.NORTH); c.add(mid, BorderLayout.CENTER);
            return c;
        }
        Item it = m.services().items().findByName(itemName).orElse(null);
        Color col = it == null ? Theme.TEXT : Theme.rarity(it.getRarity());
        c.stripe(col);
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
        top.add(cap, BorderLayout.WEST);
        if (it != null) top.add(Theme.label(it.getRarity().getLabel(), Theme.SMALL, col), BorderLayout.EAST);
        JPanel mid = new JPanel(); mid.setOpaque(false); mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        mid.add(Theme.label(itemName, Theme.serif(Font.BOLD, 17), col)); mid.add(Box.createVerticalStrut(3));
        if (it != null) mid.add(Theme.label(it.getDescription(), Theme.SMALL, Theme.MUTED));
        Theme.Btn remove = new Theme.Btn("Lepas", Theme.RED); remove.setFont(Theme.SMALL);
        remove.setBorder(new EmptyBorder(4, 12, 4, 12));
        remove.addActionListener(e -> { Player p = m.player(); p.unequip(key); m.services().players().save(p); m.reload(); });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); bottom.setOpaque(false); bottom.add(remove);
        c.add(top, BorderLayout.NORTH); c.add(mid, BorderLayout.CENTER); c.add(bottom, BorderLayout.SOUTH);
        return c;
    }

    // ---- aksi cepat ----
    private JPanel actions() {
        Theme.Card card = new Theme.Card(new BorderLayout(0, 12), Theme.PANEL, 16);
        JPanel list = new JPanel(new GridLayout(3, 1, 0, 10)); list.setOpaque(false);
        list.add(action("Explore", "Jelajahi area dan kumpulkan material", Theme.GREEN, e -> m.run(new Adventure(), log)));
        list.add(action("Hunt", "Berburu monster dan dapatkan hadiah", Theme.RED, e -> m.run(new Hunting(), log)));
        list.add(restBtn);
        card.add(Theme.label("Quick Actions", Theme.H2, Theme.TEXT), BorderLayout.NORTH); card.add(list, BorderLayout.CENTER);
        return card;
    }

    private static String html(String title, String desc) {
        return "<html><b style='font-size:13px'>" + title + "</b><br><span style='font-size:10px;color:#B8C0CC'>" + desc + "</span></html>";
    }

    private Theme.Btn action(String title, String desc, Color color, java.awt.event.ActionListener l) {
        Theme.Btn b = new Theme.Btn(html(title, desc), color);
        b.setHorizontalAlignment(SwingConstants.LEFT); b.setBorder(new EmptyBorder(11, 16, 11, 16));
        b.addActionListener(l);
        return b;
    }

    /** Dipanggil MainFrame: tombol Rest berubah jadi "Berhenti" selama istirahat berlangsung. */
    void setResting(boolean on, String info) {
        restBtn.setText(on ? html("Berhenti", info) : html("Rest", REST_DESC));
    }

    @Override public void refresh() {
        Player p = m.player();
        name.setText(p.getName()); attack.setText("" + p.getTotalAttack()); defense.setText("" + p.getTotalDefense());
        level.setText("" + p.getLevel()); gold.setText("" + p.getGold());
        expText.setText(p.getExp() + " / " + p.expToNextLevel()); exp.set(p.getExp(), p.expToNextLevel());
        String[] names = {p.getWeaponName(), p.getHelmetName(), p.getChestName(), p.getBootsName()};
        String[] keys = {"weapon", "helmet", "chest", "boots"}, labels = {"Senjata", "Helm", "Zirah", "Sepatu"};
        equip.removeAll(); int worn = 0;
        for (int i = 0; i < 4; i++) { equip.add(slot(keys[i], labels[i], names[i])); if (names[i] != null) worn++; }
        equipCount.setText(worn + " / 4 TERPASANG");
        equip.revalidate(); equip.repaint();
    }
}
