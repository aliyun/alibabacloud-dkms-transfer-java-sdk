package com.aliyun.kms.hadlers;

import com.aliyun.dkms.gcs.openapi.Client;
import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.kms.utils.KmsErrorCodeTransferUtils;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.tea.TeaUnretryableException;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ErrorCodeConstant;
import com.aliyuncs.exceptions.ErrorMessageConstant;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.utils.StringUtils;

import java.util.Map;

import static com.aliyun.kms.utils.Constants.REQUEST_ID_KEY_NAME;
import static com.aliyun.kms.utils.KmsErrorCodeTransferUtils.*;

public interface KmsTransferHandler<DReq extends TeaModel, DRep extends TeaModel> {

    Client getClient();

    String getAction();

    default <T extends AcsResponse> HttpResponse handlerDKmsRequestWithOptions(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException, ServerException {
        DReq dkmsRequest = buildDKMSRequest(request, runtimeOptions);
        try {
            return transferResponse(callDKMS(dkmsRequest, runtimeOptions));
        } catch (TeaException e) {
            throw transferTeaException(e);
        } catch (TeaUnretryableException e) {
            throw new ClientException(e);
        } catch (Exception e) {
            throw new ServerException(ErrorCodeConstant.SDK_INVALID_SERVER_RESPONSE,
                    ErrorMessageConstant.SERVER_RESPONSE_HTTP_BODY_EMPTY);
        }
    }

    <T extends AcsResponse> DReq buildDKMSRequest(AcsRequest<T> request, RuntimeOptions runtimeOptions) throws ClientException;

    DRep callDKMS(DReq dkmsRequest, RuntimeOptions runtimeOptions) throws Exception;

    HttpResponse transferResponse(DRep response) throws ClientException;

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

    default ClientException newMissingParameterClientException(String paramName) {
        return new ClientException(MISSING_PARAMETER_ERROR_CODE, String.format("The parameter  %s  needed but no provided.", paramName));
    }
}
