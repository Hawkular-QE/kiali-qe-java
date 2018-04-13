package com.redhat.qe.kiali.model.services;

import java.util.List;

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
public class Health {
    private Envoy envoy;
    private List<DeploymentStatus> deploymentStatuses;

    // available only in GUI
    private String iconText;

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        Health item = (Health) other;
        if (!KialiUtils.equalsCheck(envoy, item.envoy)) {
            return false;
        }
        if (!KialiUtils.equalsCheck(deploymentStatuses, item.deploymentStatuses)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((envoy == null) ? 0 : envoy.hashCode());
        result = prime * result + ((deploymentStatuses == null) ? 0 : deploymentStatuses.hashCode());
        return result;
    }
}
