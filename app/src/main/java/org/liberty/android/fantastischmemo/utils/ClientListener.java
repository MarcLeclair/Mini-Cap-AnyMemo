package org.liberty.android.fantastischmemo.utils;

import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by User on 2018-04-18.
 */

public class ClientListener extends Thread {
    Socket socket;

    ClientListener(Socket soc) {
        socket = soc;
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
              //TODO HANDLE MSG
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
