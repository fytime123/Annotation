package com.liufuyi.annotationlib;

import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AnnotationInject {

    public static void injectView(Object object) {
        if (object == null) {
            return;
        }
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getFields();

        Method method = null;
        try {
            method = clazz.getMethod("findViewById", Integer.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (method == null) {
            return;
        }

        for (Field field : fields) {
            BindView bindView = field.getAnnotation(BindView.class);
            if (bindView == null) {
                continue;
            }
            int id = bindView.value();
            try {
                View view = (View) method.invoke(object, id);
                field.setAccessible(true);
                field.set(object, view);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }


    public static void injectEvent(Object object) {
        if (object == null) {
            return;
        }
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            //获取该方法上所有的注解
            Annotation[] annotations = method.getAnnotations();

            for (Annotation annotation : annotations) {
                //获取注解上的类型
                Class<?> annotationType = annotation.annotationType();
                //获取注解上对应的参数
                InjectEvent injectEvent = annotationType.getAnnotation(InjectEvent.class);
                if (injectEvent == null) {
                    continue;
                }
                Log.v("liufuyi2", annotationType.toString());

                //需要注入的方法名例如：setOnClickListener
                String methodName = injectEvent.methodName();
                //注入方法的参数类型，例如：View.OnClickListener.class
                Class listenerClass = injectEvent.paramType();

                Method annotationMethod = null;
                try {
                    annotationMethod = annotationType.getDeclaredMethod("value");
                    int[] resourcesIds = (int[]) annotationMethod.invoke(annotation);

                    for (int i = 0; i < resourcesIds.length; i++) {
                        Method findViewByIdMethod = clazz.getMethod("findViewById", int.class);
                        View view = (View) findViewByIdMethod.invoke(object, resourcesIds[i]);
                        //获取到获取方法setOnClickListener
                        Method setListener = view.getClass().getMethod(methodName, listenerClass);
                        //使用动态代理获取获取到OnClickListener代理对象
                        ListenerHandler eventInvocationHandler = new ListenerHandler(object, method);

                        Object proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                                new Class[]{listenerClass}, eventInvocationHandler);

                        setListener.invoke(view, proxy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }

    }


    private static class ListenerHandler implements InvocationHandler {

        // 要代理的原始对象
        private Object obj;
        private Method action;

        public ListenerHandler(Object obj, Method action) {
            super();
            this.obj = obj;
            this.action = action;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Object result = null;

            // 调用原始对象的方法
            result = action.invoke(obj, args);

            return result;
        }

    }
}


