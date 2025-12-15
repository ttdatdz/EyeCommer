package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "Address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address extends AbstractEntity<Long> {


    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "ward")
    private String ward;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "is_default")
    private Boolean isDefault;

    // ===== GHN fields (BẮT BUỘC) =====
    @Column(name = "district_id", nullable = false)
    private Integer districtId;

    @Column(name = "ward_code", nullable = false)
    private String wardCode;

    @OneToMany(mappedBy = "address")
    private Set<Order> orders;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
