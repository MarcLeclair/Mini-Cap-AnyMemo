package org.liberty.android.fantastischmemo.utils.Connections;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.liberty.android.fantastischmemo.entity.Player;
import org.liberty.android.fantastischmemo.ui.GameUI.GameRoomListFragment;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by User on 2018-04-16.
 */

public class ServerDataHandler extends Handler {

    private AppCompatActivity mActivity;
    private ArrayList<String> dbOfServer = new ArrayList<>();


    public ServerDataHandler(AppCompatActivity mActivity,ArrayList<String> serverDB){
        this.mActivity = mActivity;
        dbOfServer.addAll(serverDB);
    }
    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        Bundle data = msg.getData();
        Object clientObj = data.getSerializable("PlayerInfo");

        if(clientObj instanceof Player){

            Player player = (Player) clientObj;
            compareDB(((Player) clientObj).getDB());
            dbOfServer.remove(0);
            GameRoomListFragment gameRoom = (GameRoomListFragment)mActivity.getSupportFragmentManager().findFragmentByTag("gameRoom");
            gameRoom.addDevice(player.getName());
            gameRoom.addDbs(dbOfServer);
        }
    }

    private void compareDB(ArrayList<String> clientDB){

        ArrayList<String> resultSet = new ArrayList<>();
        dbOfServer.retainAll(clientDB);
    }


}
