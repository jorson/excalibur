package com.excalibur.frame.service.priority;

/**
 * PriorityRunnable
 * Date: 13-12-13
 *
 * @author Yangz
 */
public interface PriorityRunnable<T> extends Runnable, Comparable<T> {
}
