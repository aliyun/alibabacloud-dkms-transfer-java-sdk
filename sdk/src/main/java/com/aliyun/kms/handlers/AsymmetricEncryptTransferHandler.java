package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.dkms.gcs.sdk.models.EncryptRequest;
import com.aliyun.dkms.gcs.sdk.models.EncryptResponse;
import com.aliyun.kms.utils.Constants;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.AsymmetricEncryptRequest;
import com.aliyuncs.kms.model.v20160120.AsymmetricEncryptResponse;
import com.aliyuncs.utils.StringUtils;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AsymmetricEncryptTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.EncryptRequest, com.aliyun.dkms.gcs.sdk.models.EncryptResponse> {

    private static final List<String> responseHeaders = new ArrayList<String>() {{
        add(Constants.MIGRATION_KEY_VERSION_ID_KEY);
    }};

    private final Client client;
    private final String action;

    public AsymmetricEncryptTransferHandler(Client client, String action) {
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
    public <T extends AcsResponse> EncryptRequest buildDKMSRequest(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException {
        AsymmetricEncryptRequest asymmetricEncryptKmsRequest = (AsymmetricEncryptRequest) request;
        if (StringUtils.isEmpty(asymmetricEncryptKmsRequest.getPlaintext())) {
            throw newMissingParameterClientException("Plaintext");
        }
        com.aliyun.dkms.gcs.sdk.models.EncryptRequest asymmetricEncryptDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.EncryptRequest();
        asymmetricEncryptDKmsRequest.setKeyId(asymmetricEncryptKmsRequest.getKeyId());
        asymmetricEncryptDKmsRequest.setPlaintext(base64.decode(asymmetricEncryptKmsRequest.getPlaintext()));
        asymmetricEncryptDKmsRequest.setAlgorithm(asymmetricEncryptKmsRequest.getAlgorithm());
        return asymmetricEncryptDKmsRequest;
    }

    @Override
    public EncryptResponse callDKMS(EncryptRequest dkmsRequest, RuntimeOptions runtimeOptions) throws Exception {
        runtimeOptions.setResponseHeaders(responseHeaders);
        return client.encryptWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(AcsRequest request, EncryptResponse response) throws ClientException {
        Map<String, String> responseHeaders = response.getResponseHeaders();
        String keyVersionId = null;
        if (responseHeaders != null) {
            keyVersionId = responseHeaders.get(Constants.MIGRATION_KEY_VERSION_ID_KEY);
        }
        final AsymmetricEncryptResponse asymmetricEncryptKmsResponse = new AsymmetricEncryptResponse();
        asymmetricEncryptKmsResponse.setKeyId(response.getKeyId());
        asymmetricEncryptKmsResponse.setKeyVersionId(keyVersionId);
        asymmetricEncryptKmsResponse.setCiphertextBlob(base64.encodeToString(response.getCiphertextBlob()));
        asymmetricEncryptKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(getHttpContent(request.getSysAcceptFormat(), asymmetricEncryptKmsResponse), StandardCharsets.UTF_8.displayName(), request.getSysAcceptFormat());
        return httpResponse;
    }
}
