package com.aerogear.androidshowcase.features.push;

import android.support.annotation.Nullable;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.aerogear.androidshowcase.R;
import com.thesurix.gesturerecycler.GestureViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PushGestureViewHolder extends GestureViewHolder {

    @Nullable
    @BindView(R.id.foreground_view)
    View mForegroundView;

    @Nullable
    @BindView(R.id.background_view_stub)
    View mBackgroundView;

    @BindView(R.id.message)
    TextView message;

    @BindView(R.id.receivedAt)
    TextView receivedAt;


    public PushGestureViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    @Override
    public boolean canDrag() {
        return false;
    }

    @Override
    public boolean canSwipe() {
        return true;
    }

    @Override
    public View getForegroundView() {
        return mForegroundView;
    }

    @Nullable
    @Override
    public View getBackgroundView() {
        return mBackgroundView;
    }

}
