package com.eyecommer.Backend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUpdateDTO {
//
//    @NotBlank(message = "Mã voucher không được để trống")
//    private String code;

    @NotBlank(message = "Mô tả voucher không được để trống")
    private String description;

    @NotNull(message = "Giá trị giảm giá không được để trống")
    @Min(value = 0, message = "Giá trị giảm giá phải lớn hơn hoặc bằng 0")
    private Double discount;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "GMT+7")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "GMT+7")
    private LocalDateTime endDate;

    @NotNull(message = "Số lượng phát hành tối đa không được để trống")
    @Min(value = 1, message = "Số lượng phát hành phải lớn hơn 0")
    private Integer maxUsage;
}