package com.aliyun.kms.examples;

import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.kms.KmsTransferAcsClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.kms.model.v20160120.GetSecretValueRequest;
import com.aliyuncs.kms.model.v20160120.GetSecretValueResponse;
import com.aliyuncs.profile.DefaultProfile;


public class GetSecretValueSample {
    public static void main(String[] args) {
        dkmsGetSecretValue();
        kmsGetSecretValue();
    }

    /**
     * 使用KMS API调用DKMS密钥
     */
    public static void dkmsGetSecretValue() {
        Config config = new Config();
        config.setProtocol("https");
        config.setClientKeyFile("<your-client-key-file>");
        config.setPassword("<your-password>");
        config.setEndpoint("<your-endpoint>");
        // 如需验证服务端证书，这里需要设置为您的服务端证书路径
        config.setCaFilePath("<path/to/yourCaCert>");
        // 或者，设置为您的服务端证书内容
        //config.setCa("<your-ca-certificate-content");
        DefaultProfile profile = DefaultProfile.getProfile("<your-endpoint>", System.getenv("<your-access-key-env-name>"), System.getenv("<your-access-key-secret-env-name>"));

        HttpClientConfig clientConfig = HttpClientConfig.getDefault();
        //如需跳过https认证,可打开此处注释代码
        //clientConfig.setIgnoreSSLCerts(true);
        profile.setHttpClientConfig(clientConfig);
        IAcsClient client = new KmsTransferAcsClient(profile, config);
        GetSecretValueRequest request = new GetSecretValueRequest();
        request.setSecretName("<your-secret-name>");
        try {
            GetSecretValueResponse response = client.getAcsResponse(request);
            System.out.printf("SecretName: %s%n", response.getSecretName());
            System.out.printf("SecretData: %s%n", response.getSecretData());
            System.out.printf("CreateTime: %s%n", response.getCreateTime());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
    /**
     * 此方法主要是针对dkmsGetSecretValue对比使用，一旦完成密钥迁移则无法使用KMS网关调用密码
     */
    private static void kmsGetSecretValue() {
        DefaultProfile profile = DefaultProfile.getProfile("<your-endpoint>", System.getenv("<your-access-key-env-name>"), System.getenv("<your-access-key-secret-env-name>"));

        IAcsClient client = new DefaultAcsClient(profile);
        GetSecretValueRequest request = new GetSecretValueRequest();
        request.setSecretName("<your-secret-name>");
        try {
            GetSecretValueResponse response = client.getAcsResponse(request);
            System.out.printf("SecretName: %s%n", response.getSecretName());
            System.out.printf("SecretData: %s%n", response.getSecretData());
            System.out.printf("CreateTime: %s%n", response.getCreateTime());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
}
