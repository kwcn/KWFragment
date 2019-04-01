package com.example.gw00175646.kwfragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.gw00175646.kwfragment.fragment.ListUtils;
import com.example.gw00175646.kwfragment.fragment.QueryArgs;
import com.example.gw00175646.kwfragment.fragment.RecyclerViewFragment;

public class MyFragment extends RecyclerViewFragment<MyAdapter> {
    @Override
    protected MyAdapter onCreateAdapter() {
        return new MyAdapter.Builder(this).setText1Col(ListUtils.TestColumn.TEXT1).setText2Col
                (ListUtils.TestColumn.TEXT2).setText3Col(ListUtils.TestColumn.TEXT3).build();
    }

    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected QueryArgs onCreateQueryArgs(int id) {
        return null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<Cursor>(getContext()) {
            @Nullable
            @Override
            public Cursor loadInBackground() {
                return ListUtils.getTestMatrixCursor(getContext());
            }
        };
    }
}
