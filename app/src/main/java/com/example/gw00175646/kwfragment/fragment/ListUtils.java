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

    public static MatrixCursor getTestMatrixCursor(Context context) {
        int testViewType = 1;
        String[] columns = {TestColumn.ID, TestColumn.TEXT1, TestColumn.TEXT2, TestColumn.TEXT3,
                TestColumn.THUMBNAIL};
        MatrixCursor cursor = new MatrixCursor(columns);
        for (int i = 0; i < 10; i++) {
            List<Object> list = new ArrayList<>();
            list.add(RecyclerCursorAdapter.convertToId(testViewType, i));
            list.add(DefaultConstants.UNKNOWN);
            list.add(DefaultConstants.UNKNOWN);
            list.add(DefaultConstants.UNKNOWN);
            list.add(DefaultConstants.UNKNOWN);
            cursor.addRow(list);
        }
        return cursor;
    }
}
