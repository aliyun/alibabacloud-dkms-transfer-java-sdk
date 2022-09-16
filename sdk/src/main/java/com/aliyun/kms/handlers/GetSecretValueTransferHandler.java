package com.aliyun.kms.hadlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.GetSecretValueRequest;
import com.aliyuncs.kms.model.v20160120.GetSecretValueResponse;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;

public class GetSecretValueTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.GetSecretValueRequest, com.aliyun.dkms.gcs.sdk.models.GetSecretValueResponse> {

    private final Client client;
    private final String action;

    public GetSecretValueTransferHandler(Client client, String action) {
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
    public <T extends AcsResponse> com.aliyun.dkms.gcs.sdk.models.GetSecretValueRequest buildDKMSRequest(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException {
        GetSecretValueRequest getSecretValueKmsRequest = (GetSecretValueRequest) request;
        com.aliyun.dkms.gcs.sdk.models.GetSecretValueRequest getSecretValueDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.GetSecretValueRequest();
        getSecretValueDKmsRequest.setSecretName(getSecretValueKmsRequest.getSecretName());
        getSecretValueDKmsRequest.setFetchExtendedConfig(getSecretValueKmsRequest.getFetchExtendedConfig());
        getSecretValueDKmsRequest.setVersionId(getSecretValueKmsRequest.getVersionId());
        getSecretValueDKmsRequest.setVersionStage(getSecretValueKmsRequest.getVersionStage());
        return getSecretValueDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.GetSecretValueResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.GetSecretValueRequest getSecretValueRequest, RuntimeOptions runtimeOptions) throws Exception {
        return this.client.getSecretValueWithOptions(getSecretValueRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(com.aliyun.dkms.gcs.sdk.models.GetSecretValueResponse response) throws ClientException {
        final GetSecretValueResponse getSecretValueResponse = new GetSecretValueResponse();
        getSecretValueResponse.setAutomaticRotation(response.getAutomaticRotation());
        getSecretValueResponse.setCreateTime(response.getCreateTime());
        getSecretValueResponse.setExtendedConfig(response.getExtendedConfig());
        getSecretValueResponse.setLastRotationDate(response.getLastRotationDate());
        getSecretValueResponse.setNextRotationDate(response.getNextRotationDate());
        getSecretValueResponse.setRotationInterval(response.getRotationInterval());
        getSecretValueResponse.setSecretData(response.getSecretData());
        getSecretValueResponse.setSecretDataType(response.getSecretDataType());
        getSecretValueResponse.setSecretName(response.getSecretName());
        getSecretValueResponse.setSecretType(response.getSecretType());
        getSecretValueResponse.setVersionId(response.getVersionId());
        getSecretValueResponse.setVersionStages(response.getVersionStages());
        getSecretValueResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(gson.toJson(getSecretValueResponse).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.displayName(), FormatType.JSON);
        return httpResponse;
    }

}
