package com.aliyun.kms.result;

import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.transform.UnmarshallerContext;

public class GetSecretValueResult extends AcsResponse {
    private String requestId;
    private String secretName;
    private String versionId;
    private String createTime;
    private String secretData;
    private String secretDataType;
    private String automaticRotation;
    private String rotationInterval;
    private String nextRotationDate;
    private String extendedConfig;
    private String lastRotationDate;
    private String secretType;
    private VersionStage versionStages;

    public GetSecretValueResult() {
    }

    @Override
    public AcsResponse getInstance(UnmarshallerContext context) throws ClientException, ServerException {
        return null;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSecretName() {
        return this.secretName;
    }

    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }

    public String getVersionId() {
        return this.versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSecretData() {
        return this.secretData;
    }

    public void setSecretData(String secretData) {
        this.secretData = secretData;
    }

    public String getSecretDataType() {
        return this.secretDataType;
    }

    public void setSecretDataType(String secretDataType) {
        this.secretDataType = secretDataType;
    }

    public String getAutomaticRotation() {
        return this.automaticRotation;
    }

    public void setAutomaticRotation(String automaticRotation) {
        this.automaticRotation = automaticRotation;
    }

    public String getRotationInterval() {
        return this.rotationInterval;
    }

    public void setRotationInterval(String rotationInterval) {
        this.rotationInterval = rotationInterval;
    }

    public String getNextRotationDate() {
        return this.nextRotationDate;
    }

    public void setNextRotationDate(String nextRotationDate) {
        this.nextRotationDate = nextRotationDate;
    }

    public String getExtendedConfig() {
        return this.extendedConfig;
    }

    public void setExtendedConfig(String extendedConfig) {
        this.extendedConfig = extendedConfig;
    }

    public String getLastRotationDate() {
        return this.lastRotationDate;
    }

    public void setLastRotationDate(String lastRotationDate) {
        this.lastRotationDate = lastRotationDate;
    }

    public String getSecretType() {
        return this.secretType;
    }

    public void setSecretType(String secretType) {
        this.secretType = secretType;
    }

    public VersionStage getVersionStages() {
        return versionStages;
    }

    public void setVersionStages(VersionStage versionStages) {
        this.versionStages = versionStages;
    }

}