package com.example.gw00175646.kwfragment.fragment;

import android.content.Context;
import android.text.TextUtils;

import com.example.gw00175646.kwfragment.R;

public class DefaultUiUtils {
    public static String transUnknownString(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            text = "<" + context.getResources().getString(R.string.unknown) + ">";
        }
        return text;
    }
}
