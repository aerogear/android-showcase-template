package com.aerogear.androidshowcase.features.push;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aerogear.androidshowcase.R;
import com.thesurix.gesturerecycler.GestureAdapter;

public class PushGestureAdapter
        extends GestureAdapter<PushFragment.PushMessage, PushGestureViewHolder> {

    @NonNull
    @Override
    public PushGestureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_push, parent, false);

        return new PushGestureViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PushGestureViewHolder holder, int position) {
        PushFragment.PushMessage pushMessage = getItem(position);

        holder.message.setText(pushMessage.getMessage());
        holder.receivedAt.setText(pushMessage.getDateReceived());
    }

}
