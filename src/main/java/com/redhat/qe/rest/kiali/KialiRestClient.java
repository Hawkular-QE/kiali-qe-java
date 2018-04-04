package com.redhat.qe.rest.kiali;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redhat.qe.kiali.model.Namespace;
import com.redhat.qe.kiali.model.rules.Rule;
import com.redhat.qe.kiali.model.rules.RuleDetail;
import com.redhat.qe.kiali.model.rules.Rules;
import com.redhat.qe.kiali.model.services.Service;
import com.redhat.qe.kiali.model.services.Services;
import com.redhat.qe.rest.core.RestHeader;
import com.redhat.qe.rest.core.RestHttpClient;
import com.redhat.qe.rest.core.RestHttpResponse;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class KialiRestClient extends RestHttpClient {

    private String baseUrl;
    private String username;
    private String password;

    private RestHeader header;

    public KialiRestClient(String baseUrl, String username, String password) {
        this(baseUrl, username, password, TRUST_HOST_TYPE.DEFAULT);
    }

    public KialiRestClient(String baseUrl, String username, String password, TRUST_HOST_TYPE trustHostType) {
        super(trustHostType == null ? TRUST_HOST_TYPE.DEFAULT : trustHostType);
        if (baseUrl.endsWith("/")) {
            this.baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        } else {
            this.baseUrl = baseUrl;
        }
        this.username = username;
        this.password = password;
        initClient();
    }

    private void initClient() {
        header = RestHeader.getDefault();
        header.addJsonContentType();
        header.addAuthorization(username, password);
    }

    @SuppressWarnings("unchecked")
    public List<Namespace> namespaces() {
        RestHttpResponse response = doGet(baseUrl + "/api/namespaces", header, STATUS_CODE.OK.getCode());
        return (List<Namespace>) readValue(response.getEntity(),
                collectionResolver().get(List.class, Namespace.class));
    }

    public String queryRaw(String path) {
        return doGet(path, null).getEntity();
    }

    public String rawPost(String path, HashMap<String, Object> entity) {
        RestHttpResponse response = doPost(baseUrl + "/api/namespaces", header, toJsonString(entity),
                STATUS_CODE.OK.getCode());
        return (String) readValue(response.getEntity(), simpleResolver().get(String.class));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> serviceMetrics(String namespace, String service,
            Map<String, Object> queryParameters) {
        RestHttpResponse response = doGet(
                baseUrl + MessageFormat.format("/api/namespaces/{0}/services/{1}/metrics", namespace, service),
                header, STATUS_CODE.OK.getCode());
        return (Map<String, Object>) readValue(response.getEntity(),
                mapResolver().get(Map.class, String.class, Object.class));
    }

    public Services services(String namespace) {
        RestHttpResponse response = doGet(baseUrl + MessageFormat.format("/api/namespaces/{0}/services", namespace),
                header, STATUS_CODE.OK.getCode());
        return (Services) readValue(response.getEntity(), simpleResolver().get(Services.class));
    }

    public List<Service> services() {
        List<Namespace> namesapces = namespaces();
        List<Service> services = new ArrayList<Service>();
        for (Namespace namespace : namesapces) {
            Services _services = services(namespace.getName());
            for (Service _service : _services.getServices()) {
                _service.setNamespace(_services.getNamespace().getName());
                services.add(_service);
            }
        }
        return services;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> status() {
        RestHttpResponse response = doGet(baseUrl + "/api/status", header, STATUS_CODE.OK.getCode());
        return (Map<String, String>) readValue(response.getEntity(),
                mapResolver().get(Map.class, String.class, String.class));
    }

    public Rules rules(String namespace) {
        RestHttpResponse response = doGet(baseUrl + MessageFormat.format("/api/namespaces/{0}/rules", namespace),
                header, STATUS_CODE.OK.getCode());
        return (Rules) readValue(response.getEntity(), simpleResolver().get(Rules.class));
    }

    public List<Rule> rules() {
        List<Namespace> namesapces = namespaces();
        List<Rule> rules = new ArrayList<Rule>();
        for (Namespace namespace : namesapces) {
            Rules _rules = rules(namespace.getName());
            for (Rule _rule : _rules.getRules()) {
                _rule.setNamespace(_rules.getNamespace().getName());
                rules.add(_rule);
            }
        }
        return rules;
    }

    public RuleDetail rule(String namespace, String rule) {
        RestHttpResponse response = doGet(
                baseUrl + MessageFormat.format("/api/namespaces/{0}/rules/{1}", namespace, rule),
                header, STATUS_CODE.OK.getCode());
        return (RuleDetail) readValue(response.getEntity(), simpleResolver().get(RuleDetail.class));
    }
}
