package com.aerogear.androidshowcase.navigation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.aerogear.androidshowcase.R;

/**
 * This fragment displays the "Feature not available" dialog.
 */
public class NotAvailableDialogFragment extends DialogFragment {

    private static final String SERVICE_TYPE_KEY = "serviceType";


    private String serviceType;
    private GotoDocsListener gotoDocsCallback;

    public NotAvailableDialogFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param serviceType serviceType name to lookup.
     * @return A new instance of fragment NotAvailableDialogFragment.
     */
    public static NotAvailableDialogFragment newInstance(String serviceType) {
        NotAvailableDialogFragment fragment = new NotAvailableDialogFragment();
        Bundle args = new Bundle();
        args.putString(SERVICE_TYPE_KEY, serviceType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.serviceType = getArguments().getString(SERVICE_TYPE_KEY);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setCustomTitle(inflater.inflate(R.layout.fragment_not_available_title, null));
        builder.setMessage(String.format("The service %s does not have a configuration in mobile-services.json.  Refer to the documentation for instructions on how to configure this service.", serviceType))
                .setPositiveButton(R.string.show_documentation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (gotoDocsCallback != null) {
                            gotoDocsCallback.goToDocs();
                        } else {
                            dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setGotoDocsCallback(GotoDocsListener gotoDocsCallback) {
        this.gotoDocsCallback = gotoDocsCallback;
    }
}
