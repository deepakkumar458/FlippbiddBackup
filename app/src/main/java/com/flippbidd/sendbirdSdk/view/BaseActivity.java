package com.flippbidd.sendbirdSdk.view;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;

import com.flippbidd.Model.ConnectionEvent;
import com.flippbidd.sendbirdSdk.widget.WaitingDialog;
import com.flippbidd.utils.DialogUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.sendbird.android.SendBird;
import com.sendbird.syncmanager.SendBirdSyncManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        registerConnectionHandler();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    protected String getConnectionHandlerId() {
        return "CONNECTION_HANDLER_MAIN_ACTIVITY";
    }

    private void registerConnectionHandler() {
        SendBird.addConnectionHandler(getConnectionHandlerId(), new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
                SendBirdSyncManager.getInstance().pauseSync();
            }

            @Override
            public void onReconnectSucceeded() {
                SendBirdSyncManager.getInstance().resumeSync();
            }

            @Override
            public void onReconnectFailed() {
            }
        });
    }

    // Shows or hides the ProgressBar
    protected void showProgressBar(boolean show) {
        if (show) {
            WaitingDialog.show(this);
        } else {
            WaitingDialog.dismiss();
        }
    }

    public String changeDateFormate(String _date) {
        DateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
        String inputDateStr = _date;
        Date date = null;
        try {
            date = inputFormat.parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputDateStr = outputFormat.format(date);
        return outputDateStr;
    }

    @Override
    protected void onPause() {
        super.onPause();

        SendBird.removeConnectionHandler(getConnectionHandlerId());
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConnectionEvent event) {
        if (!event.isConnected() && PreferenceUtils.getConnected()) {
            DialogUtils.showConnectionRetryDialog(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
