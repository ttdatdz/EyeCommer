package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Role_has_permission")
@Getter
@Setter
public class RoleHasPermission extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permissions permission;

    @Column(name = "granted_at")
    private Date grantedAt;
}
