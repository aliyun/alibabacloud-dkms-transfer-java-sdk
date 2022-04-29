package com.aliyun.kms.hadlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.GetPublicKeyRequest;
import com.aliyuncs.kms.model.v20160120.GetPublicKeyResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;

public class GetPublicKeyTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.GetPublicKeyRequest, com.aliyun.dkms.gcs.sdk.models.GetPublicKeyResponse> {

    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().disableHtmlEscaping().create();

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
        return getPublicKeyDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.GetPublicKeyResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.GetPublicKeyRequest dkmsRequest, RuntimeOptions runtimeOptions) throws Exception {
        return client.getPublicKeyWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(com.aliyun.dkms.gcs.sdk.models.GetPublicKeyResponse response) {
        final com.aliyuncs.kms.model.v20160120.GetPublicKeyResponse getPublicKeyKmsResponse = new GetPublicKeyResponse();
        getPublicKeyKmsResponse.setKeyId(response.getKeyId());
        getPublicKeyKmsResponse.setRequestId(response.getRequestId());
        getPublicKeyKmsResponse.setPublicKey(response.getPublicKey());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(gson.toJson(getPublicKeyKmsResponse).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.displayName(), FormatType.JSON);
        return httpResponse;
    }
}
