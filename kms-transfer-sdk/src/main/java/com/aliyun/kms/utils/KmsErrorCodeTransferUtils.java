package com.aliyun.kms.utils;

import com.aliyuncs.exceptions.ClientException;

import java.util.HashMap;
import java.util.Map;

public class KmsErrorCodeTransferUtils {
    private static final Map<String, String> errorCodeMap = new HashMap<String, String>();

    public static final String INVALID_PARAM_ERROR_CODE = "InvalidParam";
    public static final String UNAUTHORIZED_ERROR_CODE = "Unauthorized";
    public static final String MISSING_PARAMETER_ERROR_CODE = "MissingParameter";
    public static final String INVALID_PARAMETER_ERROR_CODE = "InvalidParameter";
    public static final String FORBIDDEN_KEY_NOT_FOUND_ERROR_CODE = "Forbidden.KeyNotFound";
    public static final String INVALID_PARAMETER_KEY_SPEC_ERROR_MESSAGE = "The specified parameter KeySpec is not valid.";
    public static final String INVALID_PARAM_DATE_ERROR_MESSAGE = "The Param Date is invalid.";
    public static final String INVALID_PARAM_AUTHORIZATION_ERROR_MESSAGE = "The Param Authorization is invalid.";

    static {
        errorCodeMap.put(FORBIDDEN_KEY_NOT_FOUND_ERROR_CODE, "The specified Key is not found.");
        errorCodeMap.put("Forbidden.NoPermission", "This operation is forbidden by permission system.");
        errorCodeMap.put("InternalFailure", "Internal Failure");
        errorCodeMap.put("Rejected.Throttling", "QPS Limit Exceeded");
    }

    public static String transferErrorMessage(String errorCode) {
        return errorCodeMap.get(errorCode);
    }

    public static ClientException transferInvalidDateException() {
        return new ClientException("IllegalTimestamp", "The input parameter \"Timestamp\" that is mandatory for processing this request is not supplied.");
    }

    public static ClientException transferInvalidAccessKeyIdException() {
        return new ClientException("InvalidAccessKeyId.NotFound", "The Access Key ID provided does not exist in our records.");
    }

    public static ClientException transferIncompleteSignatureException() {
        return new ClientException("IncompleteSignature", "The request signature does not conform to Aliyun standards.");
    }
}
