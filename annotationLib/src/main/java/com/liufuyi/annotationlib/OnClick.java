package com.liufuyi.annotationlib;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@InjectEvent(methodName = "setOnClickListener",paramType = View.OnClickListener.class)
public @interface OnClick {

    int[] value();
}
