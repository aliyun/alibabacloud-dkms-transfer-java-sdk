package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.kms.utils.Constants;
import com.aliyun.tea.utils.StringUtils;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.GetPublicKeyRequest;
import com.aliyuncs.kms.model.v20160120.GetPublicKeyResponse;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPublicKeyTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.GetPublicKeyRequest, com.aliyun.dkms.gcs.sdk.models.GetPublicKeyResponse> {

    private static final List<String> responseHeaders = new ArrayList<String>() {{
        add(Constants.MIGRATION_KEY_VERSION_ID_KEY);
    }};

    private final Client client;
    private final String action;

    public GetPublicKeyTransferHandler(Client client, String action) {
        this.client = client;
        this.action = action;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    @Override
    public String getAction() {
        return this.action;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.GetPublicKeyRequest buildDKMSRequest(AcsRequest request, RuntimeOptions runtimeOptions) {
        GetPublicKeyRequest getPublicKeyKmsRequest = (GetPublicKeyRequest) request;
        com.aliyun.dkms.gcs.sdk.models.GetPublicKeyRequest getPublicKeyDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.GetPublicKeyRequest();
        getPublicKeyDKmsRequest.setKeyId(getPublicKeyKmsRequest.getKeyId());
        final String keyVersionId = getPublicKeyKmsRequest.getKeyVersionId();
        if(!StringUtils.isEmpty(keyVersionId)) {
            getPublicKeyDKmsRequest.setRequestHeaders(new HashMap<String, String>() {{
                put(Constants.MIGRATION_KEY_VERSION_ID_KEY, keyVersionId);
            }});
        }
        return getPublicKeyDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.GetPublicKeyResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.GetPublicKeyRequest dkmsRequest, RuntimeOptions runtimeOptions) throws Exception {
        runtimeOptions.setResponseHeaders(responseHeaders);
        return client.getPublicKeyWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(AcsRequest request, com.aliyun.dkms.gcs.sdk.models.GetPublicKeyResponse response) throws ClientException {
        Map<String, String> responseHeaders = response.getResponseHeaders();
        String keyVersionId = null;
        if (responseHeaders != null) {
            keyVersionId = responseHeaders.get(Constants.MIGRATION_KEY_VERSION_ID_KEY);
        }
        final com.aliyuncs.kms.model.v20160120.GetPublicKeyResponse getPublicKeyKmsResponse = new GetPublicKeyResponse();
        getPublicKeyKmsResponse.setKeyId(response.getKeyId());
        getPublicKeyKmsResponse.setKeyVersionId(keyVersionId);
        getPublicKeyKmsResponse.setRequestId(response.getRequestId());
        getPublicKeyKmsResponse.setPublicKey(response.getPublicKey());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(getHttpContent(request.getSysAcceptFormat(), getPublicKeyKmsResponse), StandardCharsets.UTF_8.displayName(), request.getSysAcceptFormat());
        return httpResponse;
    }
}
