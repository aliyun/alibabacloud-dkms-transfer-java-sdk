package com.aliyun.kms;

import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.kms.handlers.*;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.AlibabaCloudCredentials;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.Credential;
import com.aliyuncs.auth.Signer;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.IClientProfile;

import java.util.HashMap;
import java.util.Map;

import static com.aliyun.kms.utils.Constants.*;


public class KmsTransferAcsClient extends DefaultAcsClient {
    private boolean ignoreSSLCerts = false;
    private Client client;
    private final Map<String, KmsTransferHandler> handlers = new HashMap<String, KmsTransferHandler>();
    private boolean isUseKmsShareGateway;

    public KmsTransferAcsClient(Config config) throws ClientException {
        super();
        try {
            setUserAgent(config);
            client = new Client(config);
        } catch (Exception e) {
            throw new ClientException(e);
        }
        initKmsTransferHandlers();
    }

    public KmsTransferAcsClient(String regionId, Config config) throws ClientException {
        super(regionId);
        try {
            setUserAgent(config);
            client = new Client(config);
        } catch (Exception e) {
            throw new ClientException(e);
        }
        initKmsTransferHandlers();
    }

    public KmsTransferAcsClient(String regionId) throws ClientException {
        super(regionId);
        this.isUseKmsShareGateway = true;
    }

    public KmsTransferAcsClient(IClientProfile profile, Config config) {
        super(profile);
        ignoreSSLCerts = profile.getHttpClientConfig() == null ? false : profile.getHttpClientConfig().isIgnoreSSLCerts();
        try {
            setUserAgent(config);
            client = new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initKmsTransferHandlers();
    }

    public KmsTransferAcsClient(IClientProfile profile) {
        super(profile);
        this.isUseKmsShareGateway = true;
    }

    public KmsTransferAcsClient(IClientProfile profile, AlibabaCloudCredentials credentials, Config config) {
        super(profile, credentials);
        ignoreSSLCerts = profile.getHttpClientConfig() == null ? false : profile.getHttpClientConfig().isIgnoreSSLCerts();
        try {
            setUserAgent(config);
            client = new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initKmsTransferHandlers();
    }

    public KmsTransferAcsClient(IClientProfile profile, AlibabaCloudCredentials credentials) {
        super(profile, credentials);
        this.isUseKmsShareGateway = true;
    }

    public KmsTransferAcsClient(IClientProfile profile, AlibabaCloudCredentialsProvider credentialsProvider, Config config) {
        super(profile, credentialsProvider);
        ignoreSSLCerts = profile.getHttpClientConfig() == null ? false : profile.getHttpClientConfig().isIgnoreSSLCerts();
        try {
            setUserAgent(config);
            client = new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initKmsTransferHandlers();
    }

    public KmsTransferAcsClient(IClientProfile profile, AlibabaCloudCredentialsProvider credentialsProvider) {
        super(profile, credentialsProvider);
        this.isUseKmsShareGateway = true;
    }

    private void initKmsTransferHandlers() {
        handlers.put(ENCRYPT_API_NAME, new EncryptTransferHandler(client, ENCRYPT_API_NAME));
        handlers.put(DECRYPT_API_NAME, new DecryptTransferHandler(client, DECRYPT_API_NAME));
        handlers.put(ASYMMETRIC_ENCRYPT_API_NAME, new AsymmetricEncryptTransferHandler(client, ASYMMETRIC_ENCRYPT_API_NAME));
        handlers.put(ASYMMETRIC_DECRYPT_API_NAME, new AsymmetricDecryptTransferHandler(client, ASYMMETRIC_DECRYPT_API_NAME));
        handlers.put(ASYMMETRIC_SIGN_API_NAME, new AsymmetricSignTransferHandler(client, ASYMMETRIC_SIGN_API_NAME));
        handlers.put(ASYMMETRIC_VERIFY_API_NAME, new AsymmetricVerifyTransferHandler(client, ASYMMETRIC_VERIFY_API_NAME));
        handlers.put(GENERATE_DATA_KEY_API_NAME, new GenerateDataKeyTransferHandler(client, GENERATE_DATA_KEY_API_NAME));
        handlers.put(GENERATE_DATA_KEY_WITHOUT_PLAINTEXT_API_NAME, new GenerateDataKeyWithoutPlaintextTransferHandler(client, GENERATE_DATA_KEY_WITHOUT_PLAINTEXT_API_NAME));
        handlers.put(GET_PUBLIC_KEY_API_NAME, new GetPublicKeyTransferHandler(client, GET_PUBLIC_KEY_API_NAME));
        handlers.put(GET_SECRET_VALUE_API_NAME, new GetSecretValueTransferHandler(client, GET_SECRET_VALUE_API_NAME));
    }

    @Override
    public <T extends AcsResponse> HttpResponse doAction(AcsRequest<T> request, String regionId, Credential credential) throws ClientException, ServerException {
        if (!isUseKmsShareGateway && handlers.containsKey(request.getSysActionName())) {
            return dispatchDKmsAction(handlers.get(request.getSysActionName()), request);
        }
        return super.doAction(request, regionId, credential);
    }

    @Override
    public <T extends AcsResponse> HttpResponse doAction(AcsRequest<T> request, boolean autoRetry, int maxRetryCounts, IClientProfile profile) throws ClientException, ServerException {
        if (handlers.containsKey(request.getSysActionName()) && !isUseKmsShareGateway) {
            return dispatchDKmsAction(handlers.get(request.getSysActionName()), request);
        }
        return super.doAction(request, autoRetry, maxRetryCounts, profile);
    }

    @Override
    public <T extends AcsResponse> HttpResponse doAction(AcsRequest<T> request, boolean autoRetry, int maxRetryNumber, String regionId, Credential credential, Signer signer, FormatType format) throws ClientException, ServerException {
        if (handlers.containsKey(request.getSysActionName()) && !isUseKmsShareGateway) {
            return dispatchDKmsAction(handlers.get(request.getSysActionName()), request);
        }
        return super.doAction(request, autoRetry, maxRetryNumber, regionId, credential, signer, format);
    }

    private <T extends AcsResponse> HttpResponse dispatchDKmsAction(KmsTransferHandler handler, AcsRequest<T> request) throws ClientException, ServerException {
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.setIgnoreSSL(ignoreSSLCerts);
        return handler.handlerDKmsRequestWithOptions(request, runtimeOptions);
    }

    private void setUserAgent(Config config) {
        if (config.getUserAgent() != null) {
            config.setUserAgent(SDK_USER_AGENT + " " + config.getUserAgent());
        } else {
            config.setUserAgent(SDK_USER_AGENT);
        }
    }
}
