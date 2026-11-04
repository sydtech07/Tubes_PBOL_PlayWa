package com.playwa.ui;

import com.playwa.activity.Adventure;
import com.playwa.activity.Hunting;
import com.playwa.model.Item;
import com.playwa.model.Player;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Beranda: status karakter, equipment, dan aksi cepat (Explore, Hunt, Rest). */
class HomePage extends JPanel implements Page {
    private final MainFrame m;
    private final JLabel level = Theme.label("", Theme.BIG, Theme.TEXT), attack = Theme.label("", Theme.BIG, Theme.TEXT),
        defense = Theme.label("", Theme.BIG, Theme.TEXT), gold = Theme.label("", Theme.BIG, Theme.GOLD),
        expText = Theme.label("", Theme.SMALL, Theme.MUTED), name = Theme.label("", Theme.TITLE, Theme.TEXT);
    private final Theme.Bar exp = new Theme.Bar(Theme.EXP);
    private final JPanel equip = new JPanel(new GridLayout(2, 2, 10, 10));

    HomePage(MainFrame m, Theme.Log log) {
        super(new GridLayout(1, 2, 16, 0)); this.m = m; setOpaque(false);

        Theme.Card stats = new Theme.Card(new BorderLayout(0, 14), Theme.PANEL, 16);
        JPanel boxes = new JPanel(new GridLayout(1, 4, 8, 0)); boxes.setOpaque(false);
        boxes.add(box("Attack", Theme.RED, attack)); boxes.add(box("Defense", Theme.BLUE, defense));
        boxes.add(box("Level", Theme.PURPLE, level)); boxes.add(box("Gold", new Color(0x35290F), gold));
        JPanel expRow = new JPanel(new BorderLayout(0, 5)); expRow.setOpaque(false);
        expRow.add(expText, BorderLayout.NORTH); expRow.add(exp, BorderLayout.CENTER);
        stats.add(name, BorderLayout.NORTH); stats.add(boxes, BorderLayout.CENTER); stats.add(expRow, BorderLayout.SOUTH);

        Theme.Card eq = new Theme.Card(new BorderLayout(0, 10), Theme.PANEL, 16);
        equip.setOpaque(false);
        eq.add(Theme.label("Equipment", Theme.H2, Theme.TEXT), BorderLayout.NORTH); eq.add(equip, BorderLayout.CENTER);

        JPanel left = new JPanel(new BorderLayout(0, 16)); left.setOpaque(false);
        left.add(stats, BorderLayout.NORTH); left.add(eq, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 3, 10, 0)); actions.setOpaque(false);
        actions.add(action("Explore", "10 stamina", new Color(0x1E5238), e -> m.run(new Adventure(), log)));
        actions.add(action("Hunt", "15 stamina", new Color(0x7A2A30), e -> m.run(new Hunting(), log)));
        actions.add(action("Rest", "pulihkan diri", new Color(0x2C4678), e -> m.rest(log)));
        JPanel right = new JPanel(new BorderLayout(0, 16)); right.setOpaque(false);
        right.add(actions, BorderLayout.NORTH); right.add(log, BorderLayout.CENTER);

        add(left); add(right);
    }

    private Theme.Card box(String caption, Color tint, JLabel value) {
        Theme.Card c = new Theme.Card(new BorderLayout(0, 2), tint, 10);
        value.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel cap = Theme.label(caption, Theme.SMALL, Theme.MUTED); cap.setHorizontalAlignment(SwingConstants.CENTER);
        c.add(value, BorderLayout.CENTER); c.add(cap, BorderLayout.SOUTH);
        return c;
    }

    private Theme.Btn action(String title, String sub, Color color, java.awt.event.ActionListener l) {
        Theme.Btn b = new Theme.Btn("<html><div style='text-align:center'><span style='font-size:15px'><b>" + title
            + "</b></span><br><span style='font-size:10px;color:#C9C6BE'>" + sub + "</span></div></html>", color);
        b.setBorder(new EmptyBorder(16, 8, 16, 8)); b.addActionListener(l);
        return b;
    }

    private Theme.Card slot(String key, String label, String itemName) {
        Theme.Card c = new Theme.Card(new BorderLayout(), Theme.PANEL2, 10);
        JPanel t = new JPanel(); t.setOpaque(false); t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(Theme.label(label, Theme.SMALL, Theme.MUTED)); t.add(Box.createVerticalStrut(4));
        if (itemName == null) {
            c.dashed(); t.add(Theme.label("Kosong", Theme.BODY, new Color(0x5A5E6A)));
        } else {
            Item it = m.services().items().findByName(itemName).orElse(null);
            Color col = it == null ? Theme.TEXT : Theme.rarity(it.getRarity());
            t.add(Theme.label(itemName, Theme.BOLD, col));
            if (it != null) t.add(Theme.label(it.getDescription(), Theme.SMALL, Theme.MUTED));
            c.stripe(col);
            Theme.Btn x = new Theme.Btn("x", Theme.PANEL2); x.setFont(Theme.SMALL); x.setBorder(new EmptyBorder(2, 8, 2, 8));
            x.setToolTipText("Lepas");
            x.addActionListener(e -> { Player p = m.player(); p.unequip(key); m.services().players().save(p); m.reload(); });
            JPanel east = new JPanel(new BorderLayout()); east.setOpaque(false); east.add(x, BorderLayout.NORTH);
            c.add(east, BorderLayout.EAST);
        }
        c.add(t, BorderLayout.CENTER);
        return c;
    }

    @Override public void refresh() {
        Player p = m.player();
        name.setText(p.getName()); attack.setText("" + p.getTotalAttack()); defense.setText("" + p.getTotalDefense());
        level.setText("" + p.getLevel()); gold.setText("" + p.getGold());
        expText.setText("Experience  " + p.getExp() + " / " + p.expToNextLevel()); exp.set(p.getExp(), p.expToNextLevel());
        equip.removeAll();
        equip.add(slot("weapon", "Senjata", p.getWeaponName())); equip.add(slot("helmet", "Helm", p.getHelmetName()));
        equip.add(slot("chest", "Zirah", p.getChestName())); equip.add(slot("boots", "Sepatu", p.getBootsName()));
        equip.revalidate(); equip.repaint();
    }
}
