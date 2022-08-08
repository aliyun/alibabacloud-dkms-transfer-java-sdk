package com.aliyun.kms.hadlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.AsymmetricVerifyRequest;
import com.aliyuncs.kms.model.v20160120.AsymmetricVerifyResponse;
import com.aliyuncs.utils.StringUtils;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;

import static com.aliyun.kms.utils.Constants.DIGEST_MESSAGE_TYPE;

public class AsymmetricVerifyTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.VerifyRequest, com.aliyun.dkms.gcs.sdk.models.VerifyResponse> {

    private final Client client;
    private final String action;

    public AsymmetricVerifyTransferHandler(Client client, String action) {
        this.client = client;
        this.action = action;
    }

    @Override
    public Client getClient() {
        return this.client;
    }

    @Override
    public String getAction() {
        return null;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.VerifyRequest buildDKMSRequest(AcsRequest request, RuntimeOptions runtimeOptions) throws ClientException {
        AsymmetricVerifyRequest asymmetricVerifyKmsRequest = (AsymmetricVerifyRequest) request;
        if (StringUtils.isEmpty(asymmetricVerifyKmsRequest.getDigest())) {
            throw newMissingParameterClientException("Digest");
        }
        if (StringUtils.isEmpty(asymmetricVerifyKmsRequest.getValue())) {
            throw newMissingParameterClientException("Value");
        }
        com.aliyun.dkms.gcs.sdk.models.VerifyRequest verifyDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.VerifyRequest();
        verifyDKmsRequest.setKeyId(asymmetricVerifyKmsRequest.getKeyId());
        verifyDKmsRequest.setAlgorithm(asymmetricVerifyKmsRequest.getAlgorithm());
        verifyDKmsRequest.setMessage(base64.decode(asymmetricVerifyKmsRequest.getDigest()));
        verifyDKmsRequest.setMessageType(DIGEST_MESSAGE_TYPE);
        verifyDKmsRequest.setSignature(base64.decode(asymmetricVerifyKmsRequest.getValue()));
        return verifyDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.VerifyResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.VerifyRequest dkmsRequest, RuntimeOptions runtimeOptions) throws Exception {
        return client.verifyWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(com.aliyun.dkms.gcs.sdk.models.VerifyResponse response) {
        final com.aliyuncs.kms.model.v20160120.AsymmetricVerifyResponse asymmetricVerifyKmsResponse = new AsymmetricVerifyResponse();
        asymmetricVerifyKmsResponse.setKeyId(response.getKeyId());
        asymmetricVerifyKmsResponse.setValue(response.getValue());
        asymmetricVerifyKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(gson.toJson(asymmetricVerifyKmsResponse).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.displayName(), FormatType.JSON);
        return httpResponse;
    }
}
