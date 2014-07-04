package com.excalibur.frame.exception;

import android.os.Bundle;
import com.nd.hy.android.core.base.HermesException;
import com.nd.hy.android.frame.base.Request;

/**
 * Thrown to indicate that a custom exception occurred.
 * <p/>
 * Subclass this class to create special exceptions for your needs.
 *
 * @author Foxykeep
 */
public abstract class CustomRequestException extends HermesException {

    private static final long serialVersionUID = 4658308128254827562L;

    /**
     * Constructs a new {@link CustomRequestException} that includes the current stack trace.
     */
    public CustomRequestException() {
        super();
    }

    /**
     * Constructs a new {@link CustomRequestException} that includes the current stack trace, the
     * specified detail message and the specified cause.
     *
     * @param detailMessage The detail message for this exception.
     * @param throwable     The cause of this exception.
     */
    public CustomRequestException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@link CustomRequestException} that includes the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage The detail message for this exception.
     */
    public CustomRequestException(final String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@link CustomRequestException} that includes the current stack trace and the
     * specified cause.
     *
     * @param throwable The cause of this exception.
     */
    public CustomRequestException(final Throwable throwable) {
        super(throwable);
    }

    public Bundle onRequestException(Request request) {
        return null;
    }

}
