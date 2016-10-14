package com.ganna.uber.util;

import android.app.Activity;
import android.content.Intent;

import com.ganna.uber.ui.main.MainScreen;

/**
 * Created by Ahmed on 10/14/2016.
 */

public class ScreenManager {
    public static final void launchMainScreen(Activity activity){
        activity.startActivity(new Intent(activity, MainScreen.class));
        activity.finish();
    }
}
