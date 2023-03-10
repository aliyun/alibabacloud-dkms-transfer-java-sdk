package com.aliyun.kms;

import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.kms.utils.ConfigUtils;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.kms.model.v20160120.*;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;


public class TestKmsTransferAcsClient {

    Properties properties;
    IAcsClient client;
    IAcsClient kmsClient;

    @Before
    public void init() throws Exception {
        properties = ConfigUtils.loadParam("");
        Config config = new Config();
        config.setProtocol(properties.getProperty("config.protocol"));
        config.setClientKeyContent(properties.getProperty("config.clientKeyContent"));
        config.setPassword(properties.getProperty("config.password"));
        config.setEndpoint(properties.getProperty("config.endpoint"));
        DefaultProfile profile = DefaultProfile.getProfile(properties.getProperty("config.kms.regionId"), System.getenv("accessKeyId"), System.getenv("accessKeySecret"));
        HttpClientConfig clientConfig = HttpClientConfig.getDefault();
        clientConfig.setIgnoreSSLCerts(Boolean.parseBoolean(properties.getProperty("ignoreSSLCerts")));
        profile.setHttpClientConfig(clientConfig);
        client = new KmsTransferAcsClient(profile, config);
        kmsClient = new DefaultAcsClient(profile);
    }

    @Test
    public void kmsEncrypt() {
        kmsEncrypt0();
    }

    @Test
    public void kmsDecrypt() {
        kmsDecrypt0("");
    }

    @Test
    public void kmsGenerateDataKey() {
        GenerateDataKeyRequest request = new GenerateDataKeyRequest();
        request.setKeyId(properties.getProperty("encrypt.keyId"));
//        request.setKeySpec(properties.getProperty("generateDataKey.keySpec"));
        request.setEncryptionContext(properties.getProperty("encrypt.encryption.context"));
        try {
            GenerateDataKeyResponse response = kmsClient.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
//            DecryptResponse decryptResponse = kmsDecrypt0(response.getCiphertextBlob());
            DecryptResponse decryptResponse = decrypt(response.getCiphertextBlob());
            assert response.getPlaintext().equals(decryptResponse.getPlaintext());
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EncryptResponse kmsEncrypt0() {
        EncryptRequest request = new EncryptRequest();
        request.setKeyId(properties.getProperty("encrypt.keyId"));
        request.setPlaintext(properties.getProperty("encrypt.plaintext"));
        String context = properties.getProperty("encrypt.encryption.context");
        request.setEncryptionContext(context);
        try {
            EncryptResponse response = kmsClient.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
            return response;
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public DecryptResponse kmsDecrypt0(String ciphertextBlob) {
        DecryptRequest request = new DecryptRequest();
        try {
//            request.setCiphertextBlob(kmsEncrypt0().getCiphertextBlob());
            if (StringUtils.isEmpty(ciphertextBlob)) {
                request.setCiphertextBlob(encrypt().getCiphertextBlob());
            } else {
                request.setCiphertextBlob(ciphertextBlob);
            }
            request.setEncryptionContext(properties.getProperty("encrypt.encryption.context"));
            DecryptResponse response = kmsClient.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
            return response;
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAsymmetricEncrypt() throws Exception {
        asymmetricEncrypt();
    }

    @Test
    public void testAsymmetricDecrypt() throws Exception {
        AsymmetricDecryptRequest request = new AsymmetricDecryptRequest();
        request.setCiphertextBlob(asymmetricEncrypt().getCiphertextBlob());
//        request.setCiphertextBlob(properties.getProperty("asymmetric.decrypt.ciphertextBlob"));
        request.setKeyId(properties.getProperty("asymmetric.encrypt.keyId"));
        request.setAlgorithm(properties.getProperty("asymmetric.encrypt.algorithm"));
        try {
            AsymmetricDecryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
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
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public DecryptResponse decrypt(String ciphertextBlob) throws Exception {
        try {
            DecryptRequest request = new DecryptRequest();
            request.setCiphertextBlob(ciphertextBlob);
            request.setEncryptionContext(properties.getProperty("encrypt.encryption.context"));
            DecryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
            return response;
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
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
        //properties.getProperty("decrypt.ciphertextBlob")
        decrypt(kmsEncrypt0().getCiphertextBlob());
//        decrypt(encrypt().getCiphertextBlob());
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
            DecryptResponse decryptResponse = decrypt(response.getCiphertextBlob());
            assert response.getPlaintext().equals(decryptResponse.getPlaintext());
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGenerateDataKeyWithoutPlaintext() throws Exception {
        GenerateDataKeyWithoutPlaintextRequest request = new GenerateDataKeyWithoutPlaintextRequest();
        request.setKeyId(properties.getProperty("encrypt.keyId"));
//        request.setKeySpec(properties.getProperty("generateDataKey.keySpec"));
        request.setEncryptionContext(properties.getProperty("encrypt.encryption.context"));
        try {
            GenerateDataKeyWithoutPlaintextResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
            decrypt(response.getCiphertextBlob());
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
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
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetSecretValue() throws Exception {
        GetSecretValueRequest request = new GetSecretValueRequest();
        request.setSecretName(properties.getProperty("secret.name"));
//        request.setSysAcceptFormat(FormatType.XML);
        try {
            GetSecretValueResponse response = client.getAcsResponse(request);
            System.out.printf("SecretData: %s%n", response.getSecretData());
            System.out.printf("ExtendedConfig: %s%n", response.getExtendedConfig());
            System.out.printf("CreateTime: %s%n", response.getCreateTime());
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    public EncryptResponse encrypt() throws Exception {
        EncryptRequest request = new EncryptRequest();
        request.setKeyId(properties.getProperty("encrypt.keyId"));

        request.setPlaintext(properties.getProperty("encrypt.plaintext"));
        String context = properties.getProperty("encrypt.encryption.context");
        request.setEncryptionContext(context);
        try {
            EncryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
            return response;
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (ClientException e) {
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
            throw new RuntimeException(e);
        } catch (ClientException e) {
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
            throw new RuntimeException(e);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

}