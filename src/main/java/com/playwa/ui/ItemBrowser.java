
package com.playwa.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Daftar item berbentuk grid kartu, dengan tab kategori dan pencarian.
 * Dipakai Inventory, Shop, dan Crafting.
 */
public class ItemBrowser extends JPanel implements Page {

    public record Entry(
            String name,
            String category,
            String info,
            Color color,
            String badge,
            String price,
            String button,
            boolean enabled,
            Runnable action
    ) {}

    private static final String[] CATS = {
            "Semua",
            "Senjata",
            "Armor",
            "Makanan",
            "Potion",
            "Material"
    };

    private final Supplier<List<Entry>> source;

    private final JPanel grid =
            new JPanel(new GridLayout(0, 3, 12, 12));

    private final JTextField search = new JTextField() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty()) {
                Theme.smooth(g);
                g.setColor(Theme.MUTED);
                g.setFont(Theme.BODY);

                g.drawString(
                        "Cari item...",
                        11,
                        (getHeight()
                                + g.getFontMetrics().getAscent()
                                - g.getFontMetrics().getDescent()) / 2
                );
            }
        }
    };

    private final List<Theme.Btn> tabs = new ArrayList<>();

    private String cat = "Semua";

    public ItemBrowser(Supplier<List<Entry>> source) {
        super(new BorderLayout(0, 12));

        this.source = source;
        setOpaque(false);

        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);

        JPanel tabRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 6, 0)
        );
        tabRow.setOpaque(false);

        for (String c : CATS) {
            Theme.Btn b = new Theme.Btn(c, Theme.PANEL2);

            b.setFont(Theme.SMALL);
            b.setBorder(new EmptyBorder(6, 14, 6, 14));

            b.addActionListener(e -> {
                cat = c;
                refresh();
            });

            tabs.add(b);
            tabRow.add(b);
        }

        Theme.style(search);
        search.setPreferredSize(new Dimension(200, 34));

        search.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        refresh();
                    }

                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        refresh();
                    }

                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        refresh();
                    }
                }
        );

        bar.add(tabRow, BorderLayout.CENTER);
        bar.add(search, BorderLayout.EAST);

        grid.setOpaque(false);

        JPanel top = new Theme.ScrollBody(new BorderLayout());
        top.add(grid, BorderLayout.NORTH);

        add(bar, BorderLayout.NORTH);
        add(Theme.scroll(top), BorderLayout.CENTER);

        refresh();
    }

    @Override
    public void refresh() {
        for (int i = 0; i < tabs.size(); i++) {
            tabs.get(i).setBase(
                    CATS[i].equals(cat)
                            ? new Color(0x3A4160)
                            : Theme.PANEL2
            );
        }

        String q = search.getText()
                .trim()
                .toLowerCase();

        grid.removeAll();

        int shown = 0;

        for (Entry e : source.get()) {

            if (!cat.equals("Semua")
                    && !cat.equals(e.category())) {
                continue;
            }

            if (!q.isEmpty()
                    && !e.name().toLowerCase().contains(q)) {
                continue;
            }

            grid.add(card(e));
            shown++;
        }

        if (shown == 0) {
            grid.add(
                    Theme.label(
                            "Tidak ada item.",
                            Theme.BODY,
                            Theme.MUTED
                    )
            );
        }

        grid.revalidate();
        grid.repaint();
    }

    private JPanel card(Entry e) {

        Theme.Card c = new Theme.Card(
                new BorderLayout(0, 8),
                Theme.PANEL2,
                12
        ).stripe(e.color());

        // Bagian judul dan badge
        JPanel header = new JPanel(new BorderLayout(6, 0));
        header.setOpaque(false);

        JLabel name = Theme.label(
                e.name(),
                Theme.BOLD,
                e.color()
        );

        header.add(name, BorderLayout.CENTER);

        if (!e.badge().isEmpty()) {
            JLabel badge = Theme.label(
                    e.badge(),
                    Theme.SMALL,
                    new Color(0xE2B44F)
            );

            header.add(badge, BorderLayout.EAST);
        }

        // Deskripsi tanpa HTML
        JTextArea info = new JTextArea(e.info());

        info.setEditable(false);
        info.setFocusable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setOpaque(false);
        info.setBorder(null);
        info.setFont(Theme.SMALL);
        info.setForeground(Theme.MUTED);
        info.setRows(3);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(
                new BoxLayout(text, BoxLayout.Y_AXIS)
        );

        text.add(header);
        text.add(Box.createVerticalStrut(5));
        text.add(info);

        // Bagian harga dan tombol
        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);

        JLabel price = Theme.label(
                e.price(),
                Theme.BOLD,
                Theme.GOLD
        );

        foot.add(price, BorderLayout.WEST);

        if (!e.button().isEmpty()) {

            Theme.Btn b = new Theme.Btn(
                    e.button(),
                    new Color(0x2B3142)
            );

            b.setFont(Theme.SMALL);
            b.setBorder(
                    new EmptyBorder(5, 14, 5, 14)
            );

            b.setEnabled(e.enabled());

            b.addActionListener(ev -> {
                if (e.action() != null) {
                    e.action().run();
                }
            });

            foot.add(b, BorderLayout.EAST);
        }

        c.add(text, BorderLayout.CENTER);
        c.add(foot, BorderLayout.SOUTH);

        return c;
    }
}
