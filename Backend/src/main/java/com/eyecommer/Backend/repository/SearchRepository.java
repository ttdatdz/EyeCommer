package com.eyecommer.Backend.repository;

import com.eyecommer.Backend.dto.response.PageResponse;
import com.eyecommer.Backend.model.Address;
import com.eyecommer.Backend.model.User;
import com.eyecommer.Backend.repository.critetia.SearchCriteria;
import com.eyecommer.Backend.repository.critetia.UserSearchQueryCriteriaConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
@Slf4j
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private static final String LIKE_FORMAT = "%%%s%%";
    public PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy) {
        log.info("Execute search user with keyword={}", search);

        //Viết lệnh JPQL
        StringBuilder sqlQuery = new StringBuilder("SELECT new com.example.demo.dto.response.UserDetailResponse(\n" +
                "    u.id, u.firstName, u.lastName, u.email, u.status, \n" +
                "    u.gender, u.dateOfBirth, u.phone, u.username, u.type\n" +
                ") FROM User u WHERE 1=1");
        //Gắn thêm search vào lệnh JPQL
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" AND lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" OR lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" OR lower(u.email) like lower(:email)");
        }
        //Gắn thêm sort vào lệnh JPQL
        if (StringUtils.hasLength(sortBy)) {
            // firstName:asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                sqlQuery.append(String.format(" ORDER BY u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        // Lấy danh sách user theo câu lệnh đã build bên trên
            //Tạo lệnh JPQL từ chuỗi đã build bên trên
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        //Gắn giá trị search vào từng tham số firstName,lastName,email
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("lastName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("email", String.format(LIKE_FORMAT, search));
        }
        //xác định từ bản ghi nào bắt đầu lấy.
        selectQuery.setFirstResult(pageNo*pageSize);
        //→ giới hạn số lượng bản ghi tối đa lấy ra
        selectQuery.setMaxResults(pageSize);
        //Thực thi query, trả về danh sách kết quả
        List<?> users = selectQuery.getResultList();

        // Đếm record theo các điều kiện tìm kiếm
            //build câu lệnh đếm record
        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(*) FROM User u");
        //Gắn điều kiện tìm kiếm
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" WHERE lower(u.firstName) like lower(?1)");
            sqlCountQuery.append(" OR lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" OR lower(u.email) like lower(?3)");
        }
        //Tạo câu JPQL từ chuỗi đã build bên trên
        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        //Gắn giá trị vào các tham số trong JPQL
        if (StringUtils.hasLength(search)) {
            countQuery.setParameter(1, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(2, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(3, String.format(LIKE_FORMAT, search));
            countQuery.getSingleResult();
        }
        //Thực thi truy vấn trả về kết quả duy nhất, ép sang kiểu long
        Long totalElements = (Long) countQuery.getSingleResult();
        log.info("totalElements={}", totalElements);
        //Tạo pageable(yêu cầu phân trang)
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        //PageImpl là một class implement (cài đặt) của interface Page<T>.được dùng khi bạn tự viết query
        //Page là interface chứa data của 1 trang + metadata như totalPage...vv
        Page<?> page = new PageImpl<>(users, pageable, totalElements);
        //Trả về object PageResponse
        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(users)
                .build();
    }


    public PageResponse<?> searchUserByCriteria(int pageNo, int pageSize, String sortBy, String address, String... search) {
        log.info("Search user with search={} and sortBy={}", search, sortBy);

        //list điều kiện tìm kiếm
        List<SearchCriteria> criteriaList = new ArrayList<>();

        if (search.length > 0) {
            Pattern pattern = Pattern.compile("(\\w+?)([:><])(.*)");
            for (String s : search) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
                }
            }
        }

        List<User> users = getUsers(pageNo, pageSize, criteriaList, address, sortBy);

        Long totalElements = getTotalElements(criteriaList);

        Page<User> page = new PageImpl<>(users, PageRequest.of(pageNo, pageSize), totalElements);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(users)
                .build();
    }

    private List<User> getUsers(int pageNo, int pageSize, List<SearchCriteria> criteriaList, String address, String sortBy) {
        log.info("-------------- getUsers --------------");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder(); //lấy criteriaBuilder từ EntityManager
        //tạo ra khung query(CriteriaQuery) với kiểu dữ liệu mong muốn trả về là User
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        //Thiết lập From là User,Select Là User và trả về root để có thể truy cập vào field/cột
        Root<User> userRoot = query.from(User.class);
        //Tạo predicate với giá trị ban đầu là true
        Predicate userPredicate = criteriaBuilder.conjunction();
        //Tạo searchConsumer để chuyển đổi điều kiện tìm kiếm -> predicate
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(userPredicate, criteriaBuilder, userRoot);

//        if (StringUtils.hasLength(address)) {
//            Join<Address, User> userAddressJoin = userRoot.join("addresses");
//            Predicate addressPredicate = criteriaBuilder.equal(userAddressJoin.get("city"), address);
//            query.where(userPredicate, addressPredicate); //nối 2 cái predicate vào r đặt nó vào trong mệnh đề where
//        } else {
//            criteriaList.forEach(searchConsumer); //duyệt qua criteriaList, truyền từng phần tử vào searchConsumer
//            userPredicate = searchConsumer.getPredicate();
//            query.where(userPredicate); //đặt predicate vào mệnh đề where
//        }



        //Duyệt qua criteriaList, truyền từng phần tử vào searchConsumer để chuyển thành predicate
        criteriaList.forEach(searchConsumer);
        userPredicate = searchConsumer.getPredicate();

        if (StringUtils.hasLength(address)) {
            Join<Address, User> userAddressJoin = userRoot.join("addresses");
            Predicate addressPredicate = criteriaBuilder.equal(userAddressJoin.get("city"), address);
            userPredicate = criteriaBuilder.and(userPredicate, addressPredicate);
        }

        query.where(userPredicate);

        Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
        if (StringUtils.hasLength(sortBy)) {
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String fieldName = matcher.group(1);
                String direction = matcher.group(3);
                if (direction.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(userRoot.get(fieldName)));
                } else {
                    query.orderBy(criteriaBuilder.desc(userRoot.get(fieldName)));
                }
            }
        }

        return entityManager.createQuery(query)  //tạo JPQL từ CriteriaQuery
                .setFirstResult(pageNo*pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
    private Long getTotalElements(List<SearchCriteria> params) {
        log.info("-------------- getTotalElements --------------");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, criteriaBuilder, root);
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.select(criteriaBuilder.count(root));
        query.where(predicate);

        return entityManager.createQuery(query).getSingleResult();
    }
}
