package org.liberty.android.fantastischmemo.utils;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by User on 2018-04-15.
 */

public class ServerConnThreads extends  Thread {

    private static final int serverPort = 8080;
    private static ServerSocket server;
    private static boolean startedServer = false;


    @Override
    public void run(){
        if(server == null) {
            try {
                server = new ServerSocket(serverPort);
                startedServer = true;

                } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
