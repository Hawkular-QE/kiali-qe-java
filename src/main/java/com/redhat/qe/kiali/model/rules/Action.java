package com.redhat.qe.kiali.model.rules;

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
public class Action {
    private String handler;
    private List<String> instances;

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        Action action = (Action) other;

        if (!KialiUtils.equalsCheck(handler, action.handler) || !handler.equals(action.handler)) {
            return false;
        }
        if (!instances.equals(action.instances)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((handler == null) ? 0 : handler.hashCode());
        result = prime * result + ((instances == null) ? 0 : instances.hashCode());
        return result;
    }
}
