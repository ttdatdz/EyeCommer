package com.eyecommer.Backend.mapper;

import com.eyecommer.Backend.dto.request.VoucherRequestDTO;
import com.eyecommer.Backend.dto.request.VoucherUpdateDTO;
import com.eyecommer.Backend.dto.response.VoucherResponseDTO;
import com.eyecommer.Backend.model.Voucher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VoucherMapper {

    /**
     * Chuyển đổi VoucherRequestDTO sang Voucher Entity (dùng cho CREATE).
     */
    public Voucher toEntity(VoucherRequestDTO dto) {
        if (dto == null) return null;

        Voucher voucher = new Voucher();

        // Code sẽ được Service tạo ngẫu nhiên và gán sau
        voucher.setDescription(dto.getDescription());
        voucher.setDiscount(dto.getDiscount());
        voucher.setStartDate(dto.getStartDate());
        voucher.setEndDate(dto.getEndDate());

        // Thêm số lượng tối đa
        voucher.setMaxUsage(dto.getMaxUsage());
        // Số lượng đã dùng ban đầu là 0, code sẽ gán trong Service
        voucher.setCurrentUsage(0);

        return voucher;
    }

    /**
     * Chuyển đổi Voucher Entity sang VoucherResponseDTO.
     */
    public VoucherResponseDTO toResponseDTO(Voucher entity) {
        if (entity == null) return null;

        VoucherResponseDTO dto = new VoucherResponseDTO();

        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());
        dto.setDiscount(entity.getDiscount());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());

        // Ánh xạ và tính toán các trường số lượng
        dto.setMaxUsage(entity.getMaxUsage());
        // Lấy số lượng đã dùng từ trường currentUsage trong Entity (hoặc users.size() nếu bạn muốn giữ lại logic cũ)
        dto.setCurrentUsage(entity.getCurrentUsage());

        // Tính toán số lượng còn lại
        int remaining = entity.getMaxUsage() - entity.getCurrentUsage();
        dto.setRemainingUsage(Math.max(0, remaining));

        return dto;
    }

    /**
     * Cập nhật Voucher Entity từ VoucherUpdateDTO.
     */
    public void toEntityFromUpdateDTO(VoucherUpdateDTO updateDTO, Voucher voucher) {
        if (updateDTO == null || voucher == null) return;

        voucher.setDescription(updateDTO.getDescription());
        voucher.setDiscount(updateDTO.getDiscount());
        voucher.setStartDate(updateDTO.getStartDate());
        voucher.setEndDate(updateDTO.getEndDate());

        // Cập nhật số lượng tối đa
        voucher.setMaxUsage(updateDTO.getMaxUsage());
    }

    /**
     * Chuyển List<Voucher Entity> sang List<VoucherResponseDTO>.
     */
    public List<VoucherResponseDTO> toDTOList(List<Voucher> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}