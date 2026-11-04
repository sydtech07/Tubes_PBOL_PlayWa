package com.playwa.ui;

import com.playwa.dungeon.Dungeon;
import com.playwa.dungeon.DungeonCatalog;
import com.playwa.dungeon.DungeonRun;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Daftar dungeon dan laporan hasil penjelajahan. */
class DungeonPage extends JPanel implements Page {
    private final MainFrame m;
    private final Theme.Log log;
    private final JPanel list = new JPanel();

    DungeonPage(MainFrame m, Theme.Log log) {
        super(new GridLayout(1, 2, 16, 0)); this.m = m; this.log = log; setOpaque(false);
        list.setOpaque(false); list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        JPanel top = new Theme.ScrollBody(new BorderLayout()); top.add(list, BorderLayout.NORTH);
        add(Theme.scroll(top)); add(log);
    }

    @Override public void refresh() {
        int level = m.player().getLevel();
        list.removeAll();
        for (Dungeon d : DungeonCatalog.all()) {
            boolean open = level >= d.getMinLevel();
            Theme.Card c = new Theme.Card(new BorderLayout(12, 0), Theme.PANEL, 14).stripe(open ? Theme.GOLD : Theme.LINE);
            JPanel t = new JPanel(); t.setOpaque(false); t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
            t.add(Theme.label(d.getName(), Theme.H2, open ? Theme.TEXT : Theme.MUTED));
            t.add(Theme.label("<html><body style='width:200px'>" + d.getDescription() + "</body></html>", Theme.SMALL, Theme.MUTED));
            t.add(Box.createVerticalStrut(6));
            t.add(Theme.label("Level " + d.getMinLevel() + "+  |  " + d.getFloors() + " lantai  |  " + d.getStaminaCost() + " stamina", Theme.SMALL, Theme.TEXT));
            t.add(Theme.label("Boss: " + d.getBossName(), Theme.SMALL, Theme.HP));
            t.add(Theme.label("<html><body style='width:200px'>Drop: " + d.getLootSummary() + "</body></html>", Theme.SMALL, Theme.MUTED));
            Theme.Btn go = new Theme.Btn(open ? "Masuk" : "Terkunci", new Color(0x7A2A30));
            go.setEnabled(open); go.addActionListener(e -> m.run(new DungeonRun(d), log));
            JPanel east = new JPanel(new BorderLayout()); east.setOpaque(false); east.add(go, BorderLayout.SOUTH);
            c.add(t, BorderLayout.CENTER); c.add(east, BorderLayout.EAST);
            list.add(c); list.add(Box.createVerticalStrut(12));
        }
        list.revalidate(); list.repaint();
    }
}
