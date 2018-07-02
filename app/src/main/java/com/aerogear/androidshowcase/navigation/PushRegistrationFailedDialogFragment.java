package com.aerogear.androidshowcase.navigation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.aerogear.androidshowcase.R;

public class PushRegistrationFailedDialogFragment extends DialogFragment {

    private static final String REGISTRATION_ERROR_KEY = "serviceType";

    private String registrationErrorMessage;

    public PushRegistrationFailedDialogFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param registrationException The exception thrown during registration.
     * @return A new instance of fragment NotAvailableDialogFragment.
     */
    public static PushRegistrationFailedDialogFragment newInstance(Exception registrationException) {
        PushRegistrationFailedDialogFragment fragment = new PushRegistrationFailedDialogFragment();
        Bundle args = new Bundle();
        args.putString(REGISTRATION_ERROR_KEY, registrationException.getMessage());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.registrationErrorMessage = getArguments().getString(REGISTRATION_ERROR_KEY);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setCustomTitle(inflater.inflate(R.layout.push_registration_failed_title, null));
        builder.setMessage(String.format("The push service failed to register.\n\nDetails: %s", registrationErrorMessage))
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
}
