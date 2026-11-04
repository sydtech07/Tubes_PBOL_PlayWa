package com.playwa.ui;

import com.playwa.model.Rarity;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/** Warna, font, dan komponen kustom bertema dark fantasy. */
public final class Theme {
    private Theme() {}
    public static final Color BG = new Color(0x090A0D), SIDE = new Color(0x0C0D11), PANEL = new Color(0x12141A),
        PANEL2 = new Color(0x1A1D26), LINE = new Color(0x2A2E3A), TEXT = new Color(0xE9E6DF), MUTED = new Color(0x8B8F9C),
        HP = new Color(0xE5484D), STA = new Color(0x2BB673), EXP = new Color(0x9B5DE5), GOLD = new Color(0xE2B44F),
        RED = new Color(0x4A1A1E), GREEN = new Color(0x143324), BLUE = new Color(0x1B2A4A), PURPLE = new Color(0x2C1B48);
    public static final Font TITLE = new Font("Georgia", Font.BOLD, 26), H2 = new Font("Georgia", Font.BOLD, 16),
        BODY = new Font("Segoe UI", Font.PLAIN, 13), BOLD = new Font("Segoe UI", Font.BOLD, 13),
        SMALL = new Font("Segoe UI", Font.PLAIN, 12), BIG = new Font("Segoe UI", Font.BOLD, 22);

    public static void install() {
        UIManager.put("Button.disabledText", new Color(0x6A6E7A));
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("Panel.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
    }

    public static Color rarity(Rarity r) {
        return switch (r) {
            case COMMON -> new Color(0xD0D0D0); case UNCOMMON -> new Color(0x5FD38D); case RARE -> new Color(0x5AA2FF);
            case EPIC -> new Color(0xB57BFF); case LEGENDARY -> new Color(0xFFB347);
        };
    }

    static void smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text); l.setFont(font); l.setForeground(color); return l;
    }

    public static void style(JTextField f) {
        f.setFont(BODY); f.setForeground(TEXT); f.setCaretColor(TEXT); f.setBackground(new Color(0x07080A));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE), new EmptyBorder(8, 10, 8, 10)));
    }

    public static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null); sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { thumbColor = LINE; trackColor = new Color(0, 0, 0, 0); }
            @Override protected JButton createDecreaseButton(int o) { return zero(); }
            @Override protected JButton createIncreaseButton(int o) { return zero(); }
            private JButton zero() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }
        });
        return sp;
    }

    /** Isi area scroll yang mengikuti lebar jendela (tidak muncul scroll horizontal). */
    public static class ScrollBody extends JPanel implements Scrollable {
        public ScrollBody(LayoutManager lm) { super(lm); setOpaque(false); }
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
        public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 120; }
        public boolean getScrollableTracksViewportWidth() { return true; }
        public boolean getScrollableTracksViewportHeight() { return false; }
    }

    /** Panel kartu bersudut membulat, dengan opsi garis aksen di kiri dan garis putus-putus. */
    public static class Card extends JPanel {
        private final Color fill; private Color stripe; private boolean dashed;
        public Card(LayoutManager lm, Color fill, int pad) {
            super(lm); this.fill = fill; setOpaque(false); setBorder(new EmptyBorder(pad, pad + 2, pad, pad));
        }
        public Card stripe(Color c) { stripe = c; return this; }
        public Card dashed() { dashed = true; return this; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); smooth(g2);
            RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(dashed ? new Color(0, 0, 0, 0) : fill); g2.fill(shape);
            if (stripe != null) { g2.setClip(shape); g2.setColor(stripe); g2.fillRect(0, 0, 4, getHeight()); g2.setClip(null); }
            g2.setColor(LINE);
            if (dashed) g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{5, 4}, 0));
            g2.draw(shape); g2.dispose();
        }
    }

    public static class Btn extends JButton {
        private Color base; private boolean hover;
        public Btn(String text, Color base) {
            super(text); this.base = base; setFont(BOLD); setForeground(TEXT);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setBorder(new EmptyBorder(8, 16, 8, 16)); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }
        public void setBase(Color c) { base = c; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); smooth(g2);
            Color c = !isEnabled() ? new Color(0x1C1F28) : hover ? base.brighter() : base;
            g2.setColor(c); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12); g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class Bar extends JComponent {
        private final Color color; private double value;
        public Bar(Color color) { this.color = color; setPreferredSize(new Dimension(130, 10)); }
        public void set(int cur, int max) { value = max <= 0 ? 0 : Math.min(1, (double) cur / max); repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); smooth(g2);
            int h = getHeight(), w = getWidth();
            g2.setColor(new Color(0x06070A)); g2.fillRoundRect(0, 0, w, h, h, h);
            if (value > 0) {
                g2.setPaint(new GradientPaint(0, 0, color.darker(), w, 0, color));
                g2.fillRoundRect(0, 0, Math.max(h, (int) (w * value)), h, h, h);
            }
            g2.dispose();
        }
    }

    /** Papan catatan petualangan. */
    public static class Log extends Card {
        private final JTextArea area = new JTextArea();
        public Log(String title) {
            super(new BorderLayout(0, 10), PANEL, 16);
            add(label(title, H2, TEXT), BorderLayout.NORTH);
            area.setEditable(false); area.setLineWrap(true); area.setWrapStyleWord(true); area.setOpaque(false);
            area.setFont(new Font("Segoe UI", Font.PLAIN, 14)); area.setForeground(new Color(0xCFCBC2));
            area.setText("Petualanganmu akan tercatat di sini.");
            add(scroll(area), BorderLayout.CENTER);
        }
        public void add(String header, List<String> lines) {
            area.append("\n\n-- " + header.toUpperCase() + " --");
            lines.forEach(l -> area.append("\n" + l));
            area.setCaretPosition(area.getDocument().getLength());
        }
    }
}
