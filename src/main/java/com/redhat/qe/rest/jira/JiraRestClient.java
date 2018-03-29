package com.redhat.qe.rest.jira;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Map;

import com.redhat.qe.kiali.rest.core.KialiHeader;
import com.redhat.qe.kiali.rest.core.KialiHttpClient;
import com.redhat.qe.kiali.rest.core.KialiHttpResponse;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

public class JiraRestClient extends KialiHttpClient {

    private String baseUrl;
    private String username;
    private String password;

    private KialiHeader header;

    public JiraRestClient(String baseUrl, String username, String password) {
        this(baseUrl, username, password, TRUST_HOST_TYPE.DEFAULT);
    }

    public JiraRestClient(String baseUrl, String username, String password, TRUST_HOST_TYPE trustHostType) {
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
        header = KialiHeader.getDefault();
        header.addJsonContentType();
        header.addAuthorization(username, password);
    }

    public static ArrayList<String> getList(String values) {
        ArrayList<String> list = new ArrayList<>();
        String[] _values = values.split(",");
        for (String _value : _values) {
            list.add(_value.trim());
        }
        return list;
    }

    public Map<String, Object> issue(String idOrName) {
        return issue(idOrName, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> issue(String idOrName, Map<String, Object> queryParameters) {
        KialiHttpResponse response = doGet(
                baseUrl + MessageFormat.format("/rest/api/latest/issue/{0}", idOrName),
                queryParameters,
                header, STATUS_CODE.OK.getCode());
        return (Map<String, Object>) readValue(response.getEntity(),
                mapResolver().get(Map.class, String.class, Object.class));
    }

}
