package com.aliyun.kms.examples;

import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.kms.KmsTransferAcsClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.kms.model.v20160120.GenerateDataKeyWithoutPlaintextRequest;
import com.aliyuncs.kms.model.v20160120.GenerateDataKeyWithoutPlaintextResponse;
import com.aliyuncs.profile.DefaultProfile;


public class GenerateDataKeyWithoutPlaintextSample {

    public static void main(String[] args) {
        dkmsGenerateDataKeyWithoutPlaintext();
        kmsGenerateDataKeyWithoutPlaintext();
    }

    /**
     * 使用KMS API调用DKMS密钥
     */
    public static void dkmsGenerateDataKeyWithoutPlaintext() {
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
        GenerateDataKeyWithoutPlaintextRequest request = new GenerateDataKeyWithoutPlaintextRequest();
        request.setKeyId("<your-key-id>");
        try {
            GenerateDataKeyWithoutPlaintextResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }

    /**
     * 此方法主要是针对dkmsGenerateDataKeyWithoutPlaintext对比使用，一旦完成密钥迁移则无法使用KMS网关调用密码
     */
    private static void kmsGenerateDataKeyWithoutPlaintext() {
        DefaultProfile profile = DefaultProfile.getProfile("<your-endpoint>", "<your-access-key-id>", "<your-access-key-secret>");
        IAcsClient client = new DefaultAcsClient(profile);
        GenerateDataKeyWithoutPlaintextRequest request = new GenerateDataKeyWithoutPlaintextRequest();
        request.setKeyId("<your-key-id>");
        try {
            GenerateDataKeyWithoutPlaintextResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
}