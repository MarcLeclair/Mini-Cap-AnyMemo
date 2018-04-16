package org.liberty.android.fantastischmemo.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by User on 2018-04-15.
 */

public class ServerConnThreads extends  Thread {

    private static final int serverPort = 8080;
    private static ServerSocket server;
    private static boolean startedServer = false;
    private static HashMap<Socket,String> connectedUserMap = new HashMap();


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

    public static void userConnected(Socket socket, String userName){connectedUserMap.put(socket, userName);}
}
