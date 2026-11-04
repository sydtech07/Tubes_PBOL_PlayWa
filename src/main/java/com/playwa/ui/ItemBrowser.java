package com.playwa.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Daftar item berbentuk grid kartu, dengan tab kategori dan pencarian. Dipakai Inventory, Shop, dan Crafting. */
public class ItemBrowser extends JPanel implements Page {
    public record Entry(String name, String category, String info, Color color, String badge, String price,
                        String button, boolean enabled, Runnable action) {}

    private static final String[] CATS = {"Semua", "Senjata", "Armor", "Makanan", "Potion", "Material"};
    private final Supplier<List<Entry>> source;
    private final JPanel grid = new JPanel(new GridLayout(0, 3, 12, 12));
    private final JTextField search = new JTextField() {
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Theme.smooth(g); g.setColor(Theme.MUTED); g.setFont(Theme.BODY);
                g.drawString("Cari item...", 11, (getHeight() + g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent()) / 2);
            }
        }
    };
    private final List<Theme.Btn> tabs = new ArrayList<>();
    private String cat = "Semua";

    public ItemBrowser(Supplier<List<Entry>> source) {
        super(new BorderLayout(0, 12)); this.source = source; setOpaque(false);
        JPanel bar = new JPanel(new BorderLayout(12, 0)); bar.setOpaque(false);
        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); tabRow.setOpaque(false);
        for (String c : CATS) {
            Theme.Btn b = new Theme.Btn(c, Theme.PANEL2); b.setFont(Theme.SMALL); b.setBorder(new EmptyBorder(6, 14, 6, 14));
            b.addActionListener(e -> { cat = c; refresh(); }); tabs.add(b); tabRow.add(b);
        }
        Theme.style(search); search.setPreferredSize(new Dimension(200, 34));
                search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
        });
        bar.add(tabRow, BorderLayout.CENTER); bar.add(search, BorderLayout.EAST);
        grid.setOpaque(false);
        JPanel top = new Theme.ScrollBody(new BorderLayout()); top.add(grid, BorderLayout.NORTH);
        add(bar, BorderLayout.NORTH); add(Theme.scroll(top), BorderLayout.CENTER);
        refresh();
    }

    @Override public void refresh() {
        for (int i = 0; i < tabs.size(); i++) tabs.get(i).setBase(CATS[i].equals(cat) ? new Color(0x3A4160) : Theme.PANEL2);
        String q = search.getText().trim().toLowerCase();
        grid.removeAll();
        int shown = 0;
        for (Entry e : source.get()) {
            if (!cat.equals("Semua") && !cat.equals(e.category())) continue;
            if (!q.isEmpty() && !e.name().toLowerCase().contains(q)) continue;
            grid.add(card(e)); shown++;
        }
        if (shown == 0) grid.add(Theme.label("Tidak ada item.", Theme.BODY, Theme.MUTED));
        grid.revalidate(); grid.repaint();
    }

    private JPanel card(Entry e) {
        Theme.Card c = new Theme.Card(new BorderLayout(0, 8), Theme.PANEL2, 12).stripe(e.color());
        String head = "<html><b>" + e.name() + "</b>" + (e.badge().isEmpty() ? "" : "  <font color='#E2B44F' size='-1'>" + e.badge() + "</font>") + "</html>";
        JLabel name = Theme.label(head, Theme.BOLD, e.color());
        JLabel info = Theme.label("<html><body style='width:140px'>" + e.info() + "</body></html>", Theme.SMALL, Theme.MUTED);
        JPanel text = new JPanel(); text.setOpaque(false); text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(name); text.add(Box.createVerticalStrut(3)); text.add(info);
        JPanel foot = new JPanel(new BorderLayout()); foot.setOpaque(false);
        foot.add(Theme.label(e.price(), Theme.BOLD, Theme.GOLD), BorderLayout.WEST);
        if (!e.button().isEmpty()) {
            Theme.Btn b = new Theme.Btn(e.button(), new Color(0x2B3142)); b.setFont(Theme.SMALL);
            b.setBorder(new EmptyBorder(5, 14, 5, 14)); b.setEnabled(e.enabled());
            b.addActionListener(ev -> e.action().run()); foot.add(b, BorderLayout.EAST);
        }
        c.add(text, BorderLayout.CENTER); c.add(foot, BorderLayout.SOUTH);
        return c;
    }
}
