package com.bkb.scanner.dto;

import com.bkb.scanner.entity.CustomerLifecycleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LifecycleStatusCountDTO {
    private CustomerLifecycleStatus status;
    private Long count;
}