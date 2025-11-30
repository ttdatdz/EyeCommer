package com.eyecommer.Backend.service.impl;

import com.eyecommer.Backend.dto.request.VoucherRequestDTO;
import com.eyecommer.Backend.dto.request.VoucherUpdateDTO;
import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.dto.response.VoucherResponseDTO;
import com.eyecommer.Backend.mapper.VoucherMapper;
import com.eyecommer.Backend.model.Attribute;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.model.Voucher;
import com.eyecommer.Backend.model.VoucherUser;
import com.eyecommer.Backend.repository.GenericSearchRepository;
import com.eyecommer.Backend.repository.UserRepository;
import com.eyecommer.Backend.repository.VoucherRepository;
import com.eyecommer.Backend.repository.VoucherUserRepository;
import com.eyecommer.Backend.repository.critetia.GenericSearchQueryCriteriaConsumer;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.SearchQueryCriteriaConsumer;
import com.eyecommer.Backend.service.VoucherService;
import com.eyecommer.Backend.utils.GenerateCodeRandom;
import com.eyecommer.Backend.utils.SearchCriteriaUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final GenericSearchRepository genericSearchRepository;
    private final VoucherUserRepository voucherUserRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public VoucherResponseDTO createVoucher(VoucherRequestDTO requestDTO) {
        String code = GenerateCodeRandom.generateCustomCode("VOUCHER");
        Voucher voucher = voucherMapper.toEntity(requestDTO);
        voucher.setCode(code);
        voucher = voucherRepository.save(voucher);
        return voucherMapper.toResponseDTO(voucher);
    }

    @Override
    public VoucherResponseDTO getVoucherById(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Voucher không tồn tại với ID: " + id));
        return voucherMapper.toResponseDTO(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<?> getAllVouchers(int pageNo, int pageSize, String sortBy, String[] search) {
        // 1. Convert mảng search string sang List<SearchCriteria>
        // Giả định SearchCriteriaUtils có phương thức convert tĩnh
        List<SearchCriteria> criteriaList = SearchCriteriaUtils.convert(search);

        // 2. Khởi tạo Consumer (null nếu không cần logic lọc đặc biệt cho Voucher)
        SearchQueryCriteriaConsumer<Voucher> consumer =
                new GenericSearchQueryCriteriaConsumer<>(null, null, null);

        // 3. Sử dụng generic search repo để lấy PageResponse thô
        // Giả định GenericSearchRepository có phương thức searchByCriteria
        PageResponse<?> rawPage = genericSearchRepository.searchByCriteria(
                Voucher.class, // Tìm kiếm trên Entity Voucher
                pageNo,
                pageSize,
                criteriaList,
                sortBy,
                consumer
        );
        // 4. Lấy List Entity và ánh xạ sang DTO
        // Lưu ý: Cần cast lại List items từ rawPage
        List<Voucher> vouchers = (List<Voucher>) rawPage.getItems();
        List<VoucherResponseDTO> dtoList = vouchers.stream()
                .map(voucherMapper::toResponseDTO)
                .collect(Collectors.toList());

        // 5. Trả về PageResponse đã ánh xạ
        return PageResponse.<List<VoucherResponseDTO>>builder()
                .pageNo(rawPage.getPageNo())
                .pageSize(rawPage.getPageSize())
                .totalPage(rawPage.getTotalPage())
                .items(dtoList)
                .build();
    }


//    TH1 voucher chưa đến ngày bắt đầu thì cho update thoải mái, nhưng ngày bắt đầu phải bé hơn ngày kết thức.
//    TH2 voucher đang trong khoảng ngày bắt đầu + ngày kết thúc, thì không cho update luôn.
//    TH3 voucher đã qua khoảng ngày diễn ra, thì có thể cho update lại ngày bắt đầu + ngày kết thúc + số lượng để phát hành lại voucher, nhưng số lượng phải lớn hơn số lượng người đã lấy
    @Override
    @Transactional
    public VoucherResponseDTO updateVoucher(Long id, VoucherUpdateDTO updateDTO) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Voucher không tồn tại với ID: " + id));

        LocalDateTime now = LocalDateTime.now();

        // --- RÀNG BUỘC CHUNG (Áp dụng cho mọi trường hợp) ---

        // 2. Kiểm tra Số lượng tối đa mới có thấp hơn Số lượng đã sử dụng chưa
        if (updateDTO.getMaxUsage() < voucher.getCurrentUsage()) {
            throw new IllegalStateException(String.format(
                    "Không thể đặt số lượng tối đa (%d) thấp hơn số lượng đã sử dụng (%d).",
                    updateDTO.getMaxUsage(),
                    voucher.getCurrentUsage()
            ));
        }

        // 3. Kiểm tra logic ngày (Ngày kết thúc phải sau ngày bắt đầu)
        if (updateDTO.getEndDate().isBefore(updateDTO.getStartDate()) || updateDTO.getEndDate().isEqual(updateDTO.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        }
        // --------------------------------------------------

        // --- LOGIC THEO 3 TRƯỜNG HỢP THỜI GIAN ---
        boolean isBeforeStart = voucher.getStartDate().isAfter(now);
        boolean isAfterEnd = voucher.getEndDate().isBefore(now);

        if (!isBeforeStart && !isAfterEnd) {
            throw new IllegalStateException("Voucher đang trong thời gian hiệu lực. Không thể cập nhật.");
        }

        // --- THỰC HIỆN CẬP NHẬT ---
        voucherMapper.toEntityFromUpdateDTO(updateDTO, voucher);
        return voucherMapper.toResponseDTO(voucherRepository.save(voucher));
    }
    @Override
    @Transactional
    public void deleteVoucher(Long id) {
//  Chỉ cho xóa nếu voucher chưa có ai lấy.
//  Chỉ cho xóa nếu voucher CHƯA BẮT ĐẦU HOẶC ĐÃ KẾT THÚC HOÀN TOÀN mà chưa có ai lấy
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Voucher không tồn tại với ID: " + id));

        LocalDateTime now = LocalDateTime.now();

        // 1. Kiểm tra xem đã có người dùng nào lấy/sử dụng voucher chưa
        if (voucher.getCurrentUsage() > 0) {
            throw new IllegalStateException("Không thể xóa voucher đã có người dùng nhận/sử dụng.");
        }

        // 2. NGHIỆP VỤ MỚI: Chỉ cho phép xóa nếu voucher CHƯA BẮT ĐẦU HOẶC ĐÃ KẾT THÚC HOÀN TOÀN
        boolean isBeforeStart = voucher.getStartDate().isAfter(now);
        boolean isAfterEnd = voucher.getEndDate().isBefore(now);

        if (!isBeforeStart && !isAfterEnd) {
            // Tức là voucher đang trong khoảng thời gian có hiệu lực (đang diễn ra)
            throw new IllegalStateException("Không thể xóa voucher đang diễn ra.");
        }
        // ------------------------------------

        voucherRepository.delete(voucher);
    }

    @Override
    public List<VoucherResponseDTO> getVouchersForCustomer(Long userId) {

        LocalDateTime now = LocalDateTime.now();

        // Lấy tất cả voucher đang còn hiệu lực
        List<Voucher> vouchers = voucherRepository
                .findAllByUserId(userId);

        return voucherMapper.toDTOList(vouchers);
    }

    @Override
    @Transactional
    public VoucherResponseDTO claimVoucher(Long voucherId, Long userId) {
        // 1. Lock voucher tránh race conditions
        Voucher voucher = voucherRepository.lockVoucherById(voucherId);
        if (voucher == null) {
            throw new EntityNotFoundException("Voucher không tồn tại!");
        }

        // 2. Kiểm tra ngày
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate())) {
            throw new IllegalStateException("Chưa đến thời gian phát hành voucher.");
        }
        if ( now.isAfter(voucher.getEndDate())) {
            throw new IllegalStateException("Voucher không còn hiệu lực.");
        }
        // 3. Kiểm tra user đã lấy voucher chưa
        if (voucherUserRepository.existsByVoucherIdAndUserId(voucherId, userId)) {
            throw new IllegalStateException("Bạn đã nhận voucher này rồi.");
        }

        // 4. Kiểm tra số lượng
        if (voucher.getCurrentUsage() >= voucher.getMaxUsage()) {
            throw new IllegalStateException("Voucher đã hết số lượng!");
        }

        // 5. Cập nhật số lượng
        voucher.setCurrentUsage(voucher.getCurrentUsage() + 1);
        voucherRepository.save(voucher);

        // 6. Lưu lịch sử vào Voucher_User
        User user = userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("User not found."));
        VoucherUser voucherUser = new VoucherUser();
        voucherUser.setVoucher(voucher);
        voucherUser.setUser(user);
        voucherUser.setStatus("CLAIMED");
        voucherUser.setClaimDate(LocalDateTime.now()); // <-- set claim time
        voucherUser.setUsedDate(null);

        voucherUserRepository.save(voucherUser);

        return voucherMapper.toResponseDTO(voucher);
    }
}