package com.example.gw00175646.kwfragment.fragment;

import android.content.Context;
import android.database.MatrixCursor;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    public interface TestColumn {
        String ID = "_id";
        String TEXT1 = "text1";
        String TEXT2 = "text2";
        String TEXT3 = "text3";
        String THUMBNAIL = "thumbnail";
    }

    public static MatrixCursor mCursor;

    public static void setTestMatrixCursor() {
        String[] columns = {TestColumn.ID, TestColumn.TEXT1, TestColumn.TEXT2, TestColumn.TEXT3,
                TestColumn.THUMBNAIL};
        mCursor = new MatrixCursor(columns);
        int testViewType = 1;
        for (int i = 1; i < 20; i++) {
            List<Object> list = new ArrayList<>();
            list.add(RecyclerCursorAdapter.convertToId(testViewType, i));
            list.add(i);
            list.add(DefaultConstants.UNKNOWN);
            list.add(DefaultConstants.UNKNOWN);
            list.add(DefaultConstants.UNKNOWN);
            mCursor.addRow(list);
        }
    }

    public static MatrixCursor getTestMatrixCursor(Context context) {
        if (mCursor == null) {
            String[] columns = {TestColumn.ID, TestColumn.TEXT1, TestColumn.TEXT2, TestColumn.TEXT3,
                    TestColumn.THUMBNAIL};
            mCursor = new MatrixCursor(columns);
        }
        return mCursor;
    }
}
