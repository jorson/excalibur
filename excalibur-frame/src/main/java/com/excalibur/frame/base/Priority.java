package com.excalibur.frame.base;

/**
 * Priority
 * Date: 13-12-13
 *
 * @author Yangz
 */
public enum Priority {
    LOW,
    NORMAL,
    HIGH,
    IMMEDIATE;

    public static Priority valueOf(int ordinal) {
        try {
            return Priority.values()[ordinal];
        } catch (Exception e) {
            return NORMAL;
        }
    }
}
