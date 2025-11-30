package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Cart")
@Getter
@Setter
public class Cart extends AbstractEntity<Long> {

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> items = new HashSet<>();

    public void addItem(CartItem item) {
        if (items == null) items = new HashSet<>();
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        if (items != null) {
            items.remove(item);
            item.setCart(null);
        }
    }
}
