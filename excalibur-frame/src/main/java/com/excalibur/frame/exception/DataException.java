package com.excalibur.frame.exception;

import com.excalibur.core.base.ExcaliburException;

/**
 * Thrown to indicate that a compulsory parameter is missing.
 *
 * @author Foxykeep
 */
public final class DataException extends ExcaliburException {

    private static final long serialVersionUID = -6031863210486494461L;

    /**
     * Constructs a new {@link DataException} that includes the current stack trace.
     */
    public DataException() {
        super();
    }

    /**
     * Constructs a new {@link DataException} that includes the current stack trace, the
     * specified detail message and the specified cause.
     *
     * @param detailMessage The detail message for this exception.
     * @param throwable     The cause of this exception.
     */
    public DataException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@link DataException} that includes the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage The detail message for this exception.
     */
    public DataException(final String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@link DataException} that includes the current stack trace and the
     * specified cause.
     *
     * @param throwable The cause of this exception.
     */
    public DataException(final Throwable throwable) {
        super(throwable);
    }

}
