package com.excalibur.core.bus.inner.bus;

import com.nd.hy.android.core.bus.ann.ReceiveEvents;
import com.nd.hy.android.core.bus.inner.base.Ann;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class ReceiveEventsAnn extends Ann<ReceiveEvents> {

    public final String[] names;

    public ReceiveEventsAnn(ReceiveEvents annotation) {
        super(annotation);
        String[] names;
        if (hackSuccess()) {
            names = (String[]) getElement(NAME);
            cleanup();
        } else {
            names = annotation.name();
        }
        boolean none = (names.length == 1) && isEmpty(names[0]);
        if (none) {
            this.names = new String[0];
        } else {
            this.names = names;
        }
    }

}
