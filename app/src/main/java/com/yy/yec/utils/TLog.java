package com.yy.yec.utils;

import android.util.Log;

public class TLog {
    public static final String LOG_TAG = "YEC";
    public static boolean DEBUG = true;

    public TLog() {
    }

    public static final void analytics(String log) {
        if (DEBUG)
            Log.d(LOG_TAG, log);
    }

    public static final void error(String log) {
        if (DEBUG)
            Log.e(LOG_TAG, "" + log);
    }

    public static final void info(String log) {
        if (DEBUG)
            Log.i(LOG_TAG, log);
    }

    public static final void info(String tag, String log) {
        if (DEBUG)
            Log.i(tag, log);
    }

    public static final void v(String log) {
        if (DEBUG)
            Log.v(LOG_TAG, log);
    }

    public static final void warn(String log) {
        if (DEBUG)
            Log.w(LOG_TAG, log);
    }
}
