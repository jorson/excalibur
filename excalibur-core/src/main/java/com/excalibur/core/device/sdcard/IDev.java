package com.excalibur.core.device.sdcard;

/**
 * IDev
 * Date: 13-10-10
 * Time: 上午10:58
 */
interface IDev {
    DevInfo getInternalInfo();

    DevInfo getExternalInfo();
}
