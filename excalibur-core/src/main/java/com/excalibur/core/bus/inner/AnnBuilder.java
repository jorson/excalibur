package com.excalibur.core.bus.inner;

import com.nd.hy.android.core.bus.ann.ReceiveEvents;
import com.nd.hy.android.core.bus.inner.bus.ReceiveEventsAnn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class AnnBuilder {

    static ReceiveEventsAnn getReceiveEventsAnn(Method m) {
        for (Annotation a : m.getDeclaredAnnotations()) {
            Class<?> at = a.annotationType();
            if (ReceiveEvents.class == at) {
                return new ReceiveEventsAnn((ReceiveEvents) a);
            }
        }
        return null;
    }
}