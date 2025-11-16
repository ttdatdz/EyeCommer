package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Review")
@Getter
@Setter
public class Review extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "content")
    private String content;
}
