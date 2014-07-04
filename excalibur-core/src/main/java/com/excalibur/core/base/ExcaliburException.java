package com.excalibur.core.base;

public class ExcaliburException extends Exception {

    public ExcaliburException() {
        super();
    }

    public ExcaliburException(java.lang.String detailMessage) {
        super(detailMessage);
    }

    public ExcaliburException(java.lang.String detailMessage, java.lang.Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ExcaliburException(java.lang.Throwable throwable) {
        super(throwable);
    }
}
