package com.eyecommer.Backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Groups")
@Getter
@Setter
public class Group extends AbstractEntity<Long> {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "group")
    private Set<GroupHasUser> users;

    @OneToMany(mappedBy = "group")
    private Set<GroupHasRole> roles;
}
