package com.redhat.qe.kiali.ui;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@ToString
@Data
@Builder
public class ImageCompareResult {
    private STATUS status;
    private float matchPercentage;

    public enum STATUS {
        MATCHED,
        SIZE_MISMATCH,
        PIXEL_MISMATCH
    };
}
