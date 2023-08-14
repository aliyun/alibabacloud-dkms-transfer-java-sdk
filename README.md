# Alibaba Cloud Dedicated KMS Transfer SDK for Java

![](https://aliyunsdk-pages.alicdn.com/icons/AlibabaCloud.svg)

Alibaba Cloud Dedicated KMS Transfer SDK for Java can help Java developers to migrate from the KMS keys to the Dedicated KMS keys. You can get started in minutes using ***Maven*** .

*Read this in other languages: [English](README.md), [简体中文](README.zh-cn.md)*

- [Alibaba Cloud Dedicated KMS Homepage](https://www.alibabacloud.com/help/zh/doc-detail/311016.htm)
- [Sample Code](/examples)
- [Issues](https://github.com/aliyun/alibabacloud-dkms-transfer-java-sdk/issues)
- [Release](https://github.com/aliyun/alibabacloud-dkms-transfer-java-sdk/releases)

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## Features
* Dedicated KMS provides a tenant-specific instance that is deployed in the VPC of a tenant to allow access over an internal network.
* Dedicated KMS uses a tenant-specific cryptographic resource pool to implement resource isolation and cryptographic isolation. This improves security.
* Dedicated KMS simplifies the management of HSMs. You can use the stable, easy-to-use upper-layer key management features and cryptographic operations provided by Dedicated KMS to manage your HSMs.
* Dedicated KMS allows you to integrate your HSMs with Alibaba Cloud services in a seamless manner. This delivers secure and controllable encryption capabilities for Alibaba Cloud services. For more information, see [Alibaba Cloud services that can be integrated with KMS](https://www.alibabacloud.com/help/en/key-management-service/latest/alibaba-cloud-services-that-can-be-integrated-with-kms#concept-2318937).
* Reduce the cost of migrating the Shared KMS keys to Dedicated KMS keys. 

## Requirements

- Java 1.8 or later
- Maven

## Install

The recommended way to use the Alibaba Cloud Dedicated KMS Transfer Client for Java in your project is to consume it from Maven. Import as follows:

```
<dependency>
    <groupId>com.aliyun.kms</groupId>
    <artifactId>kms-transfer-client</artifactId>
    <version>0.2.0</version>
</dependency>
```


## Build

Once you check out the code from GitHub, you can build it using Maven. Use the following command to build:

```
mvn clean install -DskipTests -Dgpg.skip=true
```

## Client Mechanism
Alibaba Cloud Dedicated KMS Transfer SDK for Java transfers the the following method of request to dedicated KMS vpc gateway by default.

* Encrypt
* Decrypt
* GenerateDataKey
* GenerateDataKeyWithoutPlaintext
* GetPublicKey
* AsymmetricEncrypt
* AsymmetricDecrypt
* AsymmetricSign
* AsymmetricVerify
* GetSecretValue


## Sample Code
```Java
import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.kms.KmsTransferAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpClientConfig;
import com.aliyuncs.kms.model.v20160120.EncryptRequest;
import com.aliyuncs.kms.model.v20160120.EncryptResponse;
import com.aliyuncs.profile.DefaultProfile;


public class EncryptSample {
    public static void main(String[] args) {
        encrypt();
    }

    public static void encrypt() {
        Config config = new Config();
        config.setProtocol("https");
        config.setClientKeyFile("<your-client-key-file>");
        config.setPassword("<your-password>");
        config.setEndpoint("<your-endpoint>");
        // If you want to verify the server certificate, you need to set it as your CA certificate file path
        config.setCaFilePath("<path/to/yourCaCert>");
        // Or, set it as the content of your CA certificate
        //config.setCa("<your-ca-certificate-content");
        DefaultProfile profile = DefaultProfile.getProfile("<your-endpoint>", System.getenv("<your-access-key-env-name>"), System.getenv("<your-access-key-secret-env-name>"));

        HttpClientConfig clientConfig = HttpClientConfig.getDefault();
        //To skip https authentication, you can open the comment code here
        //clientConfig.setIgnoreSSLCerts(true);
        profile.setHttpClientConfig(clientConfig);
        IAcsClient client = new KmsTransferAcsClient(profile, config);
        EncryptRequest request = new EncryptRequest();
        request.setKeyId("<your-key-id>");
        request.setPlaintext("<your-plaintext>");
        try {
            EncryptResponse response = client.getAcsResponse(request);
            System.out.printf("KeyId: %s%n", response.getKeyId());
            System.out.printf("KeyVersionId: %s%n", response.getKeyVersionId());
            System.out.printf("CiphertextBlob: %s%n", response.getCiphertextBlob());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }
}
```

 
