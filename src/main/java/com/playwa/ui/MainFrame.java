package com.playwa.ui;

import com.playwa.activity.Activity;
import com.playwa.activity.ActivityResult;
import com.playwa.model.InventorySlot;
import com.playwa.model.Item;
import com.playwa.model.Player;
import com.playwa.model.Usable;
import com.playwa.service.Crafting;
import com.playwa.ui.ItemBrowser.Entry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/** Jendela utama: top bar, sidebar, dan halaman-halaman game. */
public class MainFrame extends JFrame {
    private final Services s;
    private Player player;
    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);
    private final Map<String, Page> map = new LinkedHashMap<>();
    private final Map<String, Theme.Btn> nav = new LinkedHashMap<>();
    private final Theme.Bar hp = new Theme.Bar(Theme.HP), sta = new Theme.Bar(Theme.STA);
    private final JLabel hpText = Theme.label("", Theme.SMALL, Theme.TEXT), staText = Theme.label("", Theme.SMALL, Theme.TEXT),
            goldText = Theme.label("", Theme.BOLD, Theme.GOLD), who = Theme.label("", Theme.BOLD, Theme.TEXT),
            lvl = Theme.label("", Theme.SMALL, Theme.MUTED), welcome = Theme.label("", Theme.TITLE, Theme.TEXT),
            toast = Theme.label(" ", Theme.BODY, Theme.GOLD);
    private final Theme.Bar expBar = new Theme.Bar(Theme.EXP);
    private final JLabel expText = Theme.label(" ", Theme.SMALL, Theme.MUTED);
    private final javax.swing.Timer toastTimer = new javax.swing.Timer(4500, e -> toast.setText(" "));

    public MainFrame(Services s, Player player) {
        this.s = s; this.player = player; toastTimer.setRepeats(false);
        restTimer = new javax.swing.Timer(REST_SECONDS_PER_STAMINA * 1000, e -> restTick());
        setTitle("Play W.A | World Adventure");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        GameBackground root = new GameBackground(new BorderLayout());
        setContentPane(root);

        pages.setOpaque(false);
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(16, 20, 10, 20));
        center.add(pages, BorderLayout.CENTER); center.add(toast, BorderLayout.SOUTH);
        root.add(topBar(), BorderLayout.NORTH); root.add(sidebar(), BorderLayout.WEST); root.add(center, BorderLayout.CENTER);

        Theme.Log homeLog = new Theme.Log("Catatan Petualangan"), dungeonLog = new Theme.Log("Laporan Dungeon");
        home = new HomePage(this, homeLog);
        addPage("Home", welcome, home);
        addPage("Dungeon", Theme.label("Dungeon", Theme.TITLE, Theme.TEXT), new DungeonPage(this, dungeonLog));
        addPage("Inventory", Theme.label("Inventory", Theme.TITLE, Theme.TEXT), new ItemBrowser(this::inventoryEntries));
        addPage("Shop", Theme.label("Shop", Theme.TITLE, Theme.TEXT), shopPage());
        addPage("Crafting", Theme.label("Crafting", Theme.TITLE, Theme.TEXT), new ItemBrowser(this::craftEntries));
        reload(); show("Home");
    }

    public Player player() { return player; }
    public Services services() { return s; }


    private <T extends JComponent & Page> void addPage(
            String key,
            JLabel title,
            T page
    ) {
        JPanel wrap = new JPanel(new BorderLayout(0, 16));
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(4, 4, 4, 4));

        title.setFont(Theme.serif(Font.BOLD, 27));
        title.setForeground(Theme.TEXT);

        wrap.add(title, BorderLayout.NORTH);
        wrap.add(page, BorderLayout.CENTER);

        pages.add(wrap, key);
        map.put(key, page);

        Theme.Btn button = new Theme.Btn(key, Theme.SIDE);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(Theme.BOLD);
        button.setForeground(Theme.TEXT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setPreferredSize(new Dimension(190, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Buka halaman " + key);

        button.addActionListener(e -> show(key));

        nav.put(key, button);

        navBox.add(button);
        navBox.add(Box.createVerticalStrut(7));
    }

    private final JPanel navBox = new JPanel();



    private JPanel topBar() {
        JPanel bar = new JPanel(new BorderLayout(25, 0));
        bar.setBackground(Theme.SIDE);

        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, Theme.LINE
                ),
                new EmptyBorder(15, 24, 15, 24)
        ));

        // Identitas game
        JLabel logo = Theme.label(
                "PLAY W.A",
                Theme.serif(Font.BOLD, 26),
                Theme.GOLD
        );

        JLabel subtitle = Theme.label(
                "WORLD ADVENTURE",
                Theme.SMALL,
                Theme.MUTED
        );

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        brand.add(logo);
        brand.add(Box.createVerticalStrut(3));
        brand.add(subtitle);

        // Informasi status pemain
        JPanel stats = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 14, 0)
        );
        stats.setOpaque(false);

        stats.add(createTopStat(
                "HEALTH",
                hp,
                hpText,
                Theme.HP
        ));

        stats.add(createTopStat(
                "STAMINA",
                sta,
                staText,
                Theme.STA
        ));

        // Gold tanpa kotak luar
        JPanel goldPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 9, 5)
        );
        goldPanel.setOpaque(false);

        Theme.Pill goldPill = new Theme.Pill(
                "GOLD",
                Theme.GOLD
        );

        goldPanel.add(goldPill);
        goldPanel.add(goldText);

        stats.add(goldPanel);

        bar.add(brand, BorderLayout.WEST);
        bar.add(stats, BorderLayout.EAST);

        return bar;
    }


    private JPanel createTopStat(
            String title,
            Theme.Bar progress,
            JLabel value,
            Color color
    ) {
        JPanel panel = new JPanel(
                new BorderLayout(0, 5)
        );

        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(135, 42));

        JLabel heading = Theme.label(
                title,
                Theme.SMALL,
                color
        );

        progress.setPreferredSize(
                new Dimension(75, 7)
        );

        JPanel valuePanel = new JPanel(
                new BorderLayout(7, 0)
        );

        valuePanel.setOpaque(false);
        valuePanel.add(progress, BorderLayout.CENTER);
        valuePanel.add(value, BorderLayout.EAST);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(valuePanel, BorderLayout.CENTER);

        return panel;
    }


    private JPanel statusBlock(
            String title,
            Theme.Bar progress,
            JLabel value,
            Color accent
    ) {
        Theme.Card card = new Theme.Card(
                new BorderLayout(0, 5),
                Theme.PANEL,
                10
        );

        JLabel heading = Theme.label(
                title,
                Theme.SMALL,
                accent
        );

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        progress.setPreferredSize(new Dimension(85, 8));

        row.add(progress, BorderLayout.CENTER);
        row.add(value, BorderLayout.EAST);

        card.add(heading, BorderLayout.NORTH);
        card.add(row, BorderLayout.CENTER);

        card.setPreferredSize(new Dimension(190, 52));

        return card;
    }




    private JPanel sidebar() {
        JPanel side = new JPanel(new BorderLayout(0, 20));
        side.setBackground(Theme.SIDE);
        side.setPreferredSize(new Dimension(245, 0));

        side.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 0, 1, Theme.LINE
                ),
                new EmptyBorder(20, 15, 16, 15)
        ));

        // Profil pemain
        Theme.Card profile = new Theme.Card(
                new BorderLayout(0, 10),
                Theme.PANEL,
                14
        ).stripe(Theme.GOLD);

        JLabel profileTitle = Theme.label(
                "PROFIL PETUALANG",
                Theme.SMALL,
                Theme.GOLD
        );

        profileTitle.setFont(Theme.BOLD);
        profileTitle.setHorizontalAlignment(SwingConstants.LEFT);
        profileTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        who.setFont(Theme.serif(Font.BOLD, 17));
        who.setForeground(Theme.TEXT);
        who.setHorizontalAlignment(SwingConstants.LEFT);
        who.setAlignmentX(Component.LEFT_ALIGNMENT);

        lvl.setFont(Theme.BODY);
        lvl.setForeground(Theme.MUTED);
        lvl.setHorizontalAlignment(SwingConstants.LEFT);
        lvl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel identity = new JPanel();
        identity.setOpaque(false);
        identity.setLayout(new BoxLayout(identity, BoxLayout.Y_AXIS));
        identity.setAlignmentX(Component.LEFT_ALIGNMENT);

        identity.add(who);
        identity.add(Box.createVerticalStrut(5));
        identity.add(lvl);

        expBar.setPreferredSize(new Dimension(170, 9));
        expBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 9));

        JPanel expPanel = new JPanel(new BorderLayout(0, 7));
        expPanel.setOpaque(false);
        expPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        expPanel.add(expBar, BorderLayout.NORTH);
        expPanel.add(expText, BorderLayout.SOUTH);

        JPanel profileContent = new JPanel();
        profileContent.setOpaque(false);
        profileContent.setLayout(new BoxLayout(profileContent, BoxLayout.Y_AXIS));

        profileContent.add(profileTitle);
        profileContent.add(Box.createVerticalStrut(12));
        profileContent.add(identity);
        profileContent.add(Box.createVerticalStrut(15));
        profileContent.add(expPanel);

        profile.add(profileContent, BorderLayout.CENTER);

        // Menu navigasi
        JLabel menuTitle = Theme.label(
                "MENU UTAMA",
                Theme.SMALL,
                Theme.MUTED
        );

        navBox.removeAll();
        navBox.setOpaque(false);
        navBox.setLayout(new BoxLayout(navBox, BoxLayout.Y_AXIS));

        JPanel menu = new JPanel(new BorderLayout(0, 12));
        menu.setOpaque(false);
        menu.add(menuTitle, BorderLayout.NORTH);
        menu.add(navBox, BorderLayout.CENTER);

        // Tombol logout
        Theme.Btn out = new Theme.Btn("Logout", Theme.RED);

        out.setHorizontalAlignment(SwingConstants.CENTER);
        out.setFont(Theme.BOLD);
        out.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        out.setCursor(new Cursor(Cursor.HAND_CURSOR));

        out.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah kamu yakin ingin keluar dari permainan?",
                    "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                restTimer.stop();
                toastTimer.stop();

                new AuthFrame(s).setVisible(true);
                dispose();
            }
        });

        side.add(profile, BorderLayout.NORTH);
        side.add(menu, BorderLayout.CENTER);
        side.add(out, BorderLayout.SOUTH);

        return side;
    }



    private ShopPage shopPage() {
        ItemBrowser buy = new ItemBrowser(this::buyEntries), sell = new ItemBrowser(this::sellEntries);
        CardLayout cl = new CardLayout(); JPanel body = new JPanel(cl); body.setOpaque(false);
        body.add(buy, "buy"); body.add(sell, "sell");
        Theme.Btn b1 = new Theme.Btn("Beli", new Color(0x3A4160)), b2 = new Theme.Btn("Jual", Theme.PANEL2);
        b1.addActionListener(e -> { cl.show(body, "buy"); b1.setBase(new Color(0x3A4160)); b2.setBase(Theme.PANEL2); });
        b2.addActionListener(e -> { cl.show(body, "sell"); b2.setBase(new Color(0x3A4160)); b1.setBase(Theme.PANEL2); });
        JPanel tg = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); tg.setOpaque(false); tg.add(b1); tg.add(b2);
        return new ShopPage(tg, body, buy, sell);
    }

    private static class ShopPage extends JPanel implements Page {
        private final ItemBrowser buy, sell;
        ShopPage(JPanel toggles, JPanel body, ItemBrowser buy, ItemBrowser sell) {
            super(new BorderLayout(0, 12)); this.buy = buy; this.sell = sell; setOpaque(false);
            add(toggles, BorderLayout.NORTH); add(body, BorderLayout.CENTER);
        }
        @Override public void refresh() { buy.refresh(); sell.refresh(); }
    }

    // ---- data untuk ItemBrowser ----
    private String info(Item it) { return it.getRarity().getLabel() + " - " + it.getDescription(); }

    private List<Entry> inventoryEntries() {
        List<Entry> out = new ArrayList<>();
        for (InventorySlot sl : s.inventory().list(player)) {
            Item it = sl.getItem();
            out.add(new Entry(it.getName(), it.getCategory(), info(it), Theme.rarity(it.getRarity()),
                    player.isEquipped(it.getName()) ? "Terpasang" : "", "x" + sl.getQuantity(), it instanceof Usable ? "Pakai" : "", true,
                    () -> { toast(s.inventory().useItem(player, it)); reload(); }));
        }
        return out;
    }

    private List<Entry> buyEntries() {
        List<Entry> out = new ArrayList<>();
        for (Item it : s.shop().catalog())
            out.add(new Entry(it.getName(), it.getCategory(), info(it), Theme.rarity(it.getRarity()), "", it.getPrice() + " gold", "Beli",
                    player.getGold() >= it.getPrice(), () -> { toast(s.shop().buyItem(player, it.getName(), 1)); reload(); }));
        return out;
    }

    private List<Entry> sellEntries() {
        List<Entry> out = new ArrayList<>();
        for (InventorySlot sl : s.inventory().list(player)) {
            Item it = sl.getItem();
            out.add(new Entry(it.getName(), it.getCategory(), "Dimiliki x" + sl.getQuantity(), Theme.rarity(it.getRarity()),
                    player.isEquipped(it.getName()) ? "Terpasang" : "", s.shop().sellPrice(it) + " gold", "Jual", true,
                    () -> { toast(s.shop().sellItem(player, it.getName(), 1)); reload(); }));
        }
        return out;
    }

    private List<Entry> craftEntries() {
        Map<String, Integer> owned = new HashMap<>();
        s.inventory().list(player).forEach(sl -> owned.put(sl.getItem().getName(), sl.getQuantity()));
        List<Entry> out = new ArrayList<>();
        for (Crafting.Recipe r : s.crafting().recipes()) {
            Item it = s.items().findByName(r.result()).orElse(null);
            if (it == null) continue;
            boolean can = true; StringBuilder mats = new StringBuilder();
            for (Map.Entry<String, Integer> e : r.ingredients().entrySet()) {
                int have = owned.getOrDefault(e.getKey(), 0);
                if (have < e.getValue()) can = false;
                mats.append(mats.length() == 0 ? "" : ", ").append(e.getKey()).append(' ').append(have).append('/').append(e.getValue());
            }
            out.add(new Entry(it.getName(), it.getCategory(), it.getDescription() + "\nBahan: " + mats, Theme.rarity(it.getRarity()),
                    "", "", "Craft", can, () -> { toast(s.crafting().craftItem(player, r.result())); reload(); }));
        }
        return out;
    }

    // ---- aksi dan penyegaran ----
    /** Berapa detik untuk memulihkan 1 stamina saat Rest. Ubah angka ini untuk mengatur kecepatan. */
    public static final int REST_SECONDS_PER_STAMINA = 2;
    private javax.swing.Timer restTimer;
    private boolean resting;
    private Theme.Log restLog;
    private HomePage home;
    private boolean busy;

    public void run(Activity a, Theme.Log log) {
        if (resting) { toast("Hentikan istirahat dulu (klik Berhenti)."); return; }
        if (busy) { toast("Tunggu petualangan selesai..."); return; }
        try {
            ActivityResult r = s.game().perform(player, a);
            busy = true;
            log.play(a.getName(), r.getLogs(), summary(r), () -> { busy = false; reload(); });
        } catch (Exception ex) { busy = false; error(ex); }
    }

    /** Hadiah ditampilkan setelah adegan selesai. Kosong kalau tidak ada hadiah (misalnya stamina kurang). */
    private List<String> summary(ActivityResult r) {
        List<String> out = new ArrayList<>();
        if (r.getExp() == 0 && r.getGold() == 0 && r.getLoot().isEmpty()) return out;
        out.add("+" + r.getExp() + " EXP, +" + r.getGold() + " gold");
        Map<String, Integer> count = new LinkedHashMap<>();
        r.getLoot().forEach(i -> count.merge(i.getName(), 1, Integer::sum));
        out.add(count.isEmpty() ? "Item: tidak ada" : "Item: " + count.entrySet().stream()
                .map(e -> e.getKey() + " x" + e.getValue()).collect(java.util.stream.Collectors.joining(", ")));
        if (r.isLeveledUp()) out.add("LEVEL UP! Sekarang level " + player.getLevel());
        return out;
    }

    /** Rest bertahap: +1 stamina tiap REST_SECONDS_PER_STAMINA detik. Klik lagi untuk berhenti. */
    public void rest(Theme.Log log) {
        if (busy) { toast("Tunggu petualangan selesai..."); return; }
        if (resting) { stopRest("Kamu berhenti beristirahat."); return; }
        if (player.getStamina() >= player.getMaxStamina() && player.getHp() >= player.getMaxHp()) {
            toast("Kamu sudah segar, tidak perlu istirahat."); return;
        }
        resting = true; restLog = log;
        log.add("Istirahat", List.of("Kamu duduk bersandar dan memejamkan mata... (+1 stamina tiap "
                + REST_SECONDS_PER_STAMINA + " detik)"));
        home.setResting(true, restInfo());
        restTimer.start();
    }

    private void restTick() {
        player.restoreStamina(1);
        player.heal(Math.max(1, player.getMaxHp() / 50));
        s.players().save(player);
        updateBars();
        boolean full = player.getStamina() >= player.getMaxStamina() && player.getHp() >= player.getMaxHp();
        if (full) stopRest("Kamu merasa segar kembali!"); else home.setResting(true, restInfo());
    }

    private void stopRest(String message) {
        restTimer.stop(); resting = false;
        restLog.add("Selesai istirahat", List.of(message, restInfo() + "  |  HP " + player.getHp() + "/" + player.getMaxHp()));
        home.setResting(false, "");
    }

    private String restInfo() { return "STA " + player.getStamina() + "/" + player.getMaxStamina(); }

    public void toast(String msg) { toast.setText(msg); toastTimer.restart(); }

    private void error(Exception ex) { JOptionPane.showMessageDialog(this, String.valueOf(ex.getMessage()), "Terjadi kesalahan", JOptionPane.ERROR_MESSAGE); }

    private void updateBars() {
        hp.set(player.getHp(), player.getMaxHp()); hpText.setText(player.getHp() + "/" + player.getMaxHp());
        sta.set(player.getStamina(), player.getMaxStamina()); staText.setText(player.getStamina() + "/" + player.getMaxStamina());
        goldText.setText(player.getGold() + " gold");
    }

    public void reload() {
        player = s.players().findById(player.getId()).orElseThrow();
        updateBars(); who.setText(player.getName()); lvl.setText("Level " + player.getLevel());
        expBar.set(player.getExp(), player.expToNextLevel()); expText.setText("EXP " + player.getExp() + " / " + player.expToNextLevel());
        welcome.setText("Selamat datang, " + player.getName());
        map.values().forEach(Page::refresh);
    }


    private void show(String key) {
        if (busy) {
            toast("Tunggu aktivitas selesai terlebih dahulu.");
            return;
        }

        if (!map.containsKey(key)) {
            return;
        }

        cards.show(pages, key);

        nav.forEach((name, button) -> {
            boolean active = name.equals(key);

            button.setSelected(active);
            button.setBase(active ? Theme.GOLD_DARK : Theme.SIDE);
            button.setForeground(active ? Theme.GOLD : Theme.TEXT);
        });

        map.get(key).refresh();
    }

}