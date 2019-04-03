package com.example.gw00175646.kwfragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gw00175646.kwfragment.fragment.RecyclerCursorAdapter;


public class MyAdapter extends RecyclerCursorAdapter<RecyclerCursorAdapter.ViewHolder> {

    public MyAdapter(AbsBuilder<?> builder) {
        super(builder);
    }

    // 设置item布局，可以根据viewType区别设置多种布局
    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, @Nullable View
            itemView) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout,
                parent, false);
        return new ViewHolder(this, view, viewType);
    }

    // adapter构造器，设置数据库数据和控件绑定
    public static class Builder extends AbsBuilder<Builder> {

        public Builder(Fragment fragment) {
            super(fragment);
        }

        @Override
        public MyAdapter build() {
            return new MyAdapter(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
