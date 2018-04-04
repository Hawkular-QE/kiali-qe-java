package com.redhat.qe.kiali.ui.listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.IExecutionListener;
import org.testng.annotations.ITestAnnotation;

import com.redhat.qe.bugtracker.Blocker;
import com.redhat.qe.bugtracker.Blockers;
import com.redhat.qe.bugtracker.BugTrackerFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class BlockersUpdateListener implements IAnnotationTransformer, IExecutionListener {

    @Override
    public void onExecutionStart() {
        // initialize bug tracker factory
        BugTrackerFactory.initialize();
        if (!BugTrackerFactory.INITIALIZE_SUCCESS.get()) {
            _logger.error("Failed to initialize bug tracker factory. Blocker feature will be disabled.");
        }
    }

    @Override
    public void onExecutionFinish() {
        // for now no actions required
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void transform(ITestAnnotation annotation, Class clazz, Constructor constructor, Method testMethod) {
        // if initialize failed do not execute this feature
        if (!BugTrackerFactory.INITIALIZE_SUCCESS.get()) {
            return;
        }
        String className = testMethod.getDeclaringClass().getCanonicalName();
        String methodName = className + "." + testMethod.getName();

        if (testMethod.getDeclaringClass().isAnnotationPresent(Blockers.class)) {
            Blockers blockers = testMethod.getDeclaringClass().getAnnotation(Blockers.class);
            // update blockers
            BugTrackerFactory.updateBlocker(className, blockers.value());
            _logger.info("TestClass:{}, blockers:{}", className, blockers.value());
        }
        if (testMethod.isAnnotationPresent(Blockers.class)) {
            Blockers blockers = testMethod.getAnnotation(Blockers.class);
            // update blockers
            BugTrackerFactory.updateBlocker(methodName, blockers.value());
            _logger.info("TestMethod:{}, blockers:{}", methodName, blockers.value());
        }

        _logger.debug("Checking blocker status for [Class:{}, Method:{}]", className, methodName);
        if (BugTrackerFactory.getBlockers().get(className) != null) {
            updateEnabled(annotation, BugTrackerFactory.getBlockers().get(className));
        }
        if (BugTrackerFactory.getBlockers().get(methodName) != null) {
            updateEnabled(annotation, BugTrackerFactory.getBlockers().get(methodName));
        }
        if (!annotation.getEnabled()) {
            _logger.info("Disabled this test:[class:{}, method:{}]", className, methodName);
        }
    }

    private void updateEnabled(ITestAnnotation annotation, Blocker blocker) {
        _logger.debug("Checking this {}", blocker);
        if (blocker.isBlocked()) {
            annotation.setEnabled(false);
        }
    }
}
