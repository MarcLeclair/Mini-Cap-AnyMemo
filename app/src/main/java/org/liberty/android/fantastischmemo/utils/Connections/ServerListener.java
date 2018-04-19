package org.liberty.android.fantastischmemo.utils.Connections;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.liberty.android.fantastischmemo.entity.Player;
import org.liberty.android.fantastischmemo.ui.HostingFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by User on 2018-04-15.
 */

public class ServerListener extends Thread {

    private Socket  host;


    ServerListener(Socket host){
        this.host = host;
    }

    @Override
    public void run(){
        while(true){
            ObjectInputStream objectInputStream;
            try{
                //create input stream to later fetch the information
                InputStream in = null;
                in = host.getInputStream();
                //Use objectinputstream to fetch the informtion from the input. NOTE: can only be used with serialized objects
                objectInputStream = new ObjectInputStream(in);
                Object anyMemoObj;
                //Create a bundle to put the data in to be able to communicate with the UI thread
                Bundle data = new Bundle();
                anyMemoObj = objectInputStream.readObject();
                if(anyMemoObj !=null){
                    data.putSerializable("PlayerInfo", (Player)anyMemoObj);
                    Log.d("SERVERLISTENER THREAD", host.toString() + " testing");
                    ServerConnThreads.userConnected(host, ((Player) anyMemoObj).getName());
                }
                Message msg = new Message();
                msg.setData(data);
                //TODO invoke sendMessage to pass to the handler.
                HostingFragment.handleMessage(msg);

            }catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
