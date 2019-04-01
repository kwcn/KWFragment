package com.example.gw00175646.kwfragment.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.gw00175646.kwfragment.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.gw00175646.kwfragment.fragment.DefaultConstants.UNDEFINED;

public abstract class RecyclerCursorAdapter<VH extends RecyclerCursorAdapter.ViewHolder> extends
        RecyclerView.Adapter<VH> {
    private static final String TAG = "RecyclerCursorAdapter";

    protected final Fragment mFragment;
    protected final Context mContext;
    private final String mText1Col;
    private final String mText2Col;
    private final String mText3Col;
    private final String mThumbnailId;
    private final String mThumbnailFullUriCol;
    private final String mKeywordCol;
    private final SparseArray<Uri> mThumbnailUriSet;
    private final Uri mThumbnailUri;
    @ColorRes
    private final int mText1ColorResId;
    @ColorRes
    private final int mText2ColorResId;
    @ColorRes
    private final int mText3ColorResId;
    @DimenRes
    private final int mThumbnailSizeResId;
    private float mText1FontSize = UNDEFINED;
    private final SparseArray<View> mPredefinedHeaderViews = new SparseArray<>();
    private final SparseIntArray mPredefinedViewResources = new SparseIntArray();
    private final List<Integer> mHeaderViewTypes = new ArrayList<>();
    private final List<Integer> mFooterViewTypes = new ArrayList<>();
    protected final RecyclerViewableList mRecyclerViewableList;

    private OnHeaderViewCreatedListener mOnHeaderViewCreatedListener;
    private int mRowIDColumn;
    protected int mText1Index = UNDEFINED;
    protected int mText2Index = UNDEFINED;
    protected int mText3Index = UNDEFINED;
    protected int mThumbnailKeyIndex = UNDEFINED;
    private int mThumbnailFullUriIndex = UNDEFINED;
    private int mPrivateModeColIndex = UNDEFINED;
    protected int mKeywordIndex = UNDEFINED;
    protected int mCpAttrsColIndex = UNDEFINED;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemLayoutChangedListener mOnHeaderItemLayoutChangedListener;
    private View.OnGenericMotionListener mOnGenericMotionListener;
    boolean mDataValid;
    private Cursor mCursor;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position, long id);
    }

    public interface OnItemLayoutChangedListener {
        void onLayoutChange(View view, int position, int left, int top, int right, int bottom,
                            int oldLeft, int oldTop, int oldRight, int oldBottom);
    }

    public interface OnHeaderViewCreatedListener {
        void onHeaderViewCreated(int viewType, View headerView);
    }

    public RecyclerCursorAdapter(AbsBuilder<?> builder) {
        mFragment = builder.mFragment;
        mRecyclerViewableList =
                mFragment instanceof RecyclerViewableList ? (RecyclerViewableList) mFragment : null;

        mContext = builder.mContext;
        mText1Col = builder.mText1Col;
        mText2Col = builder.mText2Col;
        mText3Col = builder.mText3Col;
        mThumbnailId = builder.mThumbnailId;
        mThumbnailFullUriCol = builder.mThumbnailFullUriCol;
        mKeywordCol = builder.mKeywordCol;

        mText1ColorResId = builder.mText1ColorResId;
        mText2ColorResId = builder.mText2ColorResId;
        mText3ColorResId = builder.mText3ColorResId;

        mThumbnailUriSet = builder.mThumbnailUriSet;
        mThumbnailUri = builder.mThumbnailUri;
        mThumbnailSizeResId = builder.mThumbnailSizeResId;

        setHasStableIds(true);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View predefinedView = mPredefinedHeaderViews.get(viewType);
        int resource = mPredefinedViewResources.get(viewType, UNDEFINED);
        if (predefinedView == null && resource != UNDEFINED) {
            predefinedView =
                    LayoutInflater.from(mFragment.getActivity()).inflate(resource, parent, false);
            if (mOnHeaderViewCreatedListener != null && mHeaderViewTypes.contains(viewType)) {
                mOnHeaderViewCreatedListener.onHeaderViewCreated(viewType, predefinedView);
            }
        }
        iLog.d(TAG,
                mFragment + " | " + TAG + " onCreateViewHolder() viewType: " + viewType +
                        " | predefinedView: " + predefinedView);
        VH holder = onCreateViewHolder(parent, viewType, predefinedView);
        if (holder.textView1 != null && mText1FontSize == UNDEFINED) {
            mText1FontSize = holder.textView1.getTextSize();
        }
        return holder;
    }

    protected abstract VH onCreateViewHolder(ViewGroup parent, int viewType,
                                             @Nullable View itemView);

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        iLog.d(TAG, mFragment + " | " + TAG + " onBindViewHolder() holder: " + holder +
                " | position: " + position);
        if (getItemViewType(position) < 0) {
            return;
        }

        Cursor c = getCursorOrThrow(position);
        onBindViewHolderTextView(holder, position, c);
        if (holder.radioButton != null) {
            onBindViewHolderRadioButton(holder, position);
        }
        if (holder.thumbnailView != null) {
            onBindViewHolderThumbnailView(holder, position, c);
        }

        onBindViewHolderItemEnabled(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException(
                    "couldn't move cursor to position " + position + " | cursorCount: " +
                            (mCursor != null ? mCursor.getCount() : null));
        }
        long id = mCursor.getLong(mRowIDColumn);
        return mCursor.getLong(mRowIDColumn) > 0 ? DefaultViewType.NORMAL :
                convertToViewType(id, position);
    }

    @Override
    public int getItemCount() {
        if (!mDataValid) {
            iLog.w(TAG, mFragment + " data invalid | getItemCount()");
        }
        if (mDataValid && mCursor != null && !mCursor.isClosed()) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        Cursor c = getCursor(position);
        return c != null ? c.getLong(mRowIDColumn) : UNDEFINED;
    }

    @Nullable
    public String getItemKeyword(int position) {
        if (mKeywordIndex != UNDEFINED) {
            Cursor c = getCursor(position);
            return c != null ? c.getString(mKeywordIndex) : null;
        } else {
            return null;
        }
    }

    @Nullable
    public String getText1(int position) {
        if (mText1Index != UNDEFINED) {
            Cursor c = getCursor(position);
            return c != null ? c.getString(mText1Index) : null;
        } else {
            return null;
        }
    }

    public static int convertToViewType(long id, int position) {
        if (id > DefaultViewType.MIN) {
            // This means viewType is same as id.
            iLog.d(TAG, "convertToViewType() position: " + position + " | id: " + id +
                    " return as it is");
            return (int) id;
        }
        iLog.d(TAG,
                "convertToViewType() position: " + position + " id: " + id + " -> viewType: " +
                        ((int) id + position - DefaultViewType.MIN));
        return (int) id + position - DefaultViewType.MIN;
    }

    public static long convertToId(int viewType, int position) {
        iLog.d(TAG, "convertToId() position: " + position + " | viewType: " + viewType +
                " -> id: " + (viewType - position + DefaultViewType.MIN));
        return viewType - position + DefaultViewType.MIN;
    }

    protected void initColIndex(Cursor newCursor) {
        iLog.d(TAG,
                mFragment + " | " + TAG + " initColIndex() - newCursor: " + newCursor);
        if (mText1Col != null) {
            mText1Index = newCursor.getColumnIndexOrThrow(mText1Col);
        }
        if (mText2Col != null) {
            mText2Index = newCursor.getColumnIndexOrThrow(mText2Col);
        }
        if (mText3Col != null) {
            mText3Index = newCursor.getColumnIndexOrThrow(mText3Col);
        }
        if (mThumbnailId != null) {
            mThumbnailKeyIndex = newCursor.getColumnIndexOrThrow(mThumbnailId);
        }
        if (mThumbnailFullUriCol != null) {
            mThumbnailFullUriIndex = newCursor.getColumnIndexOrThrow(mThumbnailFullUriCol);
        }
        if (mKeywordCol != null) {
            mKeywordIndex = newCursor.getColumnIndexOrThrow(mKeywordCol);
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        iLog.d(TAG, mFragment + " | " + TAG + " swapCursor() | prevCursor: " + mCursor +
                " | newCursor: " + newCursor);
        if (newCursor == mCursor) {
            return null;
        }
        int newCursorCount = newCursor != null ? newCursor.getCount() : 0;
        if (newCursorCount != 0) {
            initColIndex(newCursor);
        }

        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if (newCursorCount != 0) {
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // invokeNotifyDataSetChanged();
        } else {
            mRowIDColumn = UNDEFINED;
            mDataValid = false;
            //   invokeNotifyDataSetChanged();
        }
        //        if (mReorderState.isEnabled()) {
        //            mReorderState.resetPositions(getItemCount());
        //        }
        return oldCursor;
    }

    /**
     * Returns the cursor.
     *
     * @return the cursor.
     */
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Sub classes can use this method to access cursor matched position
     *
     * @param position position of recyclerView
     * @return cursor matched with position
     */
    public Cursor getCursor(int position) {
        return getCursorInternal(position, false);
    }

    /**
     * Sub classes can use this method to access cursor matched position
     *
     * @param position position of recyclerView
     * @return cursor matched with position
     */
    protected final Cursor getCursorOrThrow(int position) {
        return getCursorInternal(position, true);
    }

    private Cursor getCursorInternal(int position, boolean throwException) {
        if (!mDataValid) {
            if (throwException) {
                throw new IllegalStateException(
                        "this should only be called when the cursor is valid");
            } else {
                iLog.w(TAG, mFragment + " data invalid | getCursorInternal()");
                return null;
            }
        }
        if (!mCursor.moveToPosition(position)) {
            if (throwException) {
                throw new IllegalStateException("couldn't move cursor to position " + position);
            } else {
                return null;
            }
        }
        return mCursor;
    }

    protected void onBindViewHolderTextView(VH holder, int position, Cursor c) {
        if (holder.textView1 != null && mText1Index != UNDEFINED) {
            holder.textView1
                    .setText(DefaultUiUtils.transUnknownString(mContext, c.getString(mText1Index)));
        }
        if (holder.textView2 != null && mText2Index != UNDEFINED) {
            holder.textView2
                    .setText(DefaultUiUtils.transUnknownString(mContext, c.getString(mText2Index)));
        }
        if (holder.textView3 != null && mText3Index != UNDEFINED) {
            holder.textView3
                    .setText(DefaultUiUtils.transUnknownString(mContext, c.getString(mText3Index)));
        }
    }

    protected void onBindViewHolderThumbnailView(VH holder, int position, Cursor c) {
        if (mThumbnailFullUriIndex != UNDEFINED) {
//            AsyncArtworkLoader.withDimension(mThumbnailSizeResId)
//                    .withFullUri(c.getString(mThumbnailFullUriIndex)).loadToPublisher(
//                    new RecyclerImageViewPublisher(holder.thumbnailView,
//                            MDefaultArtworkUtils.DEFAULT_ALBUM_ART, mIsDownKeyPerforming));
            return;
        }

//        AsyncArtworkLoader.withDimension(mThumbnailSizeResId).withBaseUri(uri, albumId)
//                .loadToPublisher(new RecyclerImageViewPublisher(holder.thumbnailView,
//                        MDefaultArtworkUtils.DEFAULT_ALBUM_ART, mIsDownKeyPerforming));
    }

    private void onBindViewHolderRadioButton(VH holder, int position) {
        if (mRecyclerViewableList != null) {
//            holder.radioButton.setChecked(
//                    mRecyclerViewableList.getRecyclerView().getCheckedItemPositions()
//                            .get(position));
        }
    }

    protected void onBindViewHolderItemEnabled(final VH holder, int position) {
        boolean isEnabled = isEnabled(position);
        float alpha = isEnabled ? ViewAlpha.NORMAL : ViewAlpha.DIM;
        holder.itemView.setAlpha(alpha);
        holder.itemView.setClickable(isEnabled);
        holder.itemView.setEnabled(isEnabled);
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView1;
        public final TextView textView2;
        public final TextView textView3;
        public final ImageView thumbnailView;
        public final RadioButton radioButton;

        public final List<View> animateViews = new ArrayList<>();
        public final List<Integer> animateViewLayers = new ArrayList<>();

        private View.OnGenericMotionListener mOnGenericMotionListener;

        public ViewHolder(final RecyclerCursorAdapter<?> adapter, final View itemView,
                          int viewType) {
            super(itemView);

            if (viewType > 0 && adapter.mOnItemClickListener != null) {
                initOnClickListener(adapter, itemView);
            }

            if (viewType > 0 && adapter.mOnItemLongClickListener != null) {
                initOnLongClickListener(adapter, itemView);
            }

//            if (adapter.hasHeaderView(viewType)) {
//                initOnHeaderItemLayoutChangedListener(adapter, itemView);
//            }

            Resources res = adapter.mFragment.getResources();

            textView1 = null;
            //itemView.findViewById(R.id.text1);
            if (textView1 != null) {
                textView1.setTextColor(
                        ResourcesCompat.getColor(res, adapter.mText1ColorResId, null));
            }

            textView2 = null;
            //itemView.findViewById(R.id.text2);
            if (textView2 != null) {
                textView2.setTextColor(
                        ResourcesCompat.getColor(res, adapter.mText2ColorResId, null));
                textView2
                        .setVisibility(adapter.mText2Index != UNDEFINED ? View.VISIBLE : View.GONE);
            }

            textView3 = null;
            //itemView.findViewById(R.id.text3);
            if (textView3 != null) {
                textView3.setTextColor(
                        ResourcesCompat.getColor(res, adapter.mText3ColorResId, null));
                textView3
                        .setVisibility(adapter.mText3Index != UNDEFINED ? View.VISIBLE : View.GONE);
            }

            ImageView iv = null;
            //itemView.findViewById(R.id.thumbnail);

            if (iv != null) {
                if (adapter.mThumbnailKeyIndex != UNDEFINED ||
                        adapter.mThumbnailFullUriIndex != UNDEFINED) {
                    iv.setVisibility(View.VISIBLE);
                    thumbnailView = iv;
                } else {
                    iv.setVisibility(View.GONE);
                    thumbnailView = null;
                }
            } else {
                thumbnailView = null;
            }

            radioButton = (RadioButton) itemView.findViewById(R.id.radio);

            final View thumbnailLayout = null;
            //itemView.findViewById(R.id.thumbnail_layout);
            if (thumbnailLayout != null) {
                addAnimateView(thumbnailLayout);
            }
            final View textLayout = null;
            //itemView.findViewById(R.id.text_layout);
            if (textLayout != null) {
                addAnimateView(textLayout);
            } else {
                if (textView1 != null) {
                    addAnimateView(textView1);
                }
            }
        }

        protected void initOnClickListener(final RecyclerCursorAdapter<?> adapter,
                                           final View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position < 0) {
                        Log.w(TAG,
                                adapter.mFragment + " onClick() invalid position: " + position);
                        return;
                    }
                    adapter.mOnItemClickListener
                            .onItemClick(ViewHolder.this.itemView, position, getItemId());
                }
            });
        }

        protected void initOnLongClickListener(final RecyclerCursorAdapter<?> adapter,
                                               final View itemView) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position < 0) {
                        Log.w(TAG,
                                adapter.mFragment + " onLongClick() invalid position: " + position);
                        return true;
                    }
                    return adapter.mOnItemLongClickListener
                            .onItemLongClick(itemView, position, getItemId());
                }
            });
        }

        protected void addAnimateView(View view) {
            animateViews.add(view);
            animateViewLayers.add(view.getLayerType());
        }
    }

    protected static abstract class AbsBuilder<T extends AbsBuilder<T>> {
        protected static final boolean DEBUG_BUILDER = false;

        protected final Fragment mFragment;

        private final Context mContext;

        private String mText1Col = null;

        private String mText2Col = null;

        private String mText3Col = null;

        private String mThumbnailId = null;

        private String mThumbnailFullUriCol = null;

        private String mKeywordCol = null;

        private Uri mThumbnailUri;

        private final SparseArray<Uri> mThumbnailUriSet = new SparseArray<>();

        @ColorRes
        private int mText1ColorResId = 0;
        //R.color.blur_text;

        @ColorRes
        private int mText2ColorResId = 0;
        //R.color.blur_text;

        @ColorRes
        private int mText3ColorResId = 0;
        //R.color.blur_text;

        @DimenRes
        private int mThumbnailSizeResId = 0;
        //R.dimen.bitmap_size_middle;

        protected abstract T self();

        public AbsBuilder(Fragment fragment) {
            mFragment = fragment;
            mContext = fragment.getActivity().getApplicationContext();
        }

        public T setText1Col(String column) {
            mText1Col = column;
            return self();
        }

        public T setText2Col(String column) {
            mText2Col = column;
            return self();
        }

        public T setText3Col(String column) {
            mText3Col = column;
            return self();
        }

        public T setKeywordCol(String column) {
            mKeywordCol = column;
            return self();
        }

        public T setThumbnailKey(String column) {
            mThumbnailId = column;
            return self();
        }

        public T setThumbnailKey(String column, Uri thumbnailUri) {
            mThumbnailId = column;
            mThumbnailUri = thumbnailUri;
            return self();
        }

        public T setThumbnailFullUriCol(String column) {
            mThumbnailFullUriCol = column;
            return self();
        }

        public T setText1Color(@ColorRes int resId) {
            mText1ColorResId = resId;
            return self();
        }

        public T setText2Color(@ColorRes int resId) {
            mText2ColorResId = resId;
            return self();
        }

        public T setText3Color(@ColorRes int resId) {
            mText3ColorResId = resId;
            return self();
        }

        public T addThumbnailUri(int cpAttrs, Uri uri) {
            mThumbnailUriSet.put(cpAttrs, uri);
            return self();
        }

        public T setThumbnailSize(@DimenRes int size) {
            mThumbnailSizeResId = size;
            return self();
        }

        public RecyclerCursorAdapter build() {
            return null;
        }
    }
}
