package org.liberty.android.fantastischmemo.utils;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

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
                    data.putSerializable("test", "testingServerListerner");
                    Log.d("SERVERLISTENER THREAD", host.toString() + " testing");
                    ServerConnThreads.userConnected(host, "testing");
                }
                Message msg = new Message();
                msg.setData(data);
                //TODO invoke sendMessage to pass to the handler.

            }catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
