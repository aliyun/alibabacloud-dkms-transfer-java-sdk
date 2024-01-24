package com.aliyun.kms.handlers;

import com.aliyun.dkms.gcs.openapi.Client;
import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.kms.utils.KmsErrorCodeTransferUtils;
import com.aliyun.kms.utils.XmlUtil;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.tea.TeaRetryableException;
import com.aliyun.tea.TeaUnretryableException;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ErrorCodeConstant;
import com.aliyuncs.exceptions.ErrorMessageConstant;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.utils.StringUtils;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.codec.binary.Base64;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.aliyun.kms.utils.Constants.REQUEST_ID_KEY_NAME;
import static com.aliyun.kms.utils.KmsErrorCodeTransferUtils.*;

public interface KmsTransferHandler<DReq extends TeaModel, DRep extends TeaModel> {

    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().disableHtmlEscaping().create();
    Base64 base64 = new Base64();

    Client getClient();

    String getAction();

    default <T extends AcsResponse> HttpResponse handlerDKmsRequestWithOptions(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException, ServerException {
        DReq dkmsRequest = buildDKMSRequest(request, runtimeOptions);
        try {
            return transferResponse(request, callDKMS(dkmsRequest, runtimeOptions));
        } catch (TeaException e) {
            throw transferTeaException(e);
        } catch (TeaUnretryableException e) {
            throw transferTeaUnretryableException(e, request.getSysActionName());
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            throw transferException(e, request.getSysActionName());
        }
    }

    <T extends AcsResponse> DReq buildDKMSRequest(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException;

    DRep callDKMS(DReq dkmsRequest, RuntimeOptions runtimeOptions) throws Exception;

    HttpResponse transferResponse(AcsRequest request, DRep response) throws ClientException;

    default ClientException transferTeaException(TeaException e) {
        Map<String, Object> data = e.getData();
        String requestId = "";
        if (data != null) {
            requestId = String.valueOf(data.getOrDefault(REQUEST_ID_KEY_NAME, ""));
        }
        switch (e.getCode()) {
            case INVALID_PARAM_ERROR_CODE: {
                if (INVALID_PARAM_DATE_ERROR_MESSAGE.equals(e.getMessage())) {
                    ClientException clientException = transferInvalidDateException();
                    clientException.setRequestId(requestId);
                    return clientException;
                } else if (INVALID_PARAM_AUTHORIZATION_ERROR_MESSAGE.equals(e.getMessage())) {
                    ClientException clientException = transferIncompleteSignatureException();
                    clientException.setRequestId(requestId);
                    return clientException;
                }
            }
            case UNAUTHORIZED_ERROR_CODE: {
                ClientException clientException = transferInvalidAccessKeyIdException();
                clientException.setRequestId(requestId);
                return clientException;
            }
            default:
                String errorMessage = KmsErrorCodeTransferUtils.transferErrorMessage(e.getCode());
                errorMessage = StringUtils.isEmpty(errorMessage) ? e.getMessage() : errorMessage;
                return new ClientException(e.getCode(), errorMessage, requestId);
        }
    }

    default ClientException transferTeaUnretryableException(TeaUnretryableException e, String actionName) {
        if (e.getCause() != null && e.getCause() instanceof TeaRetryableException) {
            if ((e.getCause().getMessage() != null && e.getCause().getMessage().contains("Read timed out"))
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("timeout"))
                    || (e.getCause().getCause() != null && e.getCause().getCause() instanceof SocketTimeoutException)
                    || (e.getCause().getCause() != null && e.getCause().getCause().getCause() != null && e.getCause().getCause().getCause() instanceof SocketTimeoutException)) {
                return new ClientException("SDK.ReadTimeout",
                        "SocketTimeoutException has occurred on a socket read or accept.The action is " +
                                actionName, e.getCause().getCause());
            }
        }
        return new ClientException(e);
    }

    default ClientException transferException(Exception e, String actionName) {
        if (e != null && e instanceof InvalidProtocolBufferException
                && !StringUtils.isEmpty(e.getMessage()) && e.getMessage().contains("Protocol message end-group tag did not match expected tag.")) {
            return new ClientException("SDK.ServerUnreachable",
                    "Server unreachable: connection action:" + actionName + " failed", e);
        }
        return new ServerException(ErrorCodeConstant.SDK_INVALID_SERVER_RESPONSE,
                ErrorMessageConstant.SERVER_RESPONSE_HTTP_BODY_EMPTY);
    }

    default ClientException newMissingParameterClientException(String paramName) {
        return new ClientException(MISSING_PARAMETER_ERROR_CODE, String.format("The parameter  %s  needed but no provided.", paramName));
    }

    default ClientException newInvalidParameterClientException(String paramName) {
        return new ClientException(INVALID_PARAMETER_ERROR_CODE, String.format("The parameter  %s  is invalid.", paramName));
    }

    default byte[] getHttpContent(FormatType acceptFormat, AcsResponse response) throws ClientException {
        if (FormatType.JSON.equals(acceptFormat)) {
            return gson.toJson(response).getBytes(StandardCharsets.UTF_8);
        } else if (FormatType.XML.equals(acceptFormat)) {
            try {
                return XmlUtil.buildRequestXml(response).getBytes(StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new ClientException(e);
            }
        } else {
            throw new ClientException(String.format("Server response has a bad format type: %s;", acceptFormat));
        }
    }
}
