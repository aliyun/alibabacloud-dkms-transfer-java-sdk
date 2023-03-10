package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.kms.utils.ArrayUtils;
import com.aliyun.kms.utils.Constants;
import com.aliyun.kms.utils.EncryptionContextUtils;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.EncryptRequest;
import com.aliyuncs.kms.model.v20160120.EncryptResponse;
import com.aliyuncs.utils.StringUtils;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EncryptTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.EncryptRequest, com.aliyun.dkms.gcs.sdk.models.EncryptResponse> {

    private static final List<String> responseHeaders = new ArrayList<String>() {{
        add(Constants.MIGRATION_KEY_VERSION_ID_KEY);
    }};

    private final Client client;
    private final String action;

    public EncryptTransferHandler(Client client, String action) {
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
    public <T extends AcsResponse> com.aliyun.dkms.gcs.sdk.models.EncryptRequest buildDKMSRequest(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException {
        EncryptRequest encryptKmsRequest = (EncryptRequest) request;
        if (StringUtils.isEmpty(encryptKmsRequest.getPlaintext())) {
            throw newMissingParameterClientException("Plaintext");
        }
        com.aliyun.dkms.gcs.sdk.models.EncryptRequest encryptDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.EncryptRequest();
        encryptDKmsRequest.setKeyId(encryptKmsRequest.getKeyId());
        encryptDKmsRequest.setPlaintext(encryptKmsRequest.getPlaintext().getBytes(StandardCharsets.UTF_8));
        String encryptionContext = encryptKmsRequest.getEncryptionContext();
        if (!StringUtils.isEmpty(encryptionContext)) {
            encryptDKmsRequest.setAad(EncryptionContextUtils.sortAndEncode(encryptionContext, StandardCharsets.UTF_8));
        }
        return encryptDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.EncryptResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.EncryptRequest encryptRequest, RuntimeOptions runtimeOptions) throws Exception {
        runtimeOptions.setResponseHeaders(responseHeaders);
        return this.client.encryptWithOptions(encryptRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(AcsRequest request, com.aliyun.dkms.gcs.sdk.models.EncryptResponse response) throws ClientException {
        final EncryptResponse encryptKmsResponse = new EncryptResponse();
        encryptKmsResponse.setKeyId(response.getKeyId());
        Map<String, String> responseHeaders = response.getResponseHeaders();
        String keyVersionId;
        if (responseHeaders == null || responseHeaders.size() == 0 || StringUtils.isEmpty(keyVersionId = responseHeaders.get(Constants.MIGRATION_KEY_VERSION_ID_KEY))) {
            throw new ClientException(String.format("Can not found response headers parameter[%s]", Constants.MIGRATION_KEY_VERSION_ID_KEY));
        }
        byte[] ciphertextBlob = ArrayUtils.concatAll(keyVersionId.getBytes(StandardCharsets.UTF_8), response.getIv(), response.getCiphertextBlob());
        encryptKmsResponse.setKeyVersionId(keyVersionId);
        encryptKmsResponse.setCiphertextBlob(base64.encodeToString(ciphertextBlob));
        encryptKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(getHttpContent(request.getSysAcceptFormat(), encryptKmsResponse), StandardCharsets.UTF_8.displayName(), request.getSysAcceptFormat());
        return httpResponse;
    }

}
