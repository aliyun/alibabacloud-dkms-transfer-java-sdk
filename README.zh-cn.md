# 阿里云专属KMS适配Java SDK

![](https://aliyunsdk-pages.alicdn.com/icons/AlibabaCloud.svg)


阿里云专属KMS适配Java SDK可以帮助Java开发者快速完成由KMS密钥向专属KMS密钥迁移适配工作。你可以通过***Maven***快速使用。

*其他语言版本: [English](README.md), [简体中文](README.zh-cn.md)*

- [阿里云专属KMS主页](https://help.aliyun.com/document_detail/311016.html)
- [代码示例](/examples)
- [Issues](https://github.com/aliyun/alibabacloud-dkms-transfer-java-sdk/issues)
- [Release](https://github.com/aliyun/alibabacloud-dkms-transfer-java-sdk/releases)

## 许可证

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html)


## 优势
* 专属KMS提供租户独享的服务实例，并部署到租户的VPC内，满足私有网络接入需求。
* 专属KMS使用租户独享的密码资源池（HSM集群），实现资源隔离和密码学隔离，以获得更高的安全性。
* 专属KMS可以降低使用HSM的复杂度，为您的HSM提供稳定、易用的上层密钥管理途径和密码计算服务。
* 专属KMS可以将您的HSM与云服务无缝集成，为云服务加密提供更高的安全性和可控制性。更多信息，请参见[支持服务端集成加密的云服务](https://help.aliyun.com/document_detail/141499.htm?#concept-2318937)。
* 减低用户从共享KMS密钥移专属KMS密钥的成本


## 软件要求

- Java 1.8 或以上版本
- Maven

## 安装

可以通过Maven的方式在项目中使用专属KMS适配Java客户端。导入方式如下:

```
<dependency>
    <groupId>com.aliyun.kms</groupId>
    <artifactId>kms-transfer-client</artifactId>
    <version>0.2.0</version>
</dependency>
```


## 构建

你可以从Github检出代码通过下面的maven命令进行构建。

```
mvn clean install -DskipTests -Dgpg.skip=true
```

## 客户端机制
阿里云专属KMS适配Java SDK默认将下面列表方法请求转发给专属KMS VPC网关。

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
 
