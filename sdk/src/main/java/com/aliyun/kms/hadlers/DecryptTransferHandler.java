package com.aliyun.kms.hadlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.kms.utils.Constants;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.DecryptRequest;
import com.aliyuncs.kms.model.v20160120.DecryptResponse;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import static com.aliyun.kms.utils.Constants.*;

public class DecryptTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.DecryptRequest, com.aliyun.dkms.gcs.sdk.models.DecryptResponse> {

    private static final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Base64 base64 = new Base64();

    private final Client client;
    private final String action;

    public DecryptTransferHandler(Client client, String action) {
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
    public <T extends AcsResponse> com.aliyun.dkms.gcs.sdk.models.DecryptRequest buildDKMSRequest(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException {
        DecryptRequest decryptKmsRequest = (DecryptRequest) request;
        if (StringUtils.isEmpty(decryptKmsRequest.getCiphertextBlob())) {
            throw newMissingParameterClientException("CiphertextBlob");
        }
        com.aliyun.dkms.gcs.sdk.models.DecryptRequest decryptDKmsRequest = new com.aliyun.dkms.gcs.sdk.models.DecryptRequest();
        byte[] ciphertextBlob = base64.decode(decryptKmsRequest.getCiphertextBlob());
        byte[] ektIdBytes = Arrays.copyOfRange(ciphertextBlob, 0, Constants.EKT_ID_LENGTH);
        byte[] ivBytes = Arrays.copyOfRange(ciphertextBlob, Constants.EKT_ID_LENGTH, Constants.EKT_ID_LENGTH + GCM_IV_LENGTH);
        byte[] ciphertextBytes = Arrays.copyOfRange(ciphertextBlob, Constants.EKT_ID_LENGTH + GCM_IV_LENGTH, ciphertextBlob.length);
        String ektId = new String(ektIdBytes, StandardCharsets.UTF_8);
        decryptDKmsRequest.setRequestHeaders(new HashMap<String, String>() {{
            put(Constants.MIGRATION_KEY_VERSION_ID_KEY, ektId);
        }});
        decryptDKmsRequest.setIv(ivBytes);
        decryptDKmsRequest.setCiphertextBlob(ciphertextBytes);
        if (!StringUtils.isEmpty(decryptKmsRequest.getEncryptionContext())) {
            decryptDKmsRequest.setAad(decryptKmsRequest.getEncryptionContext().getBytes(StandardCharsets.UTF_8));
        }
        return decryptDKmsRequest;
    }

    @Override
    public com.aliyun.dkms.gcs.sdk.models.DecryptResponse callDKMS(com.aliyun.dkms.gcs.sdk.models.DecryptRequest dkmsRequest, RuntimeOptions runtimeOptions) throws Exception {
        return client.decryptWithOptions(dkmsRequest, runtimeOptions);
    }

    @Override
    public HttpResponse transferResponse(com.aliyun.dkms.gcs.sdk.models.DecryptResponse response) {
        final DecryptResponse decryptKmsResponse = new DecryptResponse();
        decryptKmsResponse.setKeyId(response.getKeyId());
        decryptKmsResponse.setPlaintext(base64.encodeAsString(response.getPlaintext()));
        decryptKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(gson.toJson(decryptKmsResponse).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8.displayName(), FormatType.JSON);
        return httpResponse;
    }
}
