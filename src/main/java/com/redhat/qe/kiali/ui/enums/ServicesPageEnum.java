package com.redhat.qe.kiali.ui.enums;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class ServicesPageEnum {

    public enum FILTER implements IEnumString {
        SERVICE_NAME("Service Name"),
        ISTIO_SIDECAR("Istio Sidecar"),
        NAMESPACE("Namespace");

        public static FILTER fromText(String text) {
            return (FILTER) EnumStringHelper.fromText(text, values());
        }

        public static FILTER get(int id) {
            return (FILTER) EnumHelper.get(id, values());
        }

        private final String name;

        private FILTER(String name) {
            this.name = name;
        }

        @Override
        public String getText() {
            return this.name;
        }
    }

    public enum SORT implements IEnumString {
        NAMESPACE("Namespace"),
        SERVICE_NAME("Service Name"),
        ISTIO_SIDECAR("Istio Sidecar"),
        ERROR_RATE("Error Rate");

        public static SORT fromText(String text) {
            return (SORT) EnumStringHelper.fromText(text, values());
        }

        public static SORT get(int id) {
            return (SORT) EnumHelper.get(id, values());
        }

        private final String name;

        private SORT(String name) {
            this.name = name;
        }

        @Override
        public String getText() {
            return this.name;
        }
    }
}
