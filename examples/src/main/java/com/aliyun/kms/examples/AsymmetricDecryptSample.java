package com.aliyun.kms.examples;

import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.kms.KmsTransferAcsClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.kms.model.v20160120.*;
import com.aliyuncs.profile.DefaultProfile;

public class AsymmetricDecryptSample {
    public static void main(String[] args) {
        dkmsAsymmetricDecrypt();
        kmsAsymmetricDecrypt();
    }

    /**
     * 使用KMS API调用DKMS密钥
     */
    private static void dkmsAsymmetricDecrypt() {
        Config config = new Config();
        config.setProtocol("https");
        config.setClientKeyFile("<your-client-key-file>");
        config.setPassword("<your-password>");
        config.setEndpoint("<your-endpoint>");
        DefaultProfile profile = DefaultProfile.getProfile("<your-endpoint>", "<your-access-key-id>", "<your-access-key-secret>");
        HttpClientConfig clientConfig = HttpClientConfig.getDefault();
        //如需跳过https认证,可打开此处注释代码
        //clientConfig.setIgnoreSSLCerts(true);
        profile.setHttpClientConfig(clientConfig);
        IAcsClient client = new KmsTransferAcsClient(profile, config);

        AsymmetricDecryptRequest request = new AsymmetricDecryptRequest();
        request.setCiphertextBlob("<your-ciphertext-blob>");
        request.setAlgorithm("<your-algorithm>");
        request.setKeyId("<your-key-id>");
        try {
            AsymmetricDecryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
            System.out.printf("RequestId: %s%n", response.getRequestId());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }

    /**
     * 此方法主要是针对dkmsAsymmetricDecrypt对比使用，一旦完成密钥迁移则无法使用KMS网关调用密码
     */
    private static void kmsAsymmetricDecrypt() {
        DefaultProfile profile = DefaultProfile.getProfile("<your-endpoint>", "<your-access-key-id>", "<your-access-key-secret>");
        IAcsClient client = new DefaultAcsClient(profile);
        AsymmetricDecryptRequest request = new AsymmetricDecryptRequest();
        request.setCiphertextBlob("<your-ciphertext-blob>");
        request.setAlgorithm("<your-algorithm>");
        request.setKeyId("<your-key-id>");
        try {
            AsymmetricDecryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("Plaintext: %s%n", response.getPlaintext());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }

}