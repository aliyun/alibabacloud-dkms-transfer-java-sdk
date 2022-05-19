package com.aliyun.kms.hadlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.GenerateDataKeyRequest;
import com.aliyuncs.kms.model.v20160120.GenerateDataKeyResponse;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;

import static com.aliyun.kms.utils.Constants.*;
import static com.aliyun.kms.utils.KmsErrorCodeTransferUtils.INVALID_PARAMETER_ERROR_CODE;
import static com.aliyun.kms.utils.KmsErrorCodeTransferUtils.INVALID_PARAMETER_KEY_SPEC_ERROR_MESSAGE;

public class GenerateDataKeyTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyRequest, com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyResponse> {

    private static final Base64 base64 = new Base64();
    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().disableHtmlEscaping().create();

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
        return client.generateDataKeyWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(com.aliyun.dkms.gcs.sdk.models.GenerateDataKeyResponse response) {
        final GenerateDataKeyResponse generateDataKeyKmsResponse = new GenerateDataKeyResponse();
        generateDataKeyKmsResponse.setKeyId(response.getKeyId());
        generateDataKeyKmsResponse.setRequestId(response.getRequestId());
        generateDataKeyKmsResponse.setPlaintext(base64.encodeAsString(response.getPlaintext()));
        generateDataKeyKmsResponse.setCiphertextBlob(base64.encodeAsString(response.getCiphertextBlob()));
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(gson.toJson(generateDataKeyKmsResponse).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.displayName(), FormatType.JSON);
        return httpResponse;
    }
}
