package org.liberty.android.fantastischmemo.utils.Connections;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import org.liberty.android.fantastischmemo.common.AMEnv;
import org.liberty.android.fantastischmemo.ui.GameUI.MultiPlayerRegistrationFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by User on 2018-04-18.
 */

public class ClientListener extends Thread {
    Socket socket;
    private Context context;
    ClientListener(Socket soc, Context context) {
        socket = soc;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ObjectInputStream objectInputStream;
                InputStream inputStream = null;
                inputStream = socket.getInputStream();
                objectInputStream = new ObjectInputStream(inputStream);
                Bundle data = new Bundle();
                Object serverObject = (Object) objectInputStream.readObject();

                if(serverObject instanceof String){
                    data.putInt("value",1);
                    data.putSerializable(AMEnv.DATA,(String)serverObject);

                }
                if(serverObject instanceof ArrayList){
                    data.putInt("value",2);
                    data.putSerializable(AMEnv.DATA, (ArrayList<String>) serverObject);
                }
                Message msg = new Message();
                msg.setData(data);

                MultiPlayerRegistrationFragment.handleMsg(msg);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
