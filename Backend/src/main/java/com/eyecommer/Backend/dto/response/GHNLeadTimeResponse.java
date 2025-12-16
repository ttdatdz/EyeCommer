package com.eyecommer.Backend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class GHNLeadTimeResponse {
    private Long leadtime;    // seconds
}


