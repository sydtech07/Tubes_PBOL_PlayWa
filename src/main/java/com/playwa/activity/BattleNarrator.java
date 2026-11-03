package com.playwa.activity;

import java.util.Random;

/** Penutur cerita pertempuran: kalimat acak supaya tiap duel terasa hidup. */
public final class BattleNarrator {
    private static final Random RNG = new Random();
    private BattleNarrator() {}

    private static String pick(String[] lines) { return lines[RNG.nextInt(lines.length)]; }

    public static String encounter(String monster) {
        return String.format(pick(new String[] {
            "%s menghadang jalanmu!",
            "%s melompat keluar dari bayang-bayang!",
            "Terdengar geraman... %s muncul di depanmu!",
            "%s datang dengan mata buas, siap menerkam!",
            "Tanah bergetar. %s mengincar nyawamu!"}), monster);
    }

    public static String playerHit(String monster, int dmg) {
        return String.format(pick(new String[] {
            "Kamu menebas %s! (-%d) \"Rasakan ini!\"",
            "Serangan telak mengenai %s! (-%d) \"Hiyaaa!\"",
            "%s terhuyung kena seranganmu (-%d). \"Cih, segitu saja?\"",
            "Kamu menyerbu %s tanpa ampun (-%d). \"Menyerahlah!\""}), monster, dmg);
    }

    public static String monsterHit(String monster, int dmg) {
        return String.format(pick(new String[] {
            "%s menghantam tubuhmu! (-%d) \"Akh!\"",
            "Aduh! %s mengoyak lenganmu (-%d). \"Sialan!\"",
            "Kamu terpukul mundur oleh %s (-%d). \"Uhuk... sakit...\"",
            "%s menyambar dari samping (-%d). \"Akhhh!\""}), monster, dmg);
    }

    public static String victory(String monster) {
        return String.format(pick(new String[] {
            "%s tumbang sambil mengerang. \"Ciah, kena kau!\"",
            "%s ambruk ke tanah. Napasmu terengah, tapi kamu menang!",
            "Satu serangan terakhir... %s tak bergerak lagi."}), monster);
    }

    public static String defeat(String monster) {
        return String.format(pick(new String[] {
            "Pandanganmu menggelap... %s terlalu kuat.",
            "\"Akhhh...\" Kamu roboh di hadapan %s.",
            "Lututmu lemas. %s berdiri menjulang di atasmu."}), monster);
    }
}
