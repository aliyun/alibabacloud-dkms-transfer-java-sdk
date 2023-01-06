package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.dkms.gcs.sdk.models.EncryptRequest;
import com.aliyun.dkms.gcs.sdk.models.EncryptResponse;
import com.aliyun.kms.utils.ArrayUtils;
import com.aliyun.kms.utils.Constants;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.GenerateDataKeyRequest;
import com.aliyuncs.kms.model.v20160120.GenerateDataKeyResponse;
import com.aliyuncs.utils.StringUtils;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.aliyun.kms.utils.Constants.*;
import static com.aliyun.kms.utils.KmsErrorCodeTransferUtils.INVALID_PARAMETER_ERROR_CODE;
import static com.aliyun.kms.utils.KmsErrorCodeTransferUtils.INVALID_PARAMETER_KEY_SPEC_ERROR_MESSAGE;

public class GenerateDataKeyTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyRequest, com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyResponse> {

    private static final List<String> responseHeaders = new ArrayList<String>() {{
        add(Constants.MIGRATION_KEY_VERSION_ID_KEY);
    }};
    private final Client client;
    private final String action;

    public GenerateDataKeyTransferHandler(Client client, String action) {
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
    public <T extends AcsResponse> com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyRequest buildDKMSRequest(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException {
        GenerateDataKeyRequest generateDataKeyKmsRequest = (GenerateDataKeyRequest) request;
        com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyRequest generateDataKeyDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyRequest();
        generateDataKeyDKmsRequest.setKeyId(generateDataKeyKmsRequest.getKeyId());
        String keySpec = generateDataKeyKmsRequest.getKeySpec();
        Integer numberOfBytes = generateDataKeyKmsRequest.getNumberOfBytes();
        if (numberOfBytes == null) {
            if (StringUtils.isEmpty(keySpec) || KMS_KEY_PAIR_AES_256.equals(keySpec)) {
                numberOfBytes = NUMBER_OF_BYTES_AES_256;
            } else if (KMS_KEY_PAIR_AES_128.equals(keySpec)) {
                numberOfBytes = NUMBER_OF_BYTES_AES_128;
            } else {
                throw new ClientException(INVALID_PARAMETER_ERROR_CODE, INVALID_PARAMETER_KEY_SPEC_ERROR_MESSAGE);
            }
        }
        generateDataKeyDKmsRequest.setNumberOfBytes(numberOfBytes);
        if (!StringUtils.isEmpty(generateDataKeyKmsRequest.getEncryptionContext())) {
            generateDataKeyDKmsRequest.setAad(generateDataKeyKmsRequest.getEncryptionContext().getBytes(StandardCharsets.UTF_8));
        }
        return generateDataKeyDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyRequest dkmsRequest, RuntimeOptions runtimeOptions) throws Exception {
        runtimeOptions.setResponseHeaders(responseHeaders);
        com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyResponse generateDataKeyResponse = client.generateDataKeyWithOptions(dkmsRequest, runtimeOptions);
        EncryptRequest encryptRequest = new EncryptRequest();
        encryptRequest.setKeyId(dkmsRequest.getKeyId());
        encryptRequest.setPlaintext(base64.encodeAsString(generateDataKeyResponse.getPlaintext()).getBytes(StandardCharsets.UTF_8));
        EncryptResponse encryptResponse = client.encryptWithOptions(encryptRequest, runtimeOptions);
        generateDataKeyResponse.setCiphertextBlob(encryptResponse.getCiphertextBlob());
        generateDataKeyResponse.setIv(encryptResponse.getIv());
        return generateDataKeyResponse;
    }

    @Override
    public HttpResponse transferResponse(AcsRequest request, com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyResponse response) throws ClientException {
        Map<String, String> responseHeaders = response.getResponseHeaders();
        String keyVersionId;
        if (responseHeaders == null || responseHeaders.size() == 0 || StringUtils.isEmpty(keyVersionId = responseHeaders.get(Constants.MIGRATION_KEY_VERSION_ID_KEY))) {
            throw new ClientException(String.format("Can not found response headers parameter[%s]", Constants.MIGRATION_KEY_VERSION_ID_KEY));
        }
        byte[] ciphertextBlob = ArrayUtils.concatAll(keyVersionId.getBytes(StandardCharsets.UTF_8), response.getIv(), response.getCiphertextBlob());
        final GenerateDataKeyResponse generateDataKeyKmsResponse = new GenerateDataKeyResponse();
        generateDataKeyKmsResponse.setKeyId(response.getKeyId());
        generateDataKeyKmsResponse.setKeyVersionId(keyVersionId);
        generateDataKeyKmsResponse.setRequestId(response.getRequestId());
        generateDataKeyKmsResponse.setPlaintext(base64.encodeToString(response.getPlaintext()));
        generateDataKeyKmsResponse.setCiphertextBlob(base64.encodeToString(ciphertextBlob));
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(getHttpContent(request.getSysAcceptFormat(), generateDataKeyKmsResponse), StandardCharsets.UTF_8.displayName(), request.getSysAcceptFormat());
        return httpResponse;
    }
}
