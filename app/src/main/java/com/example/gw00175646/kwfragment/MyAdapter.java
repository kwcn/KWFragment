package com.example.gw00175646.kwfragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gw00175646.kwfragment.fragment.RecyclerCursorAdapter;

public class MyAdapter extends RecyclerCursorAdapter<MyAdapter.ViewHolder> {

    public MyAdapter(AbsBuilder<?> builder) {
        super(builder);
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, @Nullable View
            itemView) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout,
                parent, false);
        return new ViewHolder(this, view, viewType);
    }

    public static class ViewHolder extends RecyclerCursorAdapter.ViewHolder {

        public ViewHolder(RecyclerCursorAdapter<?> adapter, View itemView, int viewType) {
            super(adapter, itemView, viewType);
        }
    }

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
