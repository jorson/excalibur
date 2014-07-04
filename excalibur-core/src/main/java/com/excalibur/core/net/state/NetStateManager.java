package com.excalibur.core.net.state;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.nd.hy.android.core.util.Ln;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class NetStateManager {

    public enum NetState {
        MOBILE, WIFI, NOWAY
    }

    public static  NetState         CUR_NET_STATE = NetState.NOWAY;
    public static  String           MOBILE_PROXY  = null;
    private static NetStateReceiver sReceiver     = null;
    private static List<OnNetStateChangedListener> mOnNetStateChangedListeners;

    /**
     * 初始化连接类型，应用启动时调用
     *
     * @param context
     */
    public static void init(Context context) {
        CUR_NET_STATE = NetState.NOWAY;
        getCurNetState(context);
        sReceiver = null;
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        sReceiver = new NetStateReceiver();
        context.registerReceiver(sReceiver, filter);
        mOnNetStateChangedListeners = new ArrayList<OnNetStateChangedListener>();
    }

    /**
     * 在应用退出的时候，做一些注销操作
     *
     * @param context
     */
    public static void onExit(Context context) {
        if (null != sReceiver) {
            context.unregisterReceiver(sReceiver);
            CUR_NET_STATE = NetState.NOWAY;
            sReceiver = null;
            mOnNetStateChangedListeners = null;
        }
    }

    /**
     * 判断网络是否连接
     * <p/>
     * 不会刷新网络状态
     *
     * @return
     */
    public static boolean onNet() {
        return onNet(false);
    }

    /**
     * 判断网络是否连接
     *
     * @param retrieve 是否更新NetworkInfo
     * @return
     */
    public static boolean onNet(boolean retrieve) {
//        if (retrieve) {
//            getCurNetState(App.getApplication());
//        }
        return CUR_NET_STATE != NetState.NOWAY;
    }

    /**
     * 判断当前网络是wifi
     *
     * @return true:表示wifi
     */
    public static boolean isWify() {
        return CUR_NET_STATE == NetState.WIFI;
    }

    protected static NetState getCurNetState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            int netType = networkInfo.getType();
            int subType = networkInfo.getSubtype();
            if (isConnectionFast(netType, subType)) {
                NetStateManager.CUR_NET_STATE = NetState.WIFI;
            } else {
                NetStateManager.CUR_NET_STATE = NetState.MOBILE;
            }
        } else {
            NetStateManager.CUR_NET_STATE = NetState.NOWAY;
        }
        Ln.d("net state: %s", NetStateManager.CUR_NET_STATE.toString());
        notifyNetStateChanged();
        return NetStateManager.CUR_NET_STATE;
    }

    private static void notifyNetStateChanged() {
        if (null != mOnNetStateChangedListeners && mOnNetStateChangedListeners.size() > 0) {
            for (OnNetStateChangedListener mOnNetStateChangedListener : mOnNetStateChangedListeners) {
                mOnNetStateChangedListener.onNetStateChange(CUR_NET_STATE);
            }
        }
    }

    /**
     * 注册网络状态更变通知<br/>
     * 注：改方法需要改进，使用弱引用
     *
     * @param onNetStateChangedListener
     */
    public static void registerNetStateChangedListener(OnNetStateChangedListener onNetStateChangedListener) {
        if (null != mOnNetStateChangedListeners) {
            mOnNetStateChangedListeners.add(onNetStateChangedListener);
        }
    }

    /**
     * 取消网络状态更变的通知
     *
     * @param onNetStateChangedListener
     */
    public static void unRegisterNetStateChangedListener(OnNetStateChangedListener onNetStateChangedListener) {
        if (null != mOnNetStateChangedListeners) {
            mOnNetStateChangedListeners.remove(onNetStateChangedListener);
        }
    }

    /**
     * 判断是否网络是否快速
     *
     * @param type
     * @param subType
     * @return
     */
    protected static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    static void initMobileProxy(Context context) {
        //先清空，避免WAP切换到NET时的错误
        MOBILE_PROXY = null;
        Uri uri = Uri.parse("content://telephony/carriers/preferapn");
        Cursor mCursor = null;
        if (null != context) {
            mCursor = context.getContentResolver().query(uri, null, null, null, null);
        }
        if (mCursor != null && mCursor.moveToFirst()) {
            // 游标移至第一条记录，当然也只有一条
            String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
            if (proxyStr != null && proxyStr.trim().length() > 0) {
                MOBILE_PROXY = proxyStr;
            }
            mCursor.close();
        }
    }


    /**
     * 获取 当前apn并返回httphost对象
     *
     * @return
     */
    @Deprecated
    public static HttpHost getAPN() {
        HttpHost proxy = null;

        if (StringUtils.isNotBlank(MOBILE_PROXY)) {
            proxy = new HttpHost(MOBILE_PROXY, 80);
        }
        return proxy;
    }

    public static boolean isFastConnected() {
        return CUR_NET_STATE == NetState.WIFI;
    }
}
