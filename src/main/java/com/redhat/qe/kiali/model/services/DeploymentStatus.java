package com.redhat.qe.kiali.model.services;

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
public class DeploymentStatus {
    private String name;
    private Integer replicas;
    private Integer available;
    // available only in GUI
    private String iconText;
}
