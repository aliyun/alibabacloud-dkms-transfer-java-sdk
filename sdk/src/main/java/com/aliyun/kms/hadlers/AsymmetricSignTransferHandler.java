package com.aliyun.kms.hadlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.tea.utils.StringUtils;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.AsymmetricSignRequest;
import com.aliyuncs.kms.model.v20160120.AsymmetricSignResponse;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;

import static com.aliyun.kms.utils.Constants.DIGEST_MESSAGE_TYPE;

public class AsymmetricSignTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.SignRequest, com.aliyun.dkms.gcs.sdk.models.SignResponse> {

    private final Client client;
    private final String action;

    public AsymmetricSignTransferHandler(Client client, String action) {
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
    public com.aliyun.dkms.gcs.sdk.models.SignRequest buildDKMSRequest(AcsRequest request, RuntimeOptions runtimeOptions) throws ClientException {
        AsymmetricSignRequest asymmetricSignKmsRequest = (AsymmetricSignRequest) request;
        if (StringUtils.isEmpty(asymmetricSignKmsRequest.getDigest())) {
            throw newMissingParameterClientException("Digest");
        }
        com.aliyun.dkms.gcs.sdk.models.SignRequest signDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.SignRequest();
        signDKmsRequest.setKeyId(asymmetricSignKmsRequest.getKeyId());
        signDKmsRequest.setAlgorithm(asymmetricSignKmsRequest.getAlgorithm());
        signDKmsRequest.setMessage(base64.decode(asymmetricSignKmsRequest.getDigest()));
        signDKmsRequest.setMessageType(DIGEST_MESSAGE_TYPE);
        return signDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.SignResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.SignRequest dkmsRequest, RuntimeOptions runtimeOptions) throws Exception {
        return client.signWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(com.aliyun.dkms.gcs.sdk.models.SignResponse response) {
        final com.aliyuncs.kms.model.v20160120.AsymmetricSignResponse asymmetricSignKmsResponse = new AsymmetricSignResponse();
        asymmetricSignKmsResponse.setKeyId(response.getKeyId());
        asymmetricSignKmsResponse.setValue(base64.encodeToString(response.getSignature()));
        asymmetricSignKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(gson.toJson(asymmetricSignKmsResponse).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.displayName(), FormatType.JSON);
        return httpResponse;
    }

}
