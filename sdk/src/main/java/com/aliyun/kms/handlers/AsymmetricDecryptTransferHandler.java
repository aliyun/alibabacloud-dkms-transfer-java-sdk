package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.kms.utils.Constants;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.AsymmetricDecryptRequest;
import com.aliyuncs.kms.model.v20160120.DecryptResponse;
import com.aliyuncs.utils.StringUtils;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AsymmetricDecryptTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.DecryptRequest, com.aliyun.dkms.gcs.sdk.models.DecryptResponse> {

    private static final List<String> responseHeaders = new ArrayList<String>() {{
        add(Constants.MIGRATION_KEY_VERSION_ID_KEY);
    }};

    private final Client client;
    private final String action;

    public AsymmetricDecryptTransferHandler(Client client, String action) {
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
    public com.aliyun.dkms.gcs.sdk.models.DecryptRequest buildDKMSRequest(AcsRequest request, RuntimeOptions runtimeOptions) throws ClientException {
        AsymmetricDecryptRequest asymmetricDecryptKmsRequest = (AsymmetricDecryptRequest) request;
        if (StringUtils.isEmpty(asymmetricDecryptKmsRequest.getCiphertextBlob())) {
            throw newMissingParameterClientException("CiphertextBlob");
        }
        com.aliyun.dkms.gcs.sdk.models.DecryptRequest asymmetricDecryptDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.DecryptRequest();
        asymmetricDecryptDKmsRequest.setKeyId(asymmetricDecryptKmsRequest.getKeyId());
        asymmetricDecryptDKmsRequest.setCiphertextBlob(base64.decode(asymmetricDecryptKmsRequest.getCiphertextBlob()));
        asymmetricDecryptDKmsRequest.setAlgorithm(asymmetricDecryptKmsRequest.getAlgorithm());
        return asymmetricDecryptDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.DecryptResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.DecryptRequest dkmsRequest, RuntimeOptions runtimeOptions) throws Exception {
        runtimeOptions.setResponseHeaders(responseHeaders);
        return client.decryptWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(AcsRequest request, com.aliyun.dkms.gcs.sdk.models.DecryptResponse response) throws ClientException {
        Map<String, String> responseHeaders = response.getResponseHeaders();
        String keyVersionId = null;
        if (responseHeaders != null) {
            keyVersionId = responseHeaders.get(Constants.MIGRATION_KEY_VERSION_ID_KEY);
        }
        final DecryptResponse asymmetricDecryptKmsResponse = new DecryptResponse();
        asymmetricDecryptKmsResponse.setKeyId(response.getKeyId());
        asymmetricDecryptKmsResponse.setKeyVersionId(keyVersionId);
        asymmetricDecryptKmsResponse.setPlaintext(base64.encodeToString(response.getPlaintext()));
        asymmetricDecryptKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(getHttpContent(request.getSysAcceptFormat(), asymmetricDecryptKmsResponse), StandardCharsets.UTF_8.displayName(), request.getSysAcceptFormat());
        return httpResponse;
    }
}
