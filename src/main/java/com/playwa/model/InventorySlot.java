package com.playwa.model;

import jakarta.persistence.*;

/** Relasi Player - Item beserta jumlahnya (isi Inventory). */
@Entity
public class InventorySlot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) private Player player;
    @ManyToOne(optional = false) private Item item;
    private int quantity;

    protected InventorySlot() {}
    public InventorySlot(Player player, Item item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Jumlah item harus lebih dari 0.");
        this.player = player; this.item = item; this.quantity = quantity;
    }

    /** Menambah stok. Jumlah harus positif; hasilnya dibatasi agar tidak overflow jadi negatif. */
    public void add(int n) {
        if (n <= 0) throw new IllegalArgumentException("Jumlah item harus lebih dari 0.");
        quantity = (int) Math.min((long) quantity + n, Integer.MAX_VALUE);
    }

    /** Mengurangi stok. Gagal (false) kalau n tidak positif atau stok kurang, jumlah tidak pernah negatif. */
    public boolean remove(int n) {
        if (n <= 0 || quantity < n) return false;
        quantity -= n;
        return true;
    }

    public Long getId() { return id; }
    public Player getPlayer() { return player; }
    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }
}
