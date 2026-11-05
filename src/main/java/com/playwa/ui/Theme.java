package com.playwa.ui;

import com.playwa.model.Rarity;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Warna, font, dan komponen kustom bertema dark fantasy.
 * Font dipilih otomatis: kalau font utama tidak ada di komputer, dipakai cadangannya
 * (jadi tampilan tetap rapi di Windows, macOS, maupun Linux).
 */
public final class Theme {
    private Theme() {}

    // ---- Warna ----
    public static final Color BG = new Color(0x090A0D), SIDE = new Color(0x0C0D11), PANEL = new Color(0x12141A),
            PANEL2 = new Color(0x1A1D26), LINE = new Color(0x2A2E3A), TEXT = new Color(0xE9E6DF), MUTED = new Color(0x8B8F9C),
            HP = new Color(0xE5484D), STA = new Color(0x2BB673), EXP = new Color(0x9B5DE5), GOLD = new Color(0xE2B44F),
            RED = new Color(0x4A1A1E), GREEN = new Color(0x143324), BLUE = new Color(0x1B2A4A), PURPLE = new Color(0x2C1B48);

    // ---- Font: judul pakai serif (kesan fantasi), isi pakai sans-serif yang mudah dibaca ----
    private static final Set<String> INSTALLED = new HashSet<>(Arrays.asList(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
    public static final String SERIF = pick(Font.SERIF, "Georgia", "Palatino Linotype", "Book Antiqua", "Cambria");
    public static final String SANS = pick(Font.SANS_SERIF, "Segoe UI", "Inter", "Helvetica Neue", "Roboto", "Arial");

    public static final Font TITLE = new Font(SERIF, Font.BOLD, 28), H2 = new Font(SERIF, Font.BOLD, 17),
            BODY = new Font(SANS, Font.PLAIN, 13), BOLD = new Font(SANS, Font.BOLD, 13),
            SMALL = new Font(SANS, Font.PLAIN, 12), BIG = new Font(SANS, Font.BOLD, 22);

    /** Font judul dengan ukuran bebas (dipakai logo dan lambang). */
    public static Font serif(int style, int size) { return new Font(SERIF, style, size); }

    /** Font isi dengan ukuran bebas. */
    public static Font sans(int style, int size) { return new Font(SANS, style, size); }

    private static String pick(String fallback, String... wanted) {
        for (String f : wanted) if (INSTALLED.contains(f)) return f;
        return fallback;
    }

    public static void install() {
        UIManager.put("Button.disabledText", new Color(0x6A6E7A));
        UIManager.put("Button.font", BOLD);
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("Panel.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("OptionPane.buttonFont", BOLD);
        UIManager.put("ToolTip.background", PANEL2);
        UIManager.put("ToolTip.foreground", TEXT);
        UIManager.put("ToolTip.font", SMALL);
        UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINE), new EmptyBorder(4, 8, 4, 8)));
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
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel l = new JLabel(text); l.setFont(font); l.setForeground(color); return l;
    }

    /** Kolom input: garis tipis, berubah emas saat sedang diisi. */
    public static void style(JTextField f) {
        f.setFont(BODY); f.setForeground(TEXT); f.setCaretColor(GOLD); f.setBackground(new Color(0x07080A));
        f.setSelectionColor(new Color(0x5A4719)); f.setSelectedTextColor(TEXT);
        Runnable idle = () -> f.setBorder(fieldBorder(LINE));
        idle.run();
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { f.setBorder(fieldBorder(GOLD)); }
            @Override public void focusLost(FocusEvent e) { idle.run(); }
        });
    }

    private static javax.swing.border.Border fieldBorder(Color line) {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(line), new EmptyBorder(8, 10, 8, 10));
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
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                if (r.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create(); smooth(g2);
                g2.setColor(isThumbRollover() || isDragging ? new Color(0x4A5063) : LINE);
                g2.fillRoundRect(r.x + 1, r.y, r.width - 2, r.height, r.width, r.width); g2.dispose();
            }
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
            super(lm); this.fill = fill; setOpaque(false); setBorder(new EmptyBorder(pad, pad, pad, pad));
        }
        public Card stripe(Color c) {
            stripe = c; Insets i = getBorder().getBorderInsets(this);
            setBorder(new EmptyBorder(i.top, i.left + 4, i.bottom, i.right)); return this;
        }
        public Card dashed() { dashed = true; return this; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); smooth(g2);
            RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            if (!dashed) {
                // sedikit lebih terang di bagian atas supaya kartu terasa punya kedalaman
                g2.setPaint(new GradientPaint(0, 0, lighter(fill, 8), 0, getHeight(), fill)); g2.fill(shape);
            }
            if (stripe != null) { g2.setClip(shape); g2.setColor(stripe); g2.fillRect(0, 0, 4, getHeight()); g2.setClip(null); }
            g2.setColor(LINE);
            if (dashed) g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{5, 4}, 0));
            g2.draw(shape); g2.dispose();
        }
    }

    private static Color lighter(Color c, int amount) {
        return new Color(Math.min(255, c.getRed() + amount), Math.min(255, c.getGreen() + amount), Math.min(255, c.getBlue() + amount));
    }

    public static class Btn extends JButton {
        private Color base; private boolean hover, pressed;
        public Btn(String text, Color base) {
            super(text); this.base = base; setFont(BOLD); setForeground(TEXT);
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setBorder(new EmptyBorder(8, 16, 8, 16)); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; pressed = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }
        public void setBase(Color c) { base = c; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); smooth(g2);
            boolean on = isEnabled();
            Color c = !on ? new Color(0x1C1F28) : pressed ? base.darker() : hover ? base.brighter() : base;
            g2.setColor(c); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            if (on && hover) { // garis emas tipis saat kursor di atas tombol
                g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 120));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g2.dispose();
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
                int fw = Math.max(h, (int) (w * value));
                g2.setPaint(new GradientPaint(0, 0, color.darker(), w, 0, color));
                g2.fillRoundRect(0, 0, fw, h, h, h);
                g2.setColor(new Color(255, 255, 255, 45)); // kilau tipis di separuh atas bar
                g2.fillRoundRect(2, 1, Math.max(0, fw - 4), Math.max(1, h / 2 - 1), h, h);
            }
            g2.setColor(LINE); g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
            g2.dispose();
        }
    }

    /** Label kecil berbentuk pil (untuk rarity, badge "Terpasang", dan sejenisnya). */
    public static class Pill extends JComponent {
        private final String text; private final Color color;
        public Pill(String text, Color color) {
            this.text = text; this.color = color;
            FontMetrics fm = getFontMetrics(SMALL.deriveFont(Font.BOLD, 11f));
            setPreferredSize(new Dimension(fm.stringWidth(text) + 18, 20));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create(); smooth(g2);
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 140));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            g2.setFont(SMALL.deriveFont(Font.BOLD, 11f)); g2.setColor(color);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    /** Garis pemisah tipis dengan pudar emas di tengah. */
    public static class Divider extends JComponent {
        public Divider() { setPreferredSize(new Dimension(10, 2)); setMaximumSize(new Dimension(Integer.MAX_VALUE, 2)); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth();
            Color gold = new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 150), none = new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 0);
            g2.setPaint(new GradientPaint(0, 0, none, w / 2f, 0, gold)); g2.fillRect(0, 0, w / 2, 1);
            g2.setPaint(new GradientPaint(w / 2f, 0, gold, w, 0, none)); g2.fillRect(w / 2, 0, w - w / 2, 1);
            g2.dispose();
        }
    }

    /** Papan catatan petualangan. */
    public static class Log extends Card {
        private final JTextArea area = new JTextArea();
        public Log(String title) {
            super(new BorderLayout(0, 10), PANEL, 16);
            JPanel head = new JPanel(new BorderLayout(0, 8)); head.setOpaque(false);
            head.add(label(title, H2, TEXT), BorderLayout.NORTH); head.add(new Divider(), BorderLayout.SOUTH);
            add(head, BorderLayout.NORTH);
            area.setEditable(false); area.setLineWrap(true); area.setWrapStyleWord(true); area.setOpaque(false);
            area.setFont(sans(Font.PLAIN, 14)); area.setForeground(new Color(0xCFCBC2));
            area.setMargin(new Insets(2, 2, 2, 6));
            area.setText("Petualanganmu akan tercatat di sini.");
            add(scroll(area), BorderLayout.CENTER);
        }
        /** Memutar adegan baris demi baris, lalu hadiah di akhir. */
        public void play(String header, List<String> story, List<String> summary, Runnable done) {
            List<String> texts = new ArrayList<>(); List<Integer> delays = new ArrayList<>();
            texts.add("\n— " + header.toUpperCase() + " —"); delays.add(0);
            for (String l : story) { texts.add(l); delays.add(600 + Math.min(900, l.length() * 12)); }
            if (!summary.isEmpty()) {
                texts.add("\n— HASIL —"); delays.add(1100);
                for (String l : summary) { texts.add(l); delays.add(450); }
            }
            int[] i = {0};
            javax.swing.Timer t = new javax.swing.Timer(0, null);
            t.addActionListener(e -> {
                if (i[0] >= texts.size()) { t.stop(); done.run(); return; }
                area.append("\n" + texts.get(i[0])); area.setCaretPosition(area.getDocument().getLength());
                i[0]++; t.setDelay(i[0] < texts.size() ? delays.get(i[0]) : 700);
            });
            t.start();
        }

        public void add(String header, List<String> lines) {
            area.append("\n\n— " + header.toUpperCase() + " —");
            lines.forEach(l -> area.append("\n" + l));
            area.setCaretPosition(area.getDocument().getLength());
        }
    }
}