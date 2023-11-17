package com.aliyun.kms.utils;

public interface Constants {
    int GCM_IV_LENGTH = 12;
    int EKT_ID_LENGTH = 36;
    String ENCRYPT_API_NAME = "Encrypt";
    String ASYMMETRIC_ENCRYPT_API_NAME = "AsymmetricEncrypt";
    String DECRYPT_API_NAME = "Decrypt";
    String ASYMMETRIC_DECRYPT_API_NAME = "AsymmetricDecrypt";
    String ASYMMETRIC_SIGN_API_NAME = "AsymmetricSign";
    String ASYMMETRIC_VERIFY_API_NAME = "AsymmetricVerify";
    String GENERATE_DATA_KEY_API_NAME = "GenerateDataKey";
    String GENERATE_DATA_KEY_WITHOUT_PLAINTEXT_API_NAME = "GenerateDataKeyWithoutPlaintext";
    String GET_PUBLIC_KEY_API_NAME = "GetPublicKey";
    String GET_SECRET_VALUE_API_NAME = "GetSecretValue";
    String DIGEST_MESSAGE_TYPE = "DIGEST";
    String KMS_KEY_PAIR_AES_256 = "AES_256";
    String KMS_KEY_PAIR_AES_128 = "AES_128";
    String REQUEST_ID_KEY_NAME = "requestId";
    String MIGRATION_KEY_VERSION_ID_KEY = "x-kms-migrationkeyversionid";
    int NUMBER_OF_BYTES_AES_256 = 32;
    int NUMBER_OF_BYTES_AES_128 = 16;
    String SDK_NAME = "alibabacloud-dkms-transfer-java-sdk";
    String SDK_VERSION = "0.2.1";
    String SDK_USER_AGENT = SDK_NAME + "/" + SDK_VERSION;
}
