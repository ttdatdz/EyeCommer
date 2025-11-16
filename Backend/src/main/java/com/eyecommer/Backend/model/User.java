package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User extends AbstractEntity<Long> {
    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "user")
    private Set<Order> orders;

    @OneToMany(mappedBy = "user")
    private Set<Address> addresses;

    @OneToMany(mappedBy = "user")
    private Set<VoucherUser> voucherUsers;

    @OneToMany(mappedBy = "user")
    private Set<ChatSession> chatSessions;

    @OneToMany(mappedBy = "sender")
    private Set<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "user")
    private Set<GroupHasUser> groups;

    @OneToMany(mappedBy = "user")
    private Set<UserHasRole> roles;
}
