package com.redhat.qe.rest.core;

import static com.google.common.base.Preconditions.checkNotNull;

import com.redhat.qe.rest.core.RestHttpClient.TRUST_HOST_TYPE;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ClientInfo {
    private final String endpointUrl;
    private final String username;
    private final String password;
    private final String token;
    private final String version;
    private final TRUST_HOST_TYPE trustHostType;
    private final RestHeader header;

    public ClientInfo(String endpointUri, String username, String password,
            String token, String version, TRUST_HOST_TYPE trustHostType, RestHeader header) {
        this.endpointUrl = checkNotNull(endpointUri);
        this.username = username;
        this.password = password;
        this.token = token;
        this.version = version;
        this.trustHostType = trustHostType;
        this.header = header;
    }
}
