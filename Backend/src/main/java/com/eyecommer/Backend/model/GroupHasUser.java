package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Group_Has_User")
@Getter
@Setter
public class GroupHasUser extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "joined_at")
    private Date joinedAt;
}