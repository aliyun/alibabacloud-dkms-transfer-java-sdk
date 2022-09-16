package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.kms.result.GetSecretValueResult;
import com.aliyun.kms.result.VersionStage;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.GetSecretValueRequest;
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
    public HttpResponse transferResponse(AcsRequest request, com.aliyun.dkms.gcs.sdk.models.GetSecretValueResponse response) throws ClientException {
        final GetSecretValueResult getSecretValueResult = new GetSecretValueResult();
        getSecretValueResult.setAutomaticRotation(response.getAutomaticRotation());
        getSecretValueResult.setCreateTime(response.getCreateTime());
        getSecretValueResult.setExtendedConfig(response.getExtendedConfig());
        getSecretValueResult.setLastRotationDate(response.getLastRotationDate());
        getSecretValueResult.setNextRotationDate(response.getNextRotationDate());
        getSecretValueResult.setRotationInterval(response.getRotationInterval());
        getSecretValueResult.setSecretData(response.getSecretData());
        getSecretValueResult.setSecretDataType(response.getSecretDataType());
        getSecretValueResult.setSecretName(response.getSecretName());
        getSecretValueResult.setSecretType(response.getSecretType());
        getSecretValueResult.setVersionId(response.getVersionId());
        VersionStage versionStage = new VersionStage();
        versionStage.setVersionStage(response.getVersionStages());
        getSecretValueResult.setVersionStages(versionStage);
        getSecretValueResult.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(getHttpContent(request.getSysAcceptFormat(), getSecretValueResult), StandardCharsets.UTF_8.displayName(), request.getSysAcceptFormat());
        return httpResponse;
    }

}
