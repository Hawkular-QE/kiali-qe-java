package com.redhat.qe.kiali.ui.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.redhat.qe.kiali.ui.bugtracker.Blocker;
import com.redhat.qe.kiali.ui.bugtracker.BugTracker;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class BlockersUpdateListener implements IAnnotationTransformer {
    @SuppressWarnings("rawtypes")
    @Override
    public void transform(ITestAnnotation annotation, Class clazz, Constructor constructor, Method testMethod) {
        // if there is no blockers on list. skip below steps
        if (BugTracker.getBlockers().isEmpty()) {
            return;
        }
        String className = testMethod.getDeclaringClass().getCanonicalName();
        String methodName = testMethod.getName();
        _logger.debug("Checking blocker status for [Class:{}, Method:{}]", className, methodName);
        if (BugTracker.getBlockers().get(className) != null) {
            updateEnabled(annotation, BugTracker.getBlockers().get(className));
        }
        if (BugTracker.getBlockers().get(className + "." + methodName) != null) {
            updateEnabled(annotation, BugTracker.getBlockers().get(className + "." + methodName));
        }
        if (!annotation.getEnabled()) {
            _logger.info("Disabled this test:[class{}, method:{}]", className, methodName);
        }
    }

    private void updateEnabled(ITestAnnotation annotation, Blocker blocker) {
        _logger.debug("Checking this {}", blocker);
        if (blocker.isBlocked()) {
            annotation.setEnabled(false);
        }
    }
}
