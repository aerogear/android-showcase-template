package com.feedhenry.securenativeandroidtemplate.features.network;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feedhenry.securenativeandroidtemplate.R;
import com.feedhenry.securenativeandroidtemplate.features.network.presenters.UploadNotesPresenter;
import com.feedhenry.securenativeandroidtemplate.features.network.views.UploadNotesView;
import com.feedhenry.securenativeandroidtemplate.features.network.views.UploadNotesViewImpl;
import com.feedhenry.securenativeandroidtemplate.mvp.views.BaseFragment;

import java.util.zip.Inflater;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;

/**
 * Created by weili on 27/10/2017.
 */

public class NetworkFragment extends BaseFragment<UploadNotesPresenter, UploadNotesView> {

    public static final String TAG = "network";

    @Inject
    UploadNotesPresenter uploadNotesPresenter;

    View uploadNotesView;

    @BindView(R.id.notes_info)
    TextView notesInfo;

    @BindView(R.id.permission_info)
    TextView permissionInfo;

    @BindView(R.id.uploadNotesBtn)
    Button uploadNotesBtn;

    @BindView(R.id.uploadProgress)
    ProgressBar uploadProgressBar;

    @BindView(R.id.uploadProgressDesc)
    TextView uploadProgressDesc;

    @BindView(R.id.cancelUploadBtn)
    Button cancelUploadBtn;

    private Unbinder unbinder;

    public NetworkFragment() {

    }

    @Override
    public void onAttach(Context context) {
        AndroidInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.uploadNotesPresenter = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        uploadNotesView = inflater.inflate(R.layout.fragment_network, container, false);
        unbinder = ButterKnife.bind(this, uploadNotesView);
        return uploadNotesView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.uploadNotesPresenter.checkClientStatus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    protected UploadNotesPresenter initPresenter() {
        return uploadNotesPresenter;
    }

    @Override
    protected UploadNotesView initView() {
        return new UploadNotesViewImpl(this) {
            private long numberOfNotes = 0;
            private boolean hasUploadPermission = false;

            @Override
            public void renderNotesMessage(long numberOfNotes) {
                this.numberOfNotes = numberOfNotes;
                String message = getString(R.string.notes_info, numberOfNotes);
                if (this.numberOfNotes == 0) {
                    message = getString(R.string.no_notes_info);
                }
                notesInfo.setText(message);
                enableUploadButton();
            }

            @Override
            public void renderPermissionMessage(boolean hasPerm, String requiredRole) {
                this.hasUploadPermission = hasPerm;
                String message = getString(R.string.permission_info, hasPerm? "do":"don't", requiredRole);
                permissionInfo.setText(message);
                enableUploadButton();
            }

            @Override
            public void showProgressBar() {
                uploadProgressBar.setMax(100);
                uploadProgressBar.setVisibility(View.VISIBLE);
                uploadProgressBar.setProgress(0);

                uploadProgressDesc.setVisibility(View.VISIBLE);
                uploadProgressDesc.setText(getString(R.string.upload_prepare));
            }

            @Override
            public void updateProgress(long completed, long total) {
                int progress = (int)(completed/total*100);
                uploadProgressBar.setProgress(progress);
                uploadProgressDesc.setText(getString(R.string.upload_progress, completed, total));
            }

            @Override
            public void updateProgressDesc(String progressDesc) {
                uploadProgressDesc.setText(progressDesc);
            }

            @Override
            public void hideProgressBar() {
                toggleUploadButton(true);
            }

            private void enableUploadButton() {
                if (this.numberOfNotes > 0 && this.hasUploadPermission) {
                    uploadNotesBtn.setEnabled(true);
                }
            }
        };
    }

    @Override
    public int getHelpMessageResourceId() {
        return 0;
    }


    @OnClick(R.id.uploadNotesBtn)
    public void uploadNotes() {
        this.presenter.uploadNotes();
        toggleUploadButton(false);
    }

    @OnClick(R.id.cancelUploadBtn)
    public void cancelUpload() {
        this.presenter.cancelUpload();
        toggleUploadButton(true);
    }

    private void toggleUploadButton(boolean showUpload) {
        if (showUpload) {
            this.uploadNotesBtn.setVisibility(View.VISIBLE);
            this.cancelUploadBtn.setVisibility(View.GONE);
        } else {
            this.uploadNotesBtn.setVisibility(View.GONE);
            this.cancelUploadBtn.setVisibility(View.VISIBLE);
        }
    }
}
