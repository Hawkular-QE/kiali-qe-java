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
public class Rule {
    private String name;
    private String namespace;
    private List<Action> actions;

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        Rule rule = (Rule) other;

        if (!KialiUtils.equalsCheck(name, rule.name)) {
            return false;
        }
        if (!KialiUtils.equalsCheck(namespace, rule.namespace)) {
            return false;
        }
        if (!KialiUtils.equalsCheck(actions, rule.actions)) {
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
        result = prime * result + ((actions == null) ? 0 : actions.hashCode());
        return result;
    }
}
