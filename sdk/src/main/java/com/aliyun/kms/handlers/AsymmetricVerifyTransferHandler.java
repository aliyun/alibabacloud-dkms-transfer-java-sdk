package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.kms.utils.Constants;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.AsymmetricVerifyRequest;
import com.aliyuncs.kms.model.v20160120.AsymmetricVerifyResponse;
import com.aliyuncs.utils.StringUtils;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.aliyun.kms.utils.Constants.DIGEST_MESSAGE_TYPE;

public class AsymmetricVerifyTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.VerifyRequest, com.aliyun.dkms.gcs.sdk.models.VerifyResponse> {

    private static final List<String> responseHeaders = new ArrayList<String>() {{
        add(Constants.MIGRATION_KEY_VERSION_ID_KEY);
    }};

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
        runtimeOptions.setResponseHeaders(responseHeaders);
        return client.verifyWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(AcsRequest request, com.aliyun.dkms.gcs.sdk.models.VerifyResponse response) throws ClientException {
        Map<String, String> responseHeaders = response.getResponseHeaders();
        String keyVersionId = null;
        if (responseHeaders != null) {
            keyVersionId = responseHeaders.get(Constants.MIGRATION_KEY_VERSION_ID_KEY);
        }
        final com.aliyuncs.kms.model.v20160120.AsymmetricVerifyResponse asymmetricVerifyKmsResponse = new AsymmetricVerifyResponse();
        asymmetricVerifyKmsResponse.setKeyId(response.getKeyId());
        asymmetricVerifyKmsResponse.setKeyVersionId(keyVersionId);
        asymmetricVerifyKmsResponse.setValue(response.getValue());
        asymmetricVerifyKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(getHttpContent(request.getSysAcceptFormat(), asymmetricVerifyKmsResponse), StandardCharsets.UTF_8.displayName(), request.getSysAcceptFormat());
        return httpResponse;
    }
}
