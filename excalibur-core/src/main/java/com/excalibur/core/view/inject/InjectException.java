package com.excalibur.core.view.inject;

public class InjectException extends Exception {
    private static final long serialVersionUID = 1L;

    public InjectException() {
    }

    public InjectException(String detailMessage) {
        super(detailMessage);
    }

    public InjectException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InjectException(Throwable throwable) {
        super(throwable);
    }
}
