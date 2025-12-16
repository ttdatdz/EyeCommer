package com.eyecommer.Backend.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class GHNItemDTO {
    private String name;
    private String code;
    private Integer quantity;
    private Integer price;
    private Integer weight;
}
