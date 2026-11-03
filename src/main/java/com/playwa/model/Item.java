package com.playwa.model;

import jakarta.persistence.*;

/** Abstract class + Inheritance: parent semua item. */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type")
public abstract class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    @Enumerated(EnumType.STRING)
    private Rarity rarity = Rarity.COMMON;

    protected Item() {}
    protected Item(String name, int price) { this(name, price, Rarity.COMMON); }
    protected Item(String name, int price, Rarity rarity) { this.name = name; this.price = price; this.rarity = rarity; }

    public abstract String getDescription(); // polymorphism
    public abstract String getCategory();

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public Rarity getRarity() { return rarity == null ? Rarity.COMMON : rarity; }
}
