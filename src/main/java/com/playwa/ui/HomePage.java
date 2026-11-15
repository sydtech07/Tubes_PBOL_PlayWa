
package com.playwa.ui;

import com.playwa.activity.Adventure;
import com.playwa.activity.Hunting;
import com.playwa.model.Item;
import com.playwa.model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class HomePage extends JPanel implements Page {

    private final MainFrame m;

    private final JLabel level = Theme.label("", Theme.BIG, Theme.TEXT);
    private final JLabel attack = Theme.label("", Theme.BIG, Theme.TEXT);
    private final JLabel defense = Theme.label("", Theme.BIG, Theme.TEXT);
    private final JLabel gold = Theme.label("", Theme.BIG, Theme.GOLD);

    private final JLabel expText = Theme.label(
            "", Theme.SMALL, Theme.MUTED
    );

    private final JLabel name = Theme.label(
            "", Theme.TITLE, Theme.TEXT
    );

    private final Theme.Bar exp = new Theme.Bar(Theme.EXP);

    private Theme.Btn restBtn;

    private final JPanel equip = new JPanel(
            new GridLayout(2, 2, 8, 8)
    );


    HomePage(MainFrame m, Theme.Log log) {
        super(new BorderLayout(16, 0));

        this.m = m;
        this.currentLog = log;
        setOpaque(false);

        // PANEL KIRI
        Theme.ScrollBody leftContent = new Theme.ScrollBody(
                new BorderLayout(0, 16)
        );

        JPanel leftStack = new JPanel();
        leftStack.setOpaque(false);
        leftStack.setLayout(
                new BoxLayout(leftStack, BoxLayout.Y_AXIS)
        );

        JPanel overview = createOverview();
        JPanel equipment = createEquipment();

        overview.setAlignmentX(Component.LEFT_ALIGNMENT);
        equipment.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftStack.add(overview);
        leftStack.add(Box.createVerticalStrut(16));
        leftStack.add(equipment);

        leftContent.add(leftStack, BorderLayout.NORTH);

        JScrollPane leftScroll = Theme.scroll(leftContent);
        leftScroll.setBorder(null);

        leftScroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        leftScroll.getVerticalScrollBar().setUnitIncrement(18);

        // PANEL KANAN
        JPanel right = new JPanel(new BorderLayout(0, 16));
        right.setOpaque(false);

        right.add(createActions(), BorderLayout.NORTH);
        right.add(log, BorderLayout.CENTER);

        add(leftScroll, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        setPreferredSize(new Dimension(900, 600));
    }

    // =========================
    // OVERVIEW KARAKTER
    // =========================

    private JPanel createOverview() {
        Theme.Card card = new Theme.Card(
                new BorderLayout(0, 18),
                Theme.PANEL,
                20
        );

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(
                titlePanel,
                BoxLayout.Y_AXIS
        ));

        titlePanel.add(Theme.label(
                "CHARACTER OVERVIEW",
                Theme.SMALL,
                Theme.GOLD
        ));

        titlePanel.add(Box.createVerticalStrut(7));
        titlePanel.add(name);

        heading.add(titlePanel, BorderLayout.WEST);

        card.add(heading, BorderLayout.NORTH);

        // STATISTIK
        JPanel stats = new JPanel(
                new GridLayout(2, 2, 10, 10)
        );
        stats.setOpaque(false);

        stats.add(statCard(
                "ATTACK",
                attack,
                Theme.HP,
                "ATK"
        ));

        stats.add(statCard(
                "DEFENSE",
                defense,
                Theme.BLUE,
                "DEF"
        ));

        stats.add(statCard(
                "LEVEL",
                level,
                Theme.EXP,
                "LV"
        ));

        stats.add(statCard(
                "GOLD",
                gold,
                Theme.GOLD_DARK,
                "G"
        ));

        card.add(stats, BorderLayout.CENTER);

        // EXPERIENCE
        JPanel experience = new JPanel(
                new BorderLayout(0, 9)
        );
        experience.setOpaque(false);

        JPanel expHeading = new JPanel(
                new BorderLayout()
        );
        expHeading.setOpaque(false);

        expHeading.add(
                Theme.label(
                        "EXPERIENCE",
                        Theme.SMALL,
                        Theme.MUTED
                ),
                BorderLayout.WEST
        );

        expHeading.add(expText, BorderLayout.EAST);

        exp.setPreferredSize(new Dimension(100, 10));

        experience.add(expHeading, BorderLayout.NORTH);
        experience.add(exp, BorderLayout.CENTER);

        card.add(experience, BorderLayout.SOUTH);

        return card;
    }

    private Theme.Card statCard(
            String title,
            JLabel value,
            Color accent,
            String symbol
    ) {
        Theme.Card card = new Theme.Card(
                new BorderLayout(0, 8),
                Theme.PANEL2,
                13
        );

        JPanel heading = new JPanel(
                new BorderLayout()
        );
        heading.setOpaque(false);

        JLabel label = Theme.label(
                title,
                Theme.SMALL,
                Theme.MUTED
        );

        JLabel icon = Theme.label(
                symbol,
                Theme.BOLD,
                accent
        );

        heading.add(label, BorderLayout.WEST);
        heading.add(icon, BorderLayout.EAST);

        value.setForeground(
                title.equals("GOLD") ? Theme.GOLD : Theme.TEXT
        );

        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        card.stripe(accent);

        return card;
    }

    // =========================
    // EQUIPMENT
    // =========================

    private JPanel createEquipment() {
        Theme.Card card = new Theme.Card(
                new BorderLayout(0, 14),
                Theme.PANEL,
                18
        );

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        heading.add(
                Theme.label(
                        "Equipment",
                        Theme.H2,
                        Theme.TEXT
                ),
                BorderLayout.WEST
        );

        heading.add(
                new Theme.Pill("4 SLOTS", Theme.GOLD),
                BorderLayout.EAST
        );

        equip.setOpaque(false);
        equip.setLayout(new GridLayout(2, 2, 10, 10));

        card.add(heading, BorderLayout.NORTH);
        card.add(equip, BorderLayout.CENTER);

        card.setPreferredSize(new Dimension(600, 190));
        card.setMinimumSize(new Dimension(300, 180));

        return card;
    }

    private Theme.Card slot(
            String key,
            String label,
            String itemName
    ) {
        Theme.Card card = new Theme.Card(
                new BorderLayout(8, 0),
                Theme.PANEL2,
                13
        );

        JPanel content = new JPanel(
                new BorderLayout(0, 4)
        );
        content.setOpaque(false);

        JLabel slotTitle = Theme.label(
                label.toUpperCase(),
                Theme.SMALL,
                Theme.MUTED
        );

        content.add(slotTitle, BorderLayout.NORTH);

        if (itemName == null) {
            card.dashed();

            JLabel empty = Theme.label(
                    "Kosong",
                    Theme.BODY,
                    Theme.MUTED
            );

            content.add(empty, BorderLayout.CENTER);

        } else {
            Item item = m.services()
                    .items()
                    .findByName(itemName)
                    .orElse(null);

            Color rarityColor = item == null
                    ? Theme.TEXT
                    : Theme.rarity(item.getRarity());

            JLabel itemLabel = Theme.label(
                    itemName,
                    Theme.BOLD,
                    rarityColor
            );

            content.add(itemLabel, BorderLayout.CENTER);

            String description = item == null
                    ? itemName
                    : itemName
                    + " | " + item.getDescription()
                    + " | Rarity: " + item.getRarity().name();

            card.setToolTipText(description);
            itemLabel.setToolTipText(description);

            card.stripe(rarityColor);

            Theme.Btn remove = new Theme.Btn(
                    "Lepas",
                    Theme.RED
            );

            remove.setFont(Theme.SMALL);
            remove.setBorder(new EmptyBorder(4, 8, 4, 8));
            remove.setToolTipText("Lepas equipment");

            remove.addActionListener(e -> {
                Player player = m.player();

                player.unequip(key);

                m.services().players().save(player);
                m.reload();
            });

            JPanel buttonPanel = new JPanel(
                    new BorderLayout()
            );
            buttonPanel.setOpaque(false);
            buttonPanel.add(remove, BorderLayout.CENTER);

            card.add(buttonPanel, BorderLayout.EAST);
        }

        card.add(content, BorderLayout.CENTER);

        return card;
    }



    // =========================
    // QUICK ACTIONS
    // =========================

    private JPanel createActions() {
        Theme.Card card = new Theme.Card(
                new BorderLayout(0, 13),
                Theme.PANEL,
                16
        );

        card.add(
                Theme.label(
                        "Quick Actions",
                        Theme.H2,
                        Theme.TEXT
                ),
                BorderLayout.NORTH
        );

        JPanel buttons = new JPanel(
                new GridLayout(3, 1, 0, 9)
        );
        buttons.setOpaque(false);

        buttons.add(createAction(
                "Explore",
                "Jelajahi area dan kumpulkan material",
                Theme.GREEN,
                e -> m.run(new Adventure(), getLog())
        ));

        buttons.add(createAction(
                "Hunt",
                "Berburu monster dan dapatkan hadiah",
                Theme.RED,
                e -> m.run(new Hunting(), getLog())
        ));

        restBtn = createAction(
                "Rest",
                "Pulihkan stamina dan kondisi karakter",
                Theme.BLUE,
                e -> m.rest(getLog())
        );

        buttons.add(restBtn);

        card.add(buttons, BorderLayout.CENTER);

        return card;
    }

    private Theme.Log currentLog;

    private Theme.Log getLog() {
        return currentLog;
    }

    private Theme.Btn createAction(
            String title,
            String description,
            Color color,
            java.awt.event.ActionListener listener
    ) {
        Theme.Btn button = new Theme.Btn(
                title + "   |   " + description,
                color
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setBorder(
                new EmptyBorder(13, 15, 13, 15)
        );

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 55)
        );

        button.addActionListener(listener);

        return button;
    }

    // =========================
    // REST STATUS
    // =========================

    void setResting(boolean on, String info) {
        if (restBtn == null) return;

        if (on) {
            restBtn.setText("Berhenti  |  " + info);
        } else {
            restBtn.setText(
                    "Rest   |   Pulihkan stamina dan kondisi karakter"
            );
        }
    }

    // =========================
    // REFRESH DATA
    // =========================

    @Override
    public void refresh() {
        Player player = m.player();

        name.setText(player.getName());

        attack.setText(
                String.valueOf(player.getTotalAttack())
        );

        defense.setText(
                String.valueOf(player.getTotalDefense())
        );

        level.setText(
                String.valueOf(player.getLevel())
        );

        gold.setText(
                String.valueOf(player.getGold())
        );

        expText.setText(
                player.getExp() +
                        " / " +
                        player.expToNextLevel()
        );

        exp.set(
                player.getExp(),
                player.expToNextLevel()
        );

        equip.removeAll();

        equip.add(slot(
                "weapon",
                "Senjata",
                player.getWeaponName()
        ));

        equip.add(slot(
                "helmet",
                "Helm",
                player.getHelmetName()
        ));

        equip.add(slot(
                "chest",
                "Zirah",
                player.getChestName()
        ));

        equip.add(slot(
                "boots",
                "Sepatu",
                player.getBootsName()
        ));

        equip.revalidate();
        equip.repaint();
    }
}
