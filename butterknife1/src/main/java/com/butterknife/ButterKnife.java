package com.butterknife;

import android.app.Activity;

import java.lang.reflect.Constructor;

/**
 * Author      :ljb
 * Date        :2018/7/19
 * Description :
 */
public class ButterKnife {
    private ButterKnife() {
        throw new AssertionError("No instances.");
    }

    public static Unbinder bind(Activity  activity){
        String viewBindingClassName = activity.getClass().getName() + "_ViewBinding";
        try {
            Class<? extends Unbinder> viewBindingClass = (Class<? extends Unbinder>) Class.forName(viewBindingClassName);
            Constructor<? extends Unbinder> viewBindingConstructor = viewBindingClass.getDeclaredConstructor(activity.getClass());
            Unbinder unbinder = viewBindingConstructor.newInstance(activity);
            return unbinder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Unbinder.EMPTY;
    }
}
