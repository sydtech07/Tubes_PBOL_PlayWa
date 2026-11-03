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
    public InventorySlot(Player player, Item item, int quantity) { this.player = player; this.item = item; this.quantity = quantity; }

    public void add(int n) { quantity += n; }
    public boolean remove(int n) { if (quantity < n) return false; quantity -= n; return true; }

    public Long getId() { return id; }
    public Player getPlayer() { return player; }
    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }
}
