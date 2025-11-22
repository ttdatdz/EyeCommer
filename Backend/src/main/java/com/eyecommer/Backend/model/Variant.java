package com.eyecommer.Backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Variant")
@Getter
@Setter
public class Variant extends AbstractEntity<Long> {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    // Một thuộc tính (Variant) có thể được sử dụng bởi Nhiều (N) SKU.
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VariantProductAttribute> variantProductAttributes; // Tên mới


}
