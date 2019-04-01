package com.example.gw00175646.kwfragment.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gw00175646.kwfragment.R;

import static com.example.gw00175646.kwfragment.fragment.DefaultConstants.UNDEFINED;

public abstract class RecyclerCursorAdapter<VH extends RecyclerCursorAdapter.ViewHolder> extends
        RecyclerView.Adapter<VH> {
    private static final String TAG = "RecyclerCursorAdapter";

    protected final Fragment mFragment;
    protected final Context mContext;
    private final String mText1Col;
    private final String mText2Col;
    private final String mText3Col;
    private final String mThumbnailCol;
    @ColorRes
    private final int mText1ColorResId;
    @ColorRes
    private final int mText2ColorResId;
    @ColorRes
    private final int mText3ColorResId;
    @DimenRes
    private final int mThumbnailSizeResId;
    protected final RecyclerViewableList mRecyclerViewableList;

    private int mRowIDColumn;
    protected int mText1Index = UNDEFINED;
    protected int mText2Index = UNDEFINED;
    protected int mText3Index = UNDEFINED;
    protected int mThumbnailIndex = UNDEFINED;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private View.OnGenericMotionListener mOnGenericMotionListener;
    boolean mDataValid;
    private Cursor mCursor;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position, long id);
    }

    public RecyclerCursorAdapter(AbsBuilder<?> builder) {
        mFragment = builder.mFragment;
        mRecyclerViewableList =
                mFragment instanceof RecyclerViewableList ? (RecyclerViewableList) mFragment : null;

        mContext = builder.mContext;
        mText1Col = builder.mText1Col;
        mText2Col = builder.mText2Col;
        mText3Col = builder.mText3Col;
        mThumbnailCol = builder.mThumbnailCol;

        mText1ColorResId = builder.mText1ColorResId;
        mText2ColorResId = builder.mText2ColorResId;
        mText3ColorResId = builder.mText3ColorResId;

        mThumbnailSizeResId = builder.mThumbnailSizeResId;

        setHasStableIds(true);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // need add headView
        VH holder = onCreateViewHolder(parent, viewType, null);
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
        onBindViewHolderThumbnailView(holder, position, c);

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
        if (mThumbnailCol != null) {
            mThumbnailIndex = newCursor.getColumnIndexOrThrow(mThumbnailCol);
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
        return oldCursor;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public Cursor getCursor(int position) {
        return getCursorInternal(position, false);
    }

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
        if (holder.thumbnailView != null && mThumbnailIndex != UNDEFINED) {
            String uri = c.getString(mThumbnailIndex);
            // need use 3rd plugin load to imageView;
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

        public ViewHolder(final RecyclerCursorAdapter<?> adapter, final View itemView,
                          int viewType) {
            super(itemView);

            if (viewType > 0 && adapter.mOnItemClickListener != null) {
                initOnClickListener(adapter, itemView);
            }

            if (viewType > 0 && adapter.mOnItemLongClickListener != null) {
                initOnLongClickListener(adapter, itemView);
            }

            Resources res = adapter.mFragment.getResources();

            textView1 = itemView.findViewById(R.id.text1);
            if (textView1 != null) {
                textView1.setTextColor(
                        ResourcesCompat.getColor(res, adapter.mText1ColorResId, null));
            }

            textView2 = itemView.findViewById(R.id.text2);
            if (textView2 != null) {
                textView2.setTextColor(
                        ResourcesCompat.getColor(res, adapter.mText2ColorResId, null));
                textView2
                        .setVisibility(adapter.mText2Index != UNDEFINED ? View.VISIBLE : View.GONE);
            }

            textView3 = itemView.findViewById(R.id.text3);
            if (textView3 != null) {
                textView3.setTextColor(
                        ResourcesCompat.getColor(res, adapter.mText3ColorResId, null));
                textView3
                        .setVisibility(adapter.mText3Index != UNDEFINED ? View.VISIBLE : View.GONE);
            }

            ImageView iv = itemView.findViewById(R.id.thumbnail);

            if (iv != null) {
                if (adapter.mThumbnailIndex != UNDEFINED) {
                    iv.setVisibility(View.VISIBLE);
                    thumbnailView = iv;
                } else {
                    iv.setVisibility(View.GONE);
                    thumbnailView = null;
                }
            } else {
                thumbnailView = null;
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
    }

    protected static abstract class AbsBuilder<T extends AbsBuilder<T>> {
        protected final Fragment mFragment;

        private final Context mContext;

        private String mText1Col = null;

        private String mText2Col = null;

        private String mText3Col = null;

        private String mThumbnailCol = null;

        @ColorRes
        private int mText1ColorResId = android.support.v4.R.color
                .secondary_text_default_material_light;

        @ColorRes
        private int mText2ColorResId = android.support.v4.R.color
                .secondary_text_default_material_light;

        @ColorRes
        private int mText3ColorResId = android.support.v4.R.color
                .secondary_text_default_material_light;

        @DimenRes
        private int mThumbnailSizeResId = R.dimen.bitmap_size_middle;

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

        public T setThumbnailCol(String column) {
            mThumbnailCol = column;
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

        public T setThumbnailSize(@DimenRes int size) {
            mThumbnailSizeResId = size;
            return self();
        }

        public abstract RecyclerCursorAdapter build();
    }
}
