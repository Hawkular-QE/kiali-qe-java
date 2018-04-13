package com.redhat.qe.kiali.model.services;

import com.redhat.qe.kiali.KialiUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Builder
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Envoy {
    private Integer healthy;
    private Integer total;

    // available only in GUI
    private String iconText;

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        Envoy item = (Envoy) other;
        if (!KialiUtils.equalsCheck(healthy, item.healthy)) {
            return false;
        }
        if (!KialiUtils.equalsCheck(total, item.total)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((healthy == null) ? 0 : healthy.hashCode());
        result = prime * result + ((total == null) ? 0 : total.hashCode());
        return result;
    }
}
