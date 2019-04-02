package com.example.gw00175646.kwfragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.gw00175646.kwfragment.fragment.ListUtils;
import com.example.gw00175646.kwfragment.fragment.QueryArgs;
import com.example.gw00175646.kwfragment.fragment.RecyclerViewFragment;

public class MyFragment extends RecyclerViewFragment<MyAdapter> {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListLoader(1);
    }

    @Override
    protected MyAdapter onCreateAdapter() {
        return new MyAdapter.Builder(this).setText1Col(ListUtils.TestColumn.TEXT1).setText2Col
                (ListUtils.TestColumn.TEXT2).setText3Col(ListUtils.TestColumn.TEXT3)
                .setThumbnailCol(ListUtils.TestColumn.THUMBNAIL).build();
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
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return ListUtils.getTestMatrixCursor(getContext());
            }


            @Override
            public void stopLoading() {
                super.stopLoading();
                cancelLoad();
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }
        };
    }
}
