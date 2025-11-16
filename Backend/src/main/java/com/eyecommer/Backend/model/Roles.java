package com.eyecommer.Backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Roles")
@Getter
@Setter
public class Roles extends AbstractEntity<Long> {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "role")
    private Set<UserHasRole> users;

    @OneToMany(mappedBy = "role")
    private Set<GroupHasRole> groups;

    @OneToMany(mappedBy = "role")
    private Set<RoleHasPermission> permissions;
}