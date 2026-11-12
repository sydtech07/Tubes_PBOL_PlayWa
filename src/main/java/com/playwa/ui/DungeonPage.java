
package com.playwa.ui;

import com.playwa.dungeon.Dungeon;
import com.playwa.dungeon.DungeonCatalog;
import com.playwa.dungeon.DungeonRun;

import javax.swing.*;
import java.awt.*;

class DungeonPage extends JPanel implements Page {

    private final MainFrame m;
    private final Theme.Log log;
    private final JPanel list = new JPanel();

    DungeonPage(MainFrame m, Theme.Log log) {
        super(new GridLayout(1, 2, 16, 0));

        this.m = m;
        this.log = log;

        setOpaque(false);

        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        JPanel top = new Theme.ScrollBody(new BorderLayout());
        top.add(list, BorderLayout.NORTH);

        add(Theme.scroll(top));
        add(log);
    }

    @Override
    public void refresh() {
        int level = m.player().getLevel();

        list.removeAll();

        for (Dungeon d : DungeonCatalog.all()) {

            boolean open = level >= d.getMinLevel();

            Theme.Card c = new Theme.Card(
                    new BorderLayout(12, 0),
                    Theme.PANEL,
                    14
            ).stripe(open ? Theme.GOLD : Theme.LINE);

            JPanel t = new JPanel();
            t.setOpaque(false);
            t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));

            JLabel name = Theme.label(
                    d.getName(),
                    Theme.H2,
                    open ? Theme.TEXT : Theme.MUTED
            );

            JTextArea description = textBlock(
                    d.getDescription(),
                    Theme.SMALL,
                    Theme.MUTED,
                    3
            );

            JLabel details = Theme.label(
                    "Level " + d.getMinLevel()
                            + "  |  " + d.getFloors() + " lantai"
                            + "  |  " + d.getStaminaCost() + " stamina",
                    Theme.SMALL,
                    Theme.TEXT
            );

            JLabel boss = Theme.label(
                    "Boss: " + d.getBossName(),
                    Theme.SMALL,
                    Theme.HP
            );

            JTextArea loot = textBlock(
                    "Drop: " + d.getLootSummary(),
                    Theme.SMALL,
                    Theme.MUTED,
                    2
            );

            t.add(name);
            t.add(Box.createVerticalStrut(5));
            t.add(description);
            t.add(Box.createVerticalStrut(6));
            t.add(details);
            t.add(Box.createVerticalStrut(4));
            t.add(boss);
            t.add(Box.createVerticalStrut(4));
            t.add(loot);

            Theme.Btn go = new Theme.Btn(
                    open ? "Masuk" : "Terkunci",
                    new Color(0x7A2A30)
            );

            go.setEnabled(open);
            go.addActionListener(e -> m.run(new DungeonRun(d), log));

            JPanel east = new JPanel(new BorderLayout());
            east.setOpaque(false);
            east.add(go, BorderLayout.SOUTH);

            c.add(t, BorderLayout.CENTER);
            c.add(east, BorderLayout.EAST);

            list.add(c);
            list.add(Box.createVerticalStrut(12));
        }

        list.revalidate();
        list.repaint();
    }

    private JTextArea textBlock(
            String value,
            Font font,
            Color color,
            int rows
    ) {
        JTextArea area = new JTextArea(value);

        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setBorder(null);

        area.setFont(font);
        area.setForeground(color);
        area.setRows(rows);
        area.setColumns(24);

        return area;
    }
}
