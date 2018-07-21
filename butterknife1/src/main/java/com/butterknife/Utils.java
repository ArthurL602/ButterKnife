package com.butterknife;

import android.app.Activity;
import android.view.View;

/**
 * Author      :ljb
 * Date        :2018/7/20
 * Description :
 */
public class Utils {
    public static <T extends View> T findViewById(Activity activity,int viewId){
        return activity.findViewById(viewId);
    }
}
