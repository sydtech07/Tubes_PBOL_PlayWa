
package com.playwa.ui;

import com.playwa.dungeon.Dungeon;
import com.playwa.dungeon.DungeonCatalog;
import com.playwa.dungeon.DungeonRun;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        list.setLayout(
                new BoxLayout(list, BoxLayout.Y_AXIS)
        );

        JPanel content = new Theme.ScrollBody(
                new BorderLayout()
        );

        content.setBorder(
                new EmptyBorder(2, 2, 12, 2)
        );

        content.add(list, BorderLayout.NORTH);

        JScrollPane dungeonScroll = Theme.scroll(content);

        dungeonScroll.setBorder(null);
        dungeonScroll.setOpaque(false);
        dungeonScroll.getViewport().setOpaque(false);

        dungeonScroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        dungeonScroll.getVerticalScrollBar().setUnitIncrement(18);

        add(dungeonScroll);
        add(log);
    }

    @Override
    public void refresh() {
        int playerLevel = m.player().getLevel();

        list.removeAll();

        for (Dungeon dungeon : DungeonCatalog.all()) {
            boolean unlocked =
                    playerLevel >= dungeon.getMinLevel();

            list.add(createDungeonCard(dungeon, unlocked));
            list.add(Box.createVerticalStrut(14));
        }

        list.revalidate();
        list.repaint();
    }

    private JPanel createDungeonCard(
            Dungeon dungeon,
            boolean unlocked
    ) {
        Color accent = unlocked
                ? Theme.GOLD
                : Theme.LINE;

        Theme.Card card = new Theme.Card(
                new BorderLayout(12, 12),
                Theme.PANEL2,
                16
        ).stripe(accent);

        JPanel heading = new JPanel(
                new BorderLayout(8, 0)
        );

        heading.setOpaque(false);

        JLabel name = Theme.label(
                dungeon.getName(),
                Theme.H2,
                unlocked ? Theme.TEXT : Theme.MUTED
        );

        Theme.Pill status = new Theme.Pill(
                unlocked ? "TERBUKA" : "TERKUNCI",
                unlocked ? Theme.STA : Theme.MUTED
        );

        heading.add(name, BorderLayout.CENTER);
        heading.add(status, BorderLayout.EAST);

        JTextArea description = textBlock(
                dungeon.getDescription(),
                Theme.BODY,
                Theme.MUTED,
                3
        );

        JPanel information = new JPanel(
                new GridLayout(1, 3, 8, 0)
        );

        information.setOpaque(false);

        information.add(infoBlock(
                "LEVEL MINIMUM",
                String.valueOf(dungeon.getMinLevel()),
                Theme.GOLD
        ));

        information.add(infoBlock(
                "JUMLAH LANTAI",
                String.valueOf(dungeon.getFloors()),
                Theme.TEXT
        ));

        information.add(infoBlock(
                "STAMINA",
                String.valueOf(dungeon.getStaminaCost()),
                Theme.STA
        ));

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(
                new BoxLayout(details, BoxLayout.Y_AXIS)
        );

        details.add(heading);
        details.add(Box.createVerticalStrut(10));
        details.add(description);
        details.add(Box.createVerticalStrut(12));
        details.add(information);
        details.add(Box.createVerticalStrut(12));

        JLabel boss = Theme.label(
                "BOSS  |  " + dungeon.getBossName(),
                Theme.BOLD,
                Theme.HP
        );

        details.add(boss);
        details.add(Box.createVerticalStrut(7));

        JTextArea loot = textBlock(
                "Hadiah: " + dungeon.getLootSummary(),
                Theme.SMALL,
                Theme.MUTED,
                2
        );

        details.add(loot);

        Theme.Btn enter = new Theme.Btn(
                unlocked ? "Masuk Dungeon" : "Belum Terbuka",
                unlocked ? Theme.RED : Theme.PANEL
        );

        enter.setEnabled(unlocked);
        enter.setPreferredSize(
                new Dimension(145, 38)
        );

        enter.addActionListener(e ->
                m.run(new DungeonRun(dungeon), log)
        );

        JPanel footer = new JPanel(
                new BorderLayout()
        );

        footer.setOpaque(false);

        JLabel requirement = Theme.label(
                unlocked
                        ? "Dungeon siap dijelajahi"
                        : "Butuh level " + dungeon.getMinLevel(),
                Theme.SMALL,
                Theme.MUTED
        );

        footer.add(requirement, BorderLayout.CENTER);
        footer.add(enter, BorderLayout.EAST);

        card.add(details, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 280)
        );

        card.setPreferredSize(
                new Dimension(500, 260)
        );

        return card;
    }

    private JPanel infoBlock(
            String title,
            String value,
            Color color
    ) {
        Theme.Card box = new Theme.Card(
                new BorderLayout(0, 5),
                Theme.PANEL,
                10
        );

        JLabel heading = Theme.label(
                title,
                Theme.SMALL,
                Theme.MUTED
        );

        JLabel data = Theme.label(
                value,
                Theme.BOLD,
                color
        );

        box.add(heading, BorderLayout.NORTH);
        box.add(data, BorderLayout.CENTER);

        box.setMinimumSize(new Dimension(0, 55));
        box.setPreferredSize(new Dimension(120, 65));

        return box;
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
