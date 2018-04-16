package org.liberty.android.fantastischmemo.utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by User on 2018-04-16.
 */

public class ServerSender extends Thread{

    private Socket host;
    Object data;

    public ServerSender(Socket host, Object data){
        this.host = host;
        this.data = data;
    }

    @Override
    public void run(){
        OutputStream out;
        ObjectOutputStream objOut;

        try{
            out = host.getOutputStream();
            objOut = new ObjectOutputStream(out);
            objOut.writeObject(data);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
