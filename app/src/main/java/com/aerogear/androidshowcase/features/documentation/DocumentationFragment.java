package com.aerogear.androidshowcase.features.documentation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.aerogear.androidshowcase.features.documentation.presenters.DocumentationPresenter;
import com.aerogear.androidshowcase.features.documentation.views.DocumentationView;
import com.aerogear.androidshowcase.features.documentation.views.DocumentationViewImpl;
import com.aerogear.androidshowcase.mvp.views.BaseFragment;

import java.net.URL;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class DocumentationFragment extends BaseFragment<DocumentationPresenter, DocumentationView> {

    public static final String TAG = "home";
    private static final String DOC_URL = "DocumentationFragment.DOC_URL";

    @Inject
    DocumentationPresenter documentationPresenter;

    private String url;

    public DocumentationFragment() {
    }


    @Override
    public void onAttach(Activity activity) {
        AndroidInjection.inject(this);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.documentationPresenter = null;
    }

    @Override
    protected DocumentationPresenter initPresenter() {
        return documentationPresenter;
    }

    @Override
    protected DocumentationView initView() {
        return new DocumentationViewImpl(this);
    }

    public static DocumentationFragment newInstance(DocumentUrl url) {
        DocumentationFragment fragment = new DocumentationFragment();

        Bundle args = new Bundle();
        args.putString(DOC_URL, url.getUrl());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.url =  getArguments().getString(DOC_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        WebView webview = new WebView(this.getActivity());

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });

        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(this.url);

        return webview;
    }
}
