package com.excalibur.frame.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.nd.hy.android.core.base.HermesException;
import com.nd.hy.android.core.net.http.exception.ConnectionException;
import com.nd.hy.android.core.util.Ln;
import com.nd.hy.android.frame.base.Operation;
import com.nd.hy.android.frame.base.Request;
import com.nd.hy.android.frame.base.RequestConstants;
import com.nd.hy.android.frame.exception.CustomRequestException;
import com.nd.hy.android.frame.exception.DataException;

/**
 * This class is the superclass of all the worker services you'll create.
 *
 * @author Foxykeep
 */
public abstract class RequestService extends MultiThreadedIntentService {

    public static final String INTENT_EXTRA_RECEIVER = "com.nd.up91.core.request.extra.receiver";
    public static final String INTENT_EXTRA_REQUEST  = "com.nd.up91.core.request.extra.request";

    private static final int SUCCESS_CODE = 0;
    public static final  int ERROR_CODE   = -1;

    /**
     * Proxy method for {@link #sendResult(android.os.ResultReceiver, android.os.Bundle, int)} when the work is a
     * success.
     *
     * @param receiver The result receiver received inside the {@link android.content.Intent}.
     * @param data     A {@link android.os.Bundle} with the data to send back.
     */
    private void sendSuccess(ResultReceiver receiver, Bundle data) {
        sendResult(receiver, data, SUCCESS_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(android.os.ResultReceiver, android.os.Bundle, int)} when the work is a failure
     * due to the network.
     *
     * @param receiver  The result receiver received inside the {@link android.content.Intent}.
     * @param exception The {@link ConnectionException} triggered.
     */
    private void sendConnexionFailure(ResultReceiver receiver, ConnectionException exception) {
        Bundle data = new Bundle();
        data.putInt(RequestConstants.RECEIVER_EXTRA_ERROR_TYPE, RequestConstants.ERROR_TYPE_CONNEXION);
        data.putInt(RequestConstants.RECEIVER_EXTRA_CONNECTION_ERROR_STATUS_CODE,
                exception.getStatusCode());
        sendResult(receiver, data, ERROR_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(android.os.ResultReceiver, android.os.Bundle, int)} when the work is a failure
     * due to the data (parsing for example).
     *
     * @param receiver The result receiver received inside the {@link android.content.Intent}.
     */
    private void sendDataFailure(ResultReceiver receiver) {
        Bundle data = new Bundle();
        data.putInt(RequestConstants.RECEIVER_EXTRA_ERROR_TYPE, RequestConstants.ERROR_TYPE_DATA);
        sendResult(receiver, data, ERROR_CODE);
    }

    /**
     * Proxy method for {@link #sendResult(android.os.ResultReceiver, android.os.Bundle, int)} when the work is a failure
     * due to {@link CustomRequestException} being thrown.
     *
     * @param receiver The result receiver received inside the {@link android.content.Intent}.
     * @param data     A {@link android.os.Bundle} the data to send back.
     */
    private void sendCustomFailure(ResultReceiver receiver, Bundle data) {
        if (data == null) {
            data = new Bundle();
        }
        data.putInt(RequestConstants.RECEIVER_EXTRA_ERROR_TYPE, RequestConstants.ERROR_TYPE_CUSTOM);
        sendResult(receiver, data, ERROR_CODE);
    }

    /**
     * Method used to send back the result to the {@link RequestManager}.
     *
     * @param receiver The result receiver received inside the {@link android.content.Intent}.
     * @param data     A {@link android.os.Bundle} the data to send back.
     * @param code     The success/error code to send back.
     */
    private void sendResult(ResultReceiver receiver, Bundle data, int code) {
        Ln.d("sendResult : " + ((code == SUCCESS_CODE) ? "Success" : "Failure"));

        if (receiver != null) {
            if (data == null) {
                data = new Bundle();
            }

            receiver.send(code, data);
        }
    }

    @Override
    protected final void onHandleIntent(Intent intent) {
        Request request = intent.getParcelableExtra(INTENT_EXTRA_REQUEST);
        request.setClassLoader(getClassLoader());

        ResultReceiver receiver = intent.getParcelableExtra(INTENT_EXTRA_RECEIVER);
//        Operation operation = getOperationForType(request.getRequestType());

        try {
            Operation operation = request.getOperationClass().newInstance();
            sendSuccess(receiver, operation.execute(this, request));
        } catch (InstantiationException e) {
            Ln.e(e);
        } catch (IllegalAccessException e) {
            Ln.e(e);
        } catch (ConnectionException e) {
            Ln.e(e, "ConnectionException");
            sendConnexionFailure(receiver, e);
        } catch (DataException e) {
            Ln.e(e, "DataException");
            sendDataFailure(receiver);
        } catch (CustomRequestException e) {
            Ln.e(e, "Custom Exception");
            sendCustomFailure(receiver, onCustomRequestException(request, e));
        } catch (HermesException e) {
            Ln.e(e, "Hermes Exception");
            sendConnexionFailure(receiver, ConnectionException.DEFAULT);
        } catch (RuntimeException e) {
            Ln.e(e, "RuntimeException");
            sendConnexionFailure(receiver, ConnectionException.DEFAULT);
        }
    }

    /**
     * Call if a {@link CustomRequestException} is thrown by an {@link Operation}. You may return a
     * Bundle containing data to return to the {@link RequestManager}.
     * <p/>
     * Default implementation return null. You may want to override this method in your
     * implementation of {@link RequestService} to execute specific action and/or return specific
     * data.
     *
     * @param request   The {@link Request} which execution threw the exception.
     * @param exception The {@link CustomRequestException} thrown.
     * @return A {@link android.os.Bundle} containing data to return to the {@link RequestManager}. Default
     * implementation return null.
     */
    protected Bundle onCustomRequestException(Request request, CustomRequestException exception) {
        return exception.onRequestException(request);
    }

}
