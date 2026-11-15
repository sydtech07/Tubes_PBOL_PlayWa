
package com.playwa.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Daftar item berbentuk kartu.
 * Digunakan oleh halaman Inventory, Shop, dan Crafting.
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

    private final JPanel grid = new JPanel(
            new GridLayout(0, 3, 14, 14)
    );

    private final JTextField search = new JTextField();

    private final List<Theme.Btn> tabs = new ArrayList<>();

    private final JLabel countLabel = Theme.label(
            "0 ITEM",
            Theme.SMALL,
            Theme.MUTED
    );

    private String cat = "Semua";

    public ItemBrowser(Supplier<List<Entry>> source) {
        super(new BorderLayout(0, 16));

        this.source = source;
        setOpaque(false);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(
                new BorderLayout(0, 12)
        );

        header.setOpaque(false);

        JPanel titleArea = new JPanel(
                new BorderLayout()
        );

        titleArea.setOpaque(false);

        JLabel title = Theme.label(
                "KOLEKSI ITEM",
                Theme.H2,
                Theme.TEXT
        );

        JLabel subtitle = Theme.label(
                "Kelola dan temukan item petualanganmu.",
                Theme.SMALL,
                Theme.MUTED
        );

        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(
                new BoxLayout(titleText, BoxLayout.Y_AXIS)
        );

        titleText.add(title);
        titleText.add(Box.createVerticalStrut(4));
        titleText.add(subtitle);

        titleArea.add(titleText, BorderLayout.WEST);
        titleArea.add(countLabel, BorderLayout.EAST);

        JPanel controls = new JPanel();
        controls.setOpaque(false);
        controls.setLayout(
                new BoxLayout(controls, BoxLayout.Y_AXIS)
        );

        JPanel tabRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 7, 0)
        );

        tabRow.setOpaque(false);

        for (String category : CATS) {
            Theme.Btn button = new Theme.Btn(
                    category,
                    Theme.PANEL2
            );

            button.setFont(Theme.SMALL);
            button.setBorder(
                    new EmptyBorder(7, 13, 7, 13)
            );

            button.addActionListener(e -> {
                cat = category;
                refresh();
            });

            tabs.add(button);
            tabRow.add(button);
        }

        Theme.style(search);

        search.setPreferredSize(
                new Dimension(280, 38)
        );

        search.setMaximumSize(
                new Dimension(280, 38)
        );

        search.setToolTipText("Cari nama item");

        search.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        refresh();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        refresh();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e
                    ) {
                        refresh();
                    }
                }
        );

        JPanel searchRow = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );

        searchRow.setOpaque(false);
        searchRow.add(search);

        controls.add(tabRow);
        controls.add(Box.createVerticalStrut(10));
        controls.add(searchRow);

        header.add(titleArea, BorderLayout.NORTH);
        header.add(controls, BorderLayout.CENTER);

        return header;
    }

    private JScrollPane createContent() {
        grid.setOpaque(false);

        JPanel content = new Theme.ScrollBody(
                new BorderLayout()
        );

        content.setBorder(
                new EmptyBorder(2, 2, 12, 2)
        );

        content.add(grid, BorderLayout.NORTH);

        JScrollPane scroll = Theme.scroll(content);

        scroll.setBorder(null);

        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar().setUnitIncrement(18);

        return scroll;
    }

    @Override
    public void refresh() {
        updateTabs();

        String keyword = search.getText()
                .trim()
                .toLowerCase(Locale.ROOT);

        grid.removeAll();

        int shown = 0;

        List<Entry> entries = source.get();

        if (entries != null) {
            for (Entry entry : entries) {
                if (entry == null) {
                    continue;
                }

                if (!matchesCategory(entry)) {
                    continue;
                }

                if (!matchesSearch(entry, keyword)) {
                    continue;
                }

                grid.add(createCard(entry));
                shown++;
            }
        }

        countLabel.setText(shown + " ITEM");

        if (shown == 0) {
            grid.add(createEmptyState());
        }

        grid.revalidate();
        grid.repaint();
    }

    private void updateTabs() {
        for (int i = 0; i < tabs.size(); i++) {
            boolean selected = CATS[i].equals(cat);

            tabs.get(i).setBase(
                    selected
                            ? Theme.GOLD_DARK
                            : Theme.PANEL2
            );

            tabs.get(i).setForeground(
                    selected
                            ? Theme.GOLD
                            : Theme.TEXT
            );
        }
    }

    private boolean matchesCategory(Entry entry) {
        return cat.equals("Semua")
                || cat.equals(entry.category());
    }

    private boolean matchesSearch(
            Entry entry,
            String keyword
    ) {
        if (keyword.isEmpty()) {
            return true;
        }

        String name = safe(entry.name())
                .toLowerCase(Locale.ROOT);

        String info = safe(entry.info())
                .toLowerCase(Locale.ROOT);

        return name.contains(keyword)
                || info.contains(keyword);
    }

    private JPanel createCard(Entry entry) {
        Color accent = entry.color() != null
                ? entry.color()
                : Theme.GOLD;

        Theme.Card card = new Theme.Card(
                new BorderLayout(0, 12),
                Theme.PANEL2,
                15
        ).stripe(accent);

        card.setPreferredSize(
                new Dimension(240, 165)
        );

        JPanel header = new JPanel(
                new BorderLayout(8, 0)
        );

        header.setOpaque(false);

        JLabel name = Theme.label(
                safe(entry.name()),
                Theme.BOLD,
                accent
        );

        header.add(name, BorderLayout.CENTER);

        if (!safe(entry.badge()).isEmpty()) {
            Theme.Pill badge = new Theme.Pill(
                    entry.badge(),
                    Theme.GOLD
            );

            header.add(badge, BorderLayout.EAST);
        }

        JTextArea description = new JTextArea(
                safe(entry.info())
        );

        description.setEditable(false);
        description.setFocusable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setOpaque(false);
        description.setBorder(null);
        description.setFont(Theme.SMALL);
        description.setForeground(Theme.MUTED);
        description.setRows(3);

        JPanel details = new JPanel();
        details.setOpaque(false);

        details.setLayout(
                new BoxLayout(details, BoxLayout.Y_AXIS)
        );

        details.add(header);
        details.add(Box.createVerticalStrut(9));
        details.add(description);

        JPanel footer = new JPanel(
                new BorderLayout(8, 0)
        );

        footer.setOpaque(false);

        JLabel price = Theme.label(
                safe(entry.price()),
                Theme.BOLD,
                Theme.GOLD
        );

        footer.add(price, BorderLayout.WEST);

        if (!safe(entry.button()).isEmpty()) {
            Theme.Btn actionButton = new Theme.Btn(
                    entry.button(),
                    new Color(0x343B4E)
            );

            actionButton.setFont(Theme.SMALL);
            actionButton.setBorder(
                    new EmptyBorder(6, 13, 6, 13)
            );

            actionButton.setEnabled(entry.enabled());

            actionButton.addActionListener(e -> {
                if (entry.action() != null) {
                    entry.action().run();
                }
            });

            footer.add(
                    actionButton,
                    BorderLayout.EAST
            );
        }

        card.add(details, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createEmptyState() {
        Theme.Card empty = new Theme.Card(
                new BorderLayout(0, 8),
                Theme.PANEL,
                24
        );

        JLabel title = Theme.label(
                "ITEM TIDAK DITEMUKAN",
                Theme.BOLD,
                Theme.TEXT
        );

        JLabel subtitle = Theme.label(
                "Coba gunakan kata kunci atau kategori lain.",
                Theme.SMALL,
                Theme.MUTED
        );

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(
                new BoxLayout(text, BoxLayout.Y_AXIS)
        );

        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);

        empty.add(text, BorderLayout.CENTER);

        return empty;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
