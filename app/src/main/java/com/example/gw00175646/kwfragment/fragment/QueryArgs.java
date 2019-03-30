package com.example.gw00175646.kwfragment.fragment;

import android.net.Uri;

public class QueryArgs {

    public Uri uri;

    public String[] projection;

    public String selection;

    public String[] selectionArgs;

    public String orderBy;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("uri: ").append(uri).append(", ");
        sb.append("projection: ");
        if (projection != null) {
            for (String str : projection) {
                sb.append(str).append(", ");
            }
        } else {
            sb.append("null, ");
        }
        sb.append("selection: ").append(selection).append(", ");
        sb.append("selectionArgs: ");
        if (selectionArgs != null) {
            for (String str : selectionArgs) {
                sb.append(str).append(", ");
            }
        } else {
            sb.append("null, ");
        }
        sb.append("orderBy: ").append(orderBy);
        return sb.toString();
    }
}