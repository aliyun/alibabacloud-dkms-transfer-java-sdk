package com.aliyun.kms.hadlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.dkms.gcs.sdk.models.EncryptRequest;
import com.aliyun.dkms.gcs.sdk.models.EncryptResponse;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.AsymmetricEncryptRequest;
import com.aliyuncs.kms.model.v20160120.AsymmetricEncryptResponse;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;

public class AsymmetricEncryptTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.EncryptRequest, com.aliyun.dkms.gcs.sdk.models.EncryptResponse> {

    private static final Base64 base64 = new Base64();
    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().disableHtmlEscaping().create();

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
        return client.encryptWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(EncryptResponse response) {
        final AsymmetricEncryptResponse asymmetricEncryptKmsResponse = new AsymmetricEncryptResponse();
        asymmetricEncryptKmsResponse.setKeyId(response.getKeyId());
        asymmetricEncryptKmsResponse.setCiphertextBlob(base64.encodeAsString(response.getCiphertextBlob()));
        asymmetricEncryptKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(gson.toJson(asymmetricEncryptKmsResponse).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.displayName(), FormatType.JSON);
        return httpResponse;
    }
}
