package com.excalibur.core.bus;

public interface EventReceiver<T> {

	void onEvent(String name, T data);

}
