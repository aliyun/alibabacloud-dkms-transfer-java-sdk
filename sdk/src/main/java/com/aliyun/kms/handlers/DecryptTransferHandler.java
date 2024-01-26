package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.kms.utils.Constants;
import com.aliyun.kms.utils.EncryptionContextUtils;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.kms.model.v20160120.DecryptRequest;
import com.aliyuncs.kms.model.v20160120.DecryptResponse;
import com.aliyuncs.utils.StringUtils;
import org.apache.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.aliyun.kms.utils.Constants.*;

public class DecryptTransferHandler implements KmsTransferHandler<com.aliyun.dkms.gcs.sdk.models.DecryptRequest, com.aliyun.dkms.gcs.sdk.models.DecryptResponse> {

    private static final List<String> responseHeaders = new ArrayList<String>() {{
        add(Constants.MIGRATION_KEY_VERSION_ID_KEY);
    }};

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
        if (ciphertextBlob.length <= Constants.EKT_ID_LENGTH + GCM_IV_LENGTH) {
            throw newInvalidParameterClientException("CiphertextBlob");
        }
        byte[] ektIdBytes = Arrays.copyOfRange(ciphertextBlob, 0, Constants.EKT_ID_LENGTH);
        byte[] ivBytes = Arrays.copyOfRange(ciphertextBlob, Constants.EKT_ID_LENGTH, Constants.EKT_ID_LENGTH + GCM_IV_LENGTH);
        byte[] ciphertextBytes = Arrays.copyOfRange(ciphertextBlob, Constants.EKT_ID_LENGTH + GCM_IV_LENGTH, ciphertextBlob.length);
        String ektId = new String(ektIdBytes, StandardCharsets.UTF_8);
        decryptDKmsRequest.setRequestHeaders(new HashMap<String, String>() {{
            put(Constants.MIGRATION_KEY_VERSION_ID_KEY, ektId);
        }});
        decryptDKmsRequest.setIv(ivBytes);
        decryptDKmsRequest.setCiphertextBlob(ciphertextBytes);
        String encryptionContext = decryptKmsRequest.getEncryptionContext();
        if (!StringUtils.isEmpty(encryptionContext)) {
            decryptDKmsRequest.setAad(EncryptionContextUtils.sortAndEncode(encryptionContext, StandardCharsets.UTF_8));
        }
        return decryptDKmsRequest;
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
        final DecryptResponse decryptKmsResponse = new DecryptResponse();
        decryptKmsResponse.setKeyId(response.getKeyId());
        decryptKmsResponse.setKeyVersionId(keyVersionId);
        decryptKmsResponse.setPlaintext(new String(response.getPlaintext(), StandardCharsets.UTF_8));
        decryptKmsResponse.setRequestId(response.getRequestId());
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatus(HttpStatus.SC_OK);
        httpResponse.setHttpContent(getHttpContent(request.getSysAcceptFormat(), decryptKmsResponse), StandardCharsets.UTF_8.displayName(), request.getSysAcceptFormat());
        return httpResponse;
    }
}
