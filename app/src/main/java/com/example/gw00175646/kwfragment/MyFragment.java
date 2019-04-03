package com.example.gw00175646.kwfragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.gw00175646.kwfragment.fragment.ListUtils;
import com.example.gw00175646.kwfragment.fragment.QueryArgs;
import com.example.gw00175646.kwfragment.fragment.RecyclerViewFragment;

public class MyFragment extends RecyclerViewFragment<MyAdapter> {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpdateThrottle(0);
        // 启动loader，异步加载数据到界面
        initListLoader(1);
    }

    // 使用Builder构造adapter
    @Override
    protected MyAdapter onCreateAdapter() {
        return new MyAdapter.Builder(this).setText1Col(ListUtils.TestColumn.TEXT1).setText2Col
                (ListUtils.TestColumn.TEXT2).setText3Col(ListUtils.TestColumn.TEXT3)
                .setThumbnailCol(ListUtils.TestColumn.THUMBNAIL).build();
    }

    // 配置布局管理器
    @Override
    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    // 配置数据库查询条件，可以开启多个loader，配置多组数据，分开管理
    @Override
    protected QueryArgs onCreateQueryArgs(int loaderId) {
        return null;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        // 没有写db，暂用测试数据代替
        return new AsyncTaskLoader<Cursor>(getContext()) {
            @Nullable
            @Override
            public Cursor loadInBackground() {
                // 加载耗时数据
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
