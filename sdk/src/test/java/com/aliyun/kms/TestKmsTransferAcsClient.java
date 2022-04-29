package com.aliyun.kms;

import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.kms.utils.ConfigUtils;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.kms.model.v20160120.*;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;


public class TestKmsTransferAcsClient {

    Properties properties;
    IAcsClient client;

    @Before
    public void init() throws Exception {
        properties = ConfigUtils.loadParam("");
        Config config = new Config();
        config.setProtocol(properties.getProperty("config.protocol"));
        config.setClientKeyContent(properties.getProperty("config.clientKeyContent"));
        config.setPassword(properties.getProperty("config.password"));
        config.setEndpoint(properties.getProperty("config.endpoint"));
        DefaultProfile profile = DefaultProfile.getProfile(properties.getProperty("config.regionId"), properties.getProperty("config.ak"), properties.getProperty("config.sk"));
        HttpClientConfig clientConfig = HttpClientConfig.getDefault();
        clientConfig.setIgnoreSSLCerts(Boolean.parseBoolean(properties.getProperty("ignoreSSLCerts")));
        profile.setHttpClientConfig(clientConfig);
        client = new KmsTransferAcsClient(profile, config);
    }

    @Test
    public void testAsymmetricEncrypt() throws Exception {
        asymmetricEncrypt();
    }

    @Test
    public void testAsymmetricDecrypt() throws Exception {
        AsymmetricDecryptRequest request = new AsymmetricDecryptRequest();
//        request.setCiphertextBlob(asymmetricEncrypt().getCiphertextBlob());
        request.setCiphertextBlob(properties.getProperty("asymmetric.decrypt.ciphertextBlob"));
        request.setKeyId(properties.getProperty("asymmetric.encrypt.keyId"));
        request.setAlgorithm(properties.getProperty("asymmetric.encrypt.algorithm"));
        try {
            AsymmetricDecryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAsymmetricSign() throws Exception {
        asymmetricSign();
    }

    @Test
    public void testAsymmetricVerify() throws Exception {
        AsymmetricVerifyRequest request = new AsymmetricVerifyRequest();
        request.setKeyId(properties.getProperty("asymmetric.sign.keyId"));
        request.setAlgorithm(properties.getProperty("asymmetric.sign.algorithm"));
        request.setDigest(properties.getProperty("asymmetric.sign.digest"));
        request.setValue(asymmetricSign().getValue());
//        request.setValue(properties.getProperty("asymmetric.sign.signature"));
        try {
            AsymmetricVerifyResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("value: %s%n", response.getValue());
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testEncrypt() throws Exception {
        encrypt();
    }

    private static final Base64 base64 = new Base64();

    @Test
    public void testDecrypt() throws Exception {
        try {
            DecryptRequest request = new DecryptRequest();
//            request.setCiphertextBlob(encrypt().getCiphertextBlob());
            request.setCiphertextBlob(properties.getProperty("decrypt.ciphertextBlob"));
            request.setEncryptionContext(properties.getProperty("encrypt.encryption.context"));
            DecryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGenerateDataKey() throws Exception {
        GenerateDataKeyRequest request = new GenerateDataKeyRequest();
        request.setKeyId(properties.getProperty("encrypt.keyId"));
        request.setEncryptionContext(properties.getProperty("encrypt.encryption.context"));
        try {
            GenerateDataKeyResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGenerateDataKeyWithoutPlaintext() throws Exception {
        GenerateDataKeyWithoutPlaintextRequest request = new GenerateDataKeyWithoutPlaintextRequest();
        request.setKeyId(properties.getProperty("encrypt.keyId"));
        request.setKeySpec(properties.getProperty("generateDataKey.keySpec"));
        request.setEncryptionContext(properties.getProperty("encrypt.encryption.context"));
        try {
            GenerateDataKeyWithoutPlaintextResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetPublicKey() throws Exception {
        GetPublicKeyRequest request = new GetPublicKeyRequest();
        request.setKeyId(properties.getProperty("asymmetric.encrypt.keyId"));
        try {
            GetPublicKeyResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("PublicKey: %s%n", response.getPublicKey());
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

    public EncryptResponse encrypt() throws Exception {
        EncryptRequest request = new EncryptRequest();
        request.setKeyId(properties.getProperty("encrypt.keyId"));
        request.setPlaintext(properties.getProperty("encrypt.plaintext"));
        request.setEncryptionContext(properties.getProperty("encrypt.encryption.context"));
        try {
            EncryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
            return response;
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

    public AsymmetricSignResponse asymmetricSign() throws Exception {
        AsymmetricSignRequest request = new AsymmetricSignRequest();
        request.setKeyId(properties.getProperty("asymmetric.sign.keyId"));
        request.setAlgorithm(properties.getProperty("asymmetric.sign.algorithm"));
        request.setDigest(properties.getProperty("asymmetric.sign.digest"));
        try {
            AsymmetricSignResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("Value: %s%n", response.getValue());
            return response;
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

    private AsymmetricEncryptResponse asymmetricEncrypt() throws Exception {
        AsymmetricEncryptRequest request = new AsymmetricEncryptRequest();
        request.setPlaintext(properties.getProperty("asymmetric.encrypt.plaintext"));
        request.setKeyId(properties.getProperty("asymmetric.encrypt.keyId"));
        request.setKeyVersionId(properties.getProperty("asymmetric.encrypt.keyVersionId"));
        request.setAlgorithm(properties.getProperty("asymmetric.encrypt.algorithm"));
        try {
            AsymmetricEncryptResponse response = client.getAcsResponse(request);
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            return response;
        } catch (ServerException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
            throw new RuntimeException(e);
        }
    }

}