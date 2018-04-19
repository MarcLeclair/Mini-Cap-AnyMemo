package org.liberty.android.fantastischmemo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.ui.GameActivity;
import org.liberty.android.fantastischmemo.ui.GameJoinFragment;

/**
 * Created by User on 2018-04-15.
 */

public class ClientHandle extends Handler {

    private Bundle msgInfo;
    private AppCompatActivity mActivity;

    public ClientHandle(AppCompatActivity activity) {
        this.mActivity = activity;
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        msgInfo = msg.getData();
        int value = msgInfo.getInt("one");
        Object clientObject = msgInfo.getSerializable("name");

        if (value == 1) {
            String gameName = (String) clientObject;
            GameJoinFragment fragment = (GameJoinFragment)mActivity.getSupportFragmentManager().findFragmentByTag("joinGame");
            fragment.setGameName(gameName);
        }

    }

    public static void sendToServer(Object gameObject) {
        ClientSender sendGameChange = new ClientSender(ClientSender.getSocket(), gameObject);
        sendGameChange.start();
    }
}

