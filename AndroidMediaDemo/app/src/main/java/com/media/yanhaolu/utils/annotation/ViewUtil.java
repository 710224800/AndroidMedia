package com.media.yanhaolu.utils.annotation;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by yanhaolu on 2017/8/25.
 */

public class ViewUtil {

    /**
     * 开始注解
     *
     * @param activity
     */
    public static void inject(Activity activity) {
        // 判断对应的Activity上面是否使用了ContentView注解
        if (activity.getClass().isAnnotationPresent(ContentView.class)) {
            // 如果加了注解则转换为ContentView注解
            ContentView contentView = activity.getClass().getAnnotation(
                    ContentView.class);
            int layoutResID = contentView.value();// 获取布局的资源id
            activity.setContentView(layoutResID);// 设置布局内容
            findView(activity);// 绑定view
            registEvent(activity);// 注册点击事件
        } else {
            findView(activity);// 绑定view
            registEvent(activity);// 注册点击事件
        }
    }

    /**
     * 注册点击事件
     *
     * @param activity
     */
    private static void registEvent(final Activity activity) {
        // 获取所有的方法
        Method[] method = activity.getClass().getMethods();
        for (final Method m : method) {
            // 判断方法上面使用存在OnClick注解
            if (m.isAnnotationPresent(OnClick.class)) {
                // 如果存在则转换
                OnClick on = m.getAnnotation(OnClick.class);
                int[] values = on.value();// 获取需要绑定点击事件的控件id
                for (int i = 0; i < values.length; i++) {
                    final View view = activity.findViewById(values[i]);
                    // 给其设置点击事件
                    view.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            try {
                                m.invoke(activity, view);// 调用activity里面的方法
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 绑定view控件
     *
     * @param activity
     */
    private static void findView(Activity activity) {
        // 获取所有属性
        Field[] fields = activity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            // 判断属性上面是否存在ViewInject注解
            if (field.isAnnotationPresent(ViewInject.class)) {
                ViewInject vi = field.getAnnotation(ViewInject.class);
                int resourceId = vi.value();// 获取控件资源id
                try {
                    field.set(activity, activity.findViewById(resourceId));// 绑定控件
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}