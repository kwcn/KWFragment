package com.example.gw00175646.kwfragment.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.gw00175646.kwfragment.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;

import static com.example.gw00175646.kwfragment.fragment.DefaultConstants.UNDEFINED;

public abstract class RecyclerViewFragment<T extends RecyclerCursorAdapter> extends BaseFragment
        implements LoaderCallbacks<Cursor> {

    private static final String TAG = "RecyclerViewFragment";
    // 2000ms后才能再次加载数据到界面，避免频繁绘制
    public static final int BASE_THROTTLE_TIME = 2000;
    // 显示界面进入动画
    protected static final int LIST_SHOWN_WITH_ANIMATION = 0x00000001;
    // 显示进度条
    protected static final int LIST_SHOWN_WITH_LOADING_PROGRESS = 0x0000002;
    // 显示空界面
    protected static final int LIST_SHOWN_WITH_EMPTY_PAGE = 0x00000004;

    private final HashSet<Integer> mListLoaderIds = new HashSet<>();
    private final HashSet<Integer> mExtraLoaderIds = new HashSet<>();
    private final ListLoaderCallbacksWrapper mListLoaderCallbacksWrapper =
            new ListLoaderCallbacksWrapper(this);

    private Context mContext;
    protected T mAdapter;
    private int mUpdateThrottle = BASE_THROTTLE_TIME;
    private boolean mListShown = true;
    private boolean mShownWithAnimation = false;
    private boolean mShownWithLoadingProgress = false;
    private boolean mShownWithEmptyPage = false;
    @LayoutRes
    private int mEmptyViewLayoutResId = UNDEFINED;
    @StringRes
    private int mEmptyViewStringResId = UNDEFINED;
    private View mEmptyView;
    private EmptyViewCreator mEmptyViewCreator;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.listContainer)
    ViewGroup mListContainer;
    @BindView(R.id.progressContainer)
    View mProgressContainer;

    public interface EmptyViewCreator {
        View createEmptyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseTag = TAG;
        mLifeCycleLogEnabled = true;
        mContext = context.getApplicationContext();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.ui_recycler_view_list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(onCreateLayoutManager());
        mAdapter = onCreateAdapter();
        mRecyclerView.setAdapter(mAdapter);
        // 默认设置使用动画、加载进度条和空界面
        setListShown(false, LIST_SHOWN_WITH_ANIMATION | LIST_SHOWN_WITH_LOADING_PROGRESS |
                LIST_SHOWN_WITH_EMPTY_PAGE);
        // 设置默认空界面
        setEmptyView(R.layout.default_empty_view, R.string.no_content);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        QueryArgs args = onCreateQueryArgs(id);
        // CursorLoader里注册观察者，当db发生改变时会驱动数据界面刷新
        AsyncTaskLoader loader = new CursorLoader(mContext, args.uri, args.projection, args
                .selection, args.selectionArgs, args.orderBy);
        loader.setUpdateThrottle(mUpdateThrottle);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        setListShown(cursor.getCount() > 0 || mShownWithEmptyPage);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    protected abstract T onCreateAdapter();

    protected abstract LayoutManager onCreateLayoutManager();

    protected abstract QueryArgs onCreateQueryArgs(int id);

    // 设置列表显示，并设置过程中是否使用动画、进度条和空界面
    protected final void setListShown(boolean shown, int flags) {
        mShownWithAnimation = (flags & LIST_SHOWN_WITH_ANIMATION) == LIST_SHOWN_WITH_ANIMATION;
        mShownWithLoadingProgress = (flags & LIST_SHOWN_WITH_LOADING_PROGRESS) ==
                LIST_SHOWN_WITH_LOADING_PROGRESS;
        mShownWithEmptyPage =
                (flags & LIST_SHOWN_WITH_EMPTY_PAGE) == LIST_SHOWN_WITH_EMPTY_PAGE;
        iLog.d(TAG,
                this + " setListShownFlag() | mShownWithAnimation: " + mShownWithAnimation +
                        " | mShownWithLoadingProgress: " + mShownWithLoadingProgress +
                        " | mShownOnlyHavingValidDataOnly: " + mShownWithEmptyPage);
        setListShown(shown);
    }

    protected final void setListShown(boolean shown) {
        iLog.d(TAG, this + " setListShown() - shown: " + shown);
        if (mProgressContainer == null) {
            throw new IllegalStateException("Can't be used with a custom content view");
        }
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (mShownWithAnimation) {
                mProgressContainer.startAnimation(
                        AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out));
                mListContainer.startAnimation(
                        AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            mProgressContainer.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (mShownWithAnimation) {
                mProgressContainer.startAnimation(
                        AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                mListContainer.startAnimation(
                        AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out));
            } else {
                mProgressContainer.clearAnimation();
                mListContainer.clearAnimation();
            }
            if (mShownWithLoadingProgress) {
                mProgressContainer.setVisibility(View.VISIBLE);
            } else {
                mProgressContainer.setVisibility(View.GONE);
            }
            mListContainer.setVisibility(View.GONE);
        }
    }

    // 设置空界面
    protected final void setEmptyView(@LayoutRes int layoutResId, @StringRes int stringResId) {
        mEmptyView = null;
        mEmptyViewLayoutResId = layoutResId;
        mEmptyViewStringResId = stringResId;
    }

    protected final void setEmptyView(EmptyViewCreator emptyViewCreator) {
        mEmptyView = null;
        mEmptyViewCreator = emptyViewCreator;
    }

    protected final void setEmptyViewVisibility(boolean isEmpty) {
        if (isEmpty) {
            if (mEmptyView == null) {
                if (mEmptyViewCreator != null) {
                    mEmptyView = mEmptyViewCreator.createEmptyView();
                    mListContainer.addView(mEmptyView);
                } else if (mEmptyViewLayoutResId != UNDEFINED) {
                    mEmptyView = LayoutInflater.from(getActivity())
                            .inflate(mEmptyViewLayoutResId, mListContainer, false);
                    TextView textView = mEmptyView.findViewById(R.id.no_item_text);
                    if (textView == null) {
                        throw new RuntimeException("no item view must contains R.id.no_item_text");
                    }
                    textView.setText(mEmptyViewStringResId);
                    mListContainer.addView(mEmptyView);
                }
            }
            if (mEmptyView != null) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mEmptyView != null) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    // 设置数据刷新阀值
    protected final void setUpdateThrottle(int updateThrottle) {
        mUpdateThrottle = updateThrottle;
    }

    public T getAdapter() {
        return mAdapter;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    // 开启loader管理器，当id相同时，如果之前已经加载过的loader不会再次进行加载（可用于横竖屏幕切换时）
    protected void initListLoader(int id) {
        mListLoaderIds.add(id);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<Cursor> l = loaderManager.initLoader(id, null, mListLoaderCallbacksWrapper);
        iLog.d(TAG, this + " initListLoader() - loader : " + l + " l.isReset() : " +
                (l == null ? " Loader is null" : l.isReset()) + " | loaderManager: " +
                loaderManager);
    }

    protected void initExtraLoader(int id) {
        mExtraLoaderIds.add(id);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        Loader<Cursor> l = loaderManager.initLoader(id, null, this);
        iLog.d(TAG, this + " initExtraLoader() - loader : " + l + " l.isReset() : " +
                (l == null ? " Loader is null" : l.isReset()) + " | loaderManager: " +
                loaderManager);
    }

    protected void restartListLoader(int id) {
        LoaderManager.getInstance(this).restartLoader(id, null, mListLoaderCallbacksWrapper);
    }

    protected void restartExtraLoader(int id) {
        LoaderManager.getInstance(this).restartLoader(id, null, this);
    }

    // 每次都重新加载数据，不论id是否相同
    public void restartListLoader() {
        if (!isAdded()) {
            iLog.w(TAG, this + " restartListLoader() failed | !isAdded");
            return;
        }
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        for (int id : mListLoaderIds) {
            loaderManager.restartLoader(id, null, mListLoaderCallbacksWrapper);
        }
        for (int id : mExtraLoaderIds) {
            loaderManager.restartLoader(id, null, this);
        }
        iLog.w(TAG,
                this + " restartListLoader() is called before mListLoaderId is initiated");
    }

    protected final Cursor makeViewType(int viewType, Cursor data, int position) {
        String[] projection = data.getColumnNames();
        MatrixCursor cursor = new MatrixCursor(projection);

        List<Object> columnValues = new ArrayList<>();
        columnValues.add(RecyclerCursorAdapter.convertToId(viewType, position));
        int len = projection.length;
        for (int i = 1; i < len; i++) {
            columnValues.add(UNDEFINED);
        }
        cursor.addRow(columnValues);
        return cursor;
    }

    private class ListLoaderCallbacksWrapper implements LoaderCallbacks<Cursor> {
        private final RecyclerViewFragment<?> mFragment;

        ListLoaderCallbacksWrapper(RecyclerViewFragment<?> fg) {
            mFragment = fg;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            iLog.d(TAG, mFragment + " ListLoaderCallbacksWrapper.onCreateLoader() id: " + id);
            return mFragment.onCreateLoader(id, args);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Activity activity = getActivity();
            iLog.d(TAG,
                    mFragment + " ListLoaderCallbacksWrapper.onLoadFinished() count: " +
                            (data != null ? data.getCount() : -1) + "  id: " + loader.getId() +
                            " has it? " + mFragment.mListLoaderIds.contains(loader.getId()) +
                            " | getActivity is null? " + (activity == null));

            if (activity == null || !mFragment.mListLoaderIds.contains(loader.getId())) {
                return;
            }

            if (data == null) {
                QueryArgs queryArgs = onCreateQueryArgs(loader.getId());
                throw new IllegalArgumentException(
                        mFragment + " null cursor returned. Please check | QueryArgs: " +
                                (queryArgs != null ? queryArgs.toString() : null));
            }

            int count = data != null ? data.getCount() : 0;
            setEmptyViewVisibility(count == 0);
            mFragment.onLoadFinished(loader, data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            iLog.d(TAG, mFragment + " ListLoaderCallbacksWrapper.onLoaderReset() id: " + loader
                    .getId());
            mFragment.onLoaderReset(loader);
        }
    }
}
