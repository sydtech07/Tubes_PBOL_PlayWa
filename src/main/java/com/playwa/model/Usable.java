package com.playwa.model;

/** Interface: item yang bisa dipakai pemain. */
public interface Usable {
    String use(Player player);

    /** Potion habis setelah dipakai, senjata tidak. */
    default boolean isConsumedOnUse() { return true; }
}
