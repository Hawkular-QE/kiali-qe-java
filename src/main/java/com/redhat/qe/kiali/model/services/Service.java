package com.redhat.qe.kiali.model.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.qe.kiali.KialiUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    private String name;
    private String namespace;
    private Integer replicas;

    @JsonProperty("available_replicas")
    private Integer availableReplicas;

    @JsonProperty("unavailable_replicas")
    private Integer unavailableReplicas;

    // this is used only on GUI
    private String replicasStatus;

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        Service ser = (Service) other;
        if (!KialiUtils.equalsCheck(name, ser.name)) {
            return false;
        }
        if (!KialiUtils.equalsCheck(namespace, ser.namespace)) {
            return false;
        }
        if (!KialiUtils.equalsCheck(replicas, ser.replicas)) {
            return false;
        }
        if (!KialiUtils.equalsCheck(availableReplicas, ser.availableReplicas)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((replicas == null) ? 0 : replicas.hashCode());
        result = prime * result + ((availableReplicas == null) ? 0 : availableReplicas.hashCode());
        result = prime * result + ((unavailableReplicas == null) ? 0 : unavailableReplicas.hashCode());
        return result;
    }
}
