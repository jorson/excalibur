package com.excalibur.demo.service;

import android.os.Bundle;
import com.excalibur.frame.base.Request;
import com.excalibur.frame.exception.CustomRequestException;
import com.excalibur.frame.service.RequestService;

/**
 * This class is called by the {@link AppRequestManager} through the {@link android.content.Intent} system.
 *
 * @author Foxykeep
 */
public final class AppRequestService extends RequestService {

    @Override
    protected int getMaximumNumberOfThreads() {
        return 3;
    }

    @Override
    protected Bundle onCustomRequestException(Request request, CustomRequestException exception) {
        return null;
    }

}
