package org.liberty.android.fantastischmemo.utils;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;

import org.liberty.android.fantastischmemo.entity.Player;

/**
 * Created by User on 2018-04-16.
 */

public class ServerDataHandler extends Handler {

    @Override
    public void handleMessage(Message msg){
        super.handleMessage(msg);
        Bundle data = msg.getData();
        Object clientObj = data.getSerializable("data");

        if(clientObj instanceof Player){
            Player player = (Player) clientObj;
            //TODO ADD TO UI LIST
        }
    }


}
