package org.liberty.android.fantastischmemo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.liberty.android.fantastischmemo.entity.Player;
import org.liberty.android.fantastischmemo.ui.GameRoomListFragment;

/**
 * Created by User on 2018-04-16.
 */

public class ServerDataHandler extends Handler {

    private AppCompatActivity mActivity;
    public ServerDataHandler(AppCompatActivity mActivity){
        this.mActivity = mActivity;
    }
    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        Bundle data = msg.getData();
        Object clientObj = data.getSerializable("PlayerInfo");

        if(clientObj instanceof Player){

            Player player = (Player) clientObj;
            GameRoomListFragment gameRoom = (GameRoomListFragment)mActivity.getSupportFragmentManager().findFragmentByTag("gameRoom");
            gameRoom.addDevice(player.getName());
            //TODO ADD TO UI LIST
        }
    }


}
