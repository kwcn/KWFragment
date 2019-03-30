/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gw00175646.kwfragment.fragment;

import android.util.Log;

public class iLog {
    private static String sAppVersionInfo = " | app ver: unknown";

    public static final String PREFIX_TAG = "KW-";

    public static void setAppVersion(String version) {
        sAppVersionInfo = " | app ver: " + version;
    }

    public static void print(String log) {
        d("KWDY", log);
    }

    // Verbose.
    public static void v(String tag, String msg) {
        Log.v(PREFIX_TAG + tag, msg + sAppVersionInfo);
    }

    public static void v(boolean enableLog, String tag, String msg) {
        if (enableLog) {
            Log.v(PREFIX_TAG + tag, msg + sAppVersionInfo);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        Log.v(PREFIX_TAG + tag, msg + sAppVersionInfo, tr);
    }

    // Debug.
    public static void d(String tag, String msg) {
        Log.d(PREFIX_TAG + tag, msg + sAppVersionInfo);
    }

    public static void d(boolean enableLog, String tag, String msg) {
        if (enableLog) {
            Log.d(PREFIX_TAG + tag, msg + sAppVersionInfo);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        Log.d(PREFIX_TAG + tag, msg + sAppVersionInfo, tr);
    }

    // Info.
    public static void i(String tag, String msg) {
        Log.i(PREFIX_TAG + tag, msg + sAppVersionInfo);
    }

    public static void i(boolean enableLog, String tag, String msg) {
        if (enableLog) {
            Log.i(PREFIX_TAG + tag, msg + sAppVersionInfo);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        Log.i(PREFIX_TAG + tag, msg + sAppVersionInfo, tr);
    }

    // Warn.
    public static void w(String tag, String msg) {
        Log.w(PREFIX_TAG + tag, msg + sAppVersionInfo);
    }

    public static void w(boolean enableLog, String tag, String msg) {
        if (enableLog) {
            Log.w(PREFIX_TAG + tag, msg + sAppVersionInfo);
        }
    }

    public static void w(String tag, String msg, Throwable tr) {
        Log.w(PREFIX_TAG + tag, msg + sAppVersionInfo, tr);
    }

    // Error.
    public static void e(String tag, String msg) {
        Log.e(PREFIX_TAG + tag, msg + sAppVersionInfo);
    }

    public static void e(boolean enableLog, String tag, String msg) {
        if (enableLog) {
            Log.e(PREFIX_TAG + tag, msg + sAppVersionInfo);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(PREFIX_TAG + tag, msg + sAppVersionInfo, tr);
    }
}
