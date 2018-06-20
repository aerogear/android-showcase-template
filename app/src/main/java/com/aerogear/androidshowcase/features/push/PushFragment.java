package com.aerogear.androidshowcase.features.push;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aerogear.androidshowcase.R;
import com.aerogear.androidshowcase.features.push.presenters.PushPresenter;
import com.aerogear.androidshowcase.features.push.views.PushView;
import com.aerogear.androidshowcase.features.push.views.PushViewImpl;
import com.aerogear.androidshowcase.handler.NotificationBarMessageHandler;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;
import com.thesurix.gesturerecycler.GestureAdapter;
import com.thesurix.gesturerecycler.GestureManager;

import org.aerogear.mobile.push.MessageHandler;
import org.aerogear.mobile.push.PushService;
import org.aerogear.mobile.push.UnifiedPushMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;

public class PushFragment extends BaseFragment implements MessageHandler {

    @Inject
    PushPresenter pushPresenter;

    @Inject
    Context context;

    @BindView(R.id.messages)
    RecyclerView messageList;

    public static final String TAG = "Push";

    private PushGestureAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_push, container, false);

        ButterKnife.bind(this, view);

        adapter = new PushGestureAdapter();
        adapter.setDataChangeListener(new GestureAdapter.OnDataChangeListener<PushMessage>() {
            @Override
            public void onItemRemoved(final PushMessage item, final int position) {
                Snackbar snackbar = Snackbar.make(messageList, R.string.push_deleted, Snackbar.LENGTH_SHORT);

                snackbar.setAction(R.string.push_undo, view1 -> adapter.undoLast());
                snackbar.setActionTextColor(ContextCompat.getColor(getActivity(), R.color.white));

                TextView tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

                snackbar.show();
            }

            @Override
            public void onItemReorder(final PushMessage item, final int fromPos, final int toPos) {
            }
        });

        messageList.setHasFixedSize(true);
        messageList.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageList.setAdapter(adapter);

        new GestureManager.Builder(messageList)
                .setSwipeEnabled(true)
                .setSwipeFlags(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
                .build();

        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.pushPresenter = null;
    }

    @Override
    protected PushPresenter initPresenter() {
        return pushPresenter;
    }

    @Override
    protected PushView initView() {
        return new PushViewImpl(this) {
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        PushService.registerMainThreadHandler(this);
        PushService.unregisterBackgroundThreadHandler(NotificationBarMessageHandler.getInstance());
    }

    @Override
    public void onStop() {
        super.onStop();
        PushService.unregisterMainThreadHandler(this);
        PushService.registerBackgroundThreadHandler(NotificationBarMessageHandler.getInstance());
    }

    @Override
    public void onMessage(Context context, Map<String, String> message) {
        adapter.add(new PushMessage(message.get(UnifiedPushMessage.MESSAGE), new Date()));
    }

    public static class PushMessage {

        private static final SimpleDateFormat DATE_FORMATTER =
                new SimpleDateFormat("E MMM d y", Locale.US);

        private final String message;
        private final String dateReceived;

        public PushMessage(String message, Date dateReceived) {
            this.message = message;
            this.dateReceived = DATE_FORMATTER.format(dateReceived);
        }

        public String getMessage() {
            return message;
        }

        public String getDateReceived() {
            return dateReceived;
        }

    }

}