package com.eyecommer.Backend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponseDTO {
    private Long id;
    private String code;
    private String description;
    private Double discount;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime endDate;
    private Integer maxUsage; // Số lượng phát hành tối đa
    private Integer currentUsage; // Số lượng đã sử dụng
    private Integer remainingUsage;
}