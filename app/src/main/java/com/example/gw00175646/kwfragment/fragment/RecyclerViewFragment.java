package com.example.gw00175646.kwfragment.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;

import com.example.gw00175646.kwfragment.R;

import butterknife.BindView;

public abstract class RecyclerViewFragment<T extends RecyclerCursorAdapter> extends BaseFragment
        implements LoaderCallbacks<Cursor> {
    private static final String TAG = "RecyclerViewFragment";
    public static final int BASE_THROTTLE_TIME = 2000;

    private Context mContext;
    protected T mAdapter;
    private int mUpdateThrottle = BASE_THROTTLE_TIME;

    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.progressContainer)
    private View mProgressContainer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseTag = TAG;
        mLifeCycleLogEnabled = true;
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setLayoutManager(onCreateLayoutManager());
        mAdapter = onCreateAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && getUserVisibleHint()) {
            //setMenuVisibility(true);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        QueryArgs args = onCreateQueryArgs(id);
        AsyncTaskLoader loader = null;
        loader.setUpdateThrottle(mUpdateThrottle);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        //  mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //  mAdapter.swapCursor(null);
    }

    protected abstract T onCreateAdapter();

    protected abstract LayoutManager onCreateLayoutManager();

    protected abstract QueryArgs onCreateQueryArgs(int id);

    @Override
    protected int getLayoutRes() {
        return R.layout.ui_recycler_view_list;
    }

    protected final void setUpdateThrottle(int updateThrottle) {
        mUpdateThrottle = updateThrottle;
    }

    public T getAdapter() {
        return mAdapter;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}
