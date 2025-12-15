package com.eyecommer.Backend.dto.response;

import lombok.Data;

@Data
public class GHNResponseDTO<T> {
    private Integer code;
    private String message;
    private T data;
}

