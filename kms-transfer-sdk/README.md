# Alibaba Cloud Dedicated KMS Transfer SDK for Java

[![GitHub version](https://badge.fury.io/gh/aliyun%2Falibabacloud-dkms-transfer-java-sdk.svg)](https://badge.fury.io/gh/aliyun%2Falibabacloud-dkms-transfer-java-sdk)
[![Build Status](https://travis-ci.org/aliyun/alibabacloud-dkms-transfer-java-sdk.svg?branch=master)](https://travis-ci.org/aliyun/alibabacloud-dkms-transfer-java-sdk)

Alibaba Cloud Dedicated KMS Transfer SDK for Java can help Java developers migrate from the KMS SDK to the Dedicated KMS SDK. You can get started in minutes using ***Maven*** .

*Read this in other languages: [English](README.md), [简体中文](README.zh-cn.md)*

- [Alibaba Cloud Dedicated KMS Homepage](https://www.alibabacloud.com/help/zh/doc-detail/311016.htm)
- [Issues](https://github.com/aliyun/alibabacloud-dkms-transfer-java-sdk/issues)
- [Release](https://github.com/aliyun/alibabacloud-dkms-transfer-java-sdk/releases)

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)

## Features
* Reduce the cost of migrating Dedicated KMS SDK from the KMS SDK
* Reduce the amount of adapted code transferred from KMS SDK to Dedicated KMS SDK

## Requirements

- Java 1.8 or later
- Maven

## Install

The recommended way to use the dedicated KMS transfer client for Java in your project is to consume it from Maven. Import as follows:

```
<dependency>
    <groupId>com.aliyun.kms</groupId>
        <artifactId>kms-transfer-client</artifactId>
    <version>0.0.1</version>
</dependency>
```


## Build

Once you check out the code from GitHub, you can build it using Maven. Use the following command to build:

```
mvn clean install -DskipTests -Dgpg.skip=true
```


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
        DefaultProfile profile = DefaultProfile.getProfile("<your-endpoint>", "<your-access-key-id>", "<your-access-key-secret>");
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

 
