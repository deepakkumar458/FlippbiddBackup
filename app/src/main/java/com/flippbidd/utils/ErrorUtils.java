package com.flippbidd.utils;

import android.view.View;
import android.widget.TextView;

import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class ErrorUtils {

    private static final String NO_CONNECTION_ERROR = BaseApplication.getInstance().getString(R.string.error_connection);
    private static final String NO_RESPONSE_TIMEOUT = BaseApplication.getInstance().getString(R.string.error_response_timeout);
    private static final String NO_SERVER_CONNECTION = BaseApplication.getInstance().getString(R.string.no_server_connection);

    private ErrorUtils() {
    }

    public static Snackbar showSnackbar(View view, @StringRes int errorMessageResource, Exception e,
                                        @StringRes int actionLabel, View.OnClickListener clickListener) {
        String error = (e == null) ? "" : e.getMessage();
        boolean noConnection = NO_CONNECTION_ERROR.equals(error);
        boolean timeout = error.startsWith(NO_RESPONSE_TIMEOUT);
        if (noConnection || timeout) {
            return showSnackbar(view, NO_SERVER_CONNECTION, actionLabel, clickListener);
        } else if (errorMessageResource == 0) {
            return showSnackbar(view, error, actionLabel, clickListener);
        } else if (error.equals("")) {
            return showSnackbar(view, errorMessageResource, NO_SERVER_CONNECTION, actionLabel, clickListener);
        } else {
            return showSnackbar(view, errorMessageResource, error, actionLabel, clickListener);
        }
    }

    public static Snackbar showSnackbar(View view, @StringRes int errorMessage, String error,
                                        @StringRes int actionLabel, View.OnClickListener clickListener) {
        String errorMessageString = BaseApplication.getInstance().getString(errorMessage);
        String message = String.format("%s: %s", errorMessageString, error);
        return showSnackbar(view, message, actionLabel, clickListener);
    }

    private static Snackbar showSnackbar(View view, String message,
                                         @StringRes int actionLabel,
                                         View.OnClickListener clickListener) {
        Snackbar snackbar = Snackbar.make(view, message.trim(), Snackbar.LENGTH_INDEFINITE);
        if (clickListener != null) {
            snackbar.setAction(actionLabel, clickListener);
            snackbar.setActionTextColor(ContextCompat.getColor(BaseApplication.getInstance(), R.color.color_blue_qb));
            TextView tv = snackbar.getView().findViewById(R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(BaseApplication.getInstance(), R.color.color_light_blue_qb));
        }
        snackbar.show();
        return snackbar;
    }
}