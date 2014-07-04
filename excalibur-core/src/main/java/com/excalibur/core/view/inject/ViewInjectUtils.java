package com.excalibur.core.view.inject;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.lang.reflect.Field;

public final class ViewInjectUtils {

    private ViewInjectUtils() {
    }

    /**
     * 注入Activity中(与其字段绑定)的Fragment或界面组件
     *
     * @param activity
     */
    public static void inject(Activity activity) {
        injectActivity(activity);
    }

    /**
     * 注入View中(与其字段绑定)的界面组件
     *
     * @param view
     */
    public static void inject(View view) {
        injectView(view);
    }

    /**
     * 从View中获取界面组件，并注入viewHolder对象的字段中
     * <p/>
     * 当View对象本身不持有界面组件时，应使用该方法
     * 例如：ListAdapter中的ViewHolder
     *
     * @param view
     * @param viewHolder
     */
    public static void inject(View view, Object viewHolder) {
        injectView(viewHolder, view, null);
    }

    /**
     * 从View中获取界面组件，并注入viewHolder对象的字段中
     * <p/>
     * 当View对象本身不持有界面组件时，应使用该方法
     * 例如：Fragment
     *
     * @param view
     * @param viewHolder
     * @param fragment
     */
    public static void inject(View view, Object viewHolder, Fragment fragment) {
        injectView(viewHolder, view, fragment);
    }

    private static void injectActivity(Activity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                InjectView viewInject = field.getAnnotation(InjectView.class);
                if (viewInject != null) {
                    int viewId = viewInject.id();
                    if (!injectFragmentField(activity, field, viewId)) {
                        View findView = activity.findViewById(viewId);
                        safeSetField(activity, field, findView);
                    }
                }
            }
        }
    }

    private static boolean injectFragmentField(Activity activity, Field field, int viewId) {
        return injectFragmentField(activity, activity, field, viewId);
    }

    private static boolean injectFragmentField(Object target, Activity activity, Field field, int viewId) {
        // 判断字段类型是否为Fragment
        if (Fragment.class.isAssignableFrom(field.getType())) {
            if (activity instanceof FragmentActivity) {
                FragmentActivity fragmentActivity = (FragmentActivity) activity;
                Fragment fragment = fragmentActivity.getSupportFragmentManager().findFragmentById(viewId);
                safeSetField(target, field, fragment);
            } else {
                // activity type error
                // inject failure
            }
            return true;
        }
        return false;
    }

    private static void injectView(View view) {
        injectView(view, view, null);
    }

    private static void injectView(Object holder, View view, Fragment fragment) {
        injectView(holder, holder.getClass(), view, fragment);
    }

    private static void injectView(Object holder, Class<?> clazz, View view, Fragment fragment) {
        if (clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                InjectView viewInject = field.getAnnotation(InjectView.class);
                if (viewInject != null) {
                    int viewId = viewInject.id();
                    if (fragment == null || !injectFragmentField(holder, fragment.getActivity(), field, viewId)) {
                        View findView = view.findViewById(viewId);
                        safeSetField(holder, field, findView);
                    }
                }
            }
        }
        injectView(holder, clazz.getSuperclass(), view, fragment);
    }

    private static void safeSetField(Object target, Field field, Object value) {
        try {
            if (value != null) {
                field.setAccessible(true);
                field.set(target, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
