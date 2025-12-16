package com.eyecommer.Backend.utils;

import com.eyecommer.Backend.dto.request.GHNAvailableServiceRequest;
import com.eyecommer.Backend.dto.request.GHNCreateOrderRequest;
import com.eyecommer.Backend.dto.request.GHNFeeRequest;
import com.eyecommer.Backend.dto.request.GHNLeadTimeRequest;
import com.eyecommer.Backend.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GHNClient {

    @Value("${ghn.token}")
    private String token;

    @Value("${ghn.shop-id}")
    private String shopId;

    @Value("${ghn.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    /* ================= COMMON HEADERS ================= */
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        headers.set("ShopId", shopId);
        return headers;
    }

    /* ================= CREATE ORDER ================= */
    public GHNCreateOrderResponse createOrder(GHNCreateOrderRequest body) {

        HttpEntity<GHNCreateOrderRequest> entity =
                new HttpEntity<>(body, headers());

        ResponseEntity<GHNCreateOrderResponse> response =
                restTemplate.postForEntity(
                        baseUrl + "/shiip/public-api/v2/shipping-order/create",
                        entity,
                        GHNCreateOrderResponse.class
                );

        return response.getBody();
    }

    public GHNFeeResponse calculateFee(GHNFeeRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        headers.set("ShopId", String.valueOf(shopId));

        HttpEntity<GHNFeeRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<GHNResponseDTO<GHNFeeResponse>> response =
                restTemplate.exchange(
                        baseUrl + "/shiip/public-api/v2/shipping-order/fee",
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody().getData();
    }

    /* ================= CANCEL ORDER ================= */
    public void cancelOrder(String shipmentCode) {

        HttpEntity<?> entity = new HttpEntity<>(headers());

        restTemplate.postForEntity(
                baseUrl + "/shiip/public-api/v2/switch-status/cancel",
                new HttpEntity<>(
                        "{\"order_codes\":[\"" + shipmentCode + "\"]}",
                        headers()
                ),
                String.class
        );
    }

    /* ================= LOCATION APIs ================= */
    public List<ProvinceResponseDTO> getProvinces() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token.trim());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GHNResponseDTO<List<ProvinceResponseDTO>>> response =
                restTemplate.exchange(
                        baseUrl + "/shiip/public-api/master-data/province",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody().getData();
    }


    //Get Lead Time
    public GHNLeadTimeResponse getLeadTime(GHNLeadTimeRequest request) {

        HttpEntity<GHNLeadTimeRequest> entity =
                new HttpEntity<>(request, headers());

        ResponseEntity<GHNResponseDTO<GHNLeadTimeResponse>> response =
                restTemplate.exchange(
                        baseUrl + "/shiip/public-api/v2/shipping-order/leadtime",
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody().getData();
    }
    // Get Available Services
    public List<GHNAvailableServiceResponse> getAvailableServices(
            GHNAvailableServiceRequest request
    ) {

        HttpEntity<GHNAvailableServiceRequest> entity =
                new HttpEntity<>(request, headers());

        ResponseEntity<GHNResponseDTO<List<GHNAvailableServiceResponse>>> response =
                restTemplate.exchange(
                        baseUrl + "/shiip/public-api/v2/shipping-order/available-services",
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody().getData();
    }
    public List<DistrictResponseDTO> getDistricts(Integer province_id) {

        HttpEntity<?> entity =
                new HttpEntity<>(province_id, headers());

        ResponseEntity<GHNResponseDTO<List<DistrictResponseDTO>>> response =
                restTemplate.exchange(
                        baseUrl + "/shiip/public-api/master-data/district",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody().getData();
    }

    public List<WardResponseDTO> getWards(Integer districtId) {

        ResponseEntity<GHNResponseDTO<List<WardResponseDTO>>> response =
                restTemplate.exchange(
                        baseUrl + "/shiip/public-api/master-data/ward?district_id=" + districtId,
                        HttpMethod.GET,
                        new HttpEntity<>(headers()),
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody().getData();
    }

}
