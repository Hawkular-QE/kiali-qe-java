package com.redhat.qe.kiali.ui.enums;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class RootPageEnum {
    public enum MAIN_MENU implements IEnumString {
        GRAPH("Graph"),
        SERVICES("Services"),
        ISTIO_MIXER("Istio Mixer"),
        DISTRIBUTED_TRACING("Distributed Tracing");

        public static MAIN_MENU fromText(String text) {
            return (MAIN_MENU) EnumStringHelper.fromText(text, values());
        }

        public static MAIN_MENU get(int id) {
            return (MAIN_MENU) EnumHelper.get(id, values());
        }

        private final String name;

        private MAIN_MENU(String name) {
            this.name = name;
        }

        @Override
        public String getText() {
            return this.name;
        }
    }

    public enum TOP_RIGHT_MENU implements IEnumString {
        ABOUT("About");

        public static TOP_RIGHT_MENU fromText(String text) {
            return (TOP_RIGHT_MENU) EnumStringHelper.fromText(text, values());
        }

        public static TOP_RIGHT_MENU get(int id) {
            return (TOP_RIGHT_MENU) EnumHelper.get(id, values());
        }

        private final String name;

        private TOP_RIGHT_MENU(String name) {
            this.name = name;
        }

        @Override
        public String getText() {
            return this.name;
        }
    }

    public enum VERSION implements IEnumString {
        KIALI_UI("kiali-ui"),
        KIALI_CORE("kiali"),
        ISTIO("Istio"),
        PROMETHEUS("Prometheus"),
        KUBERNETES("Kubernetes");

        public static VERSION fromText(String text) {
            return (VERSION) EnumStringHelper.fromText(text, values());
        }

        public static VERSION get(int id) {
            return (VERSION) EnumHelper.get(id, values());
        }

        private final String name;

        private VERSION(String name) {
            this.name = name;
        }

        @Override
        public String getText() {
            return this.name;
        }
    }
}
