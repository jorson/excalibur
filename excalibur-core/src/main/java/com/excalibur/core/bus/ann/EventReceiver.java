package com.excalibur.core.bus.ann;

public interface EventReceiver<T> {

	void onEvent(String name, T data);

}
