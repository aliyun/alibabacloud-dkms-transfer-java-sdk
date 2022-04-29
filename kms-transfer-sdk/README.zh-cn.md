# 阿里云专属KMS适配Java SDK
[![GitHub version](https://badge.fury.io/gh/aliyun%2Falibabacloud-dkms-transfer-java-sdk.svg)](https://badge.fury.io/gh/aliyun%2Falibabacloud-dkms-transfer-java-sdk)
[![Build Status](https://travis-ci.org/aliyun/alibabacloud-dkms-transfer-java-sdk.svg?branch=master)](https://travis-ci.org/aliyun/alibabacloud-dkms-transfer-java-sdk)

阿里云专属KMS适配Java SDK可以帮助Java开发者完成由KMS SDK向专属KMS SDK迁移适配工作。你可以通过***Maven***快速使用。

*其他语言版本: [English](README.md), [简体中文](README.zh-cn.md)*

- [阿里云专属KMS主页](https://help.aliyun.com/document_detail/311016.html)
- [Issues](https://github.com/aliyun/alibabacloud-dkms-transfer-java-sdk/issues)
- [Release](https://github.com/aliyun/alibabacloud-dkms-transfer-java-sdk/releases)

## 许可证

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)


## 优势
* 减低用户从KMS SDK迁移专属KMS SDK的成本
* 减少由KMS SDK向专属KMS SDK迁移适配工作代码量

## 软件要求

- Java 1.8 或以上版本
- Maven

## 安装

可以通过Maven的方式在项目中使用专属KMS适配Java客户端。导入方式如下:

```
<dependency>
    <groupId>com.aliyun.kms</groupId>
        <artifactId>kms-transfer-client</artifactId>
    <version>0.0.1</version>
</dependency>
```


## 构建

你可以从Github检出代码通过下面的maven命令进行构建。

```
mvn clean install -DskipTests -Dgpg.skip=true
```

## 示例代码

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
        //如需跳过https认证,可打开此处注释代码
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
 
