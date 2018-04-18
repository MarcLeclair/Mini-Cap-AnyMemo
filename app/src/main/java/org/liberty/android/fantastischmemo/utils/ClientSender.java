package org.liberty.android.fantastischmemo.utils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by User on 2018-04-17.
 */

public class ClientSender extends Thread {

    private Socket hostThreadSocket;
    private Object message;
    private static boolean isActive = true;

    public ClientSender(Socket socket, Object message) {
        hostThreadSocket = socket;
        this.message = message;
    }

    @Override
    public void run() {
        OutputStream outputStream;
        ObjectOutputStream objectOutputStream;
        if (hostThreadSocket.isConnected()) {
            try {
                if (isActive) {
                    //if (message instanceof Game && !Constants.isPlayerActive(MainFragment.userName.getText().toString(), (Game) message)) {
                    //    isActive = false;
                    //}
                    outputStream = hostThreadSocket.getOutputStream();
                    objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
