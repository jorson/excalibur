package com.excalibur.frame.base;

import android.content.Context;
import android.os.Bundle;
import com.nd.hy.android.core.base.HermesException;

/**
 * Interface to implement by your operations
 *
 * @author Foxykeep
 */
public interface Operation {
    /**
     * Execute the request and returns a {@link android.os.Bundle} containing the data to return.
     *
     * @param context The context to use for your operation.
     * @param request The request to execute.
     * @return A {@link android.os.Bundle} containing the data to return. If no data to return, null.
     */
    public Bundle execute(Context context, Request request) throws HermesException;
}
