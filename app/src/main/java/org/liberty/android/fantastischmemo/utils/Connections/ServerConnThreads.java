package org.liberty.android.fantastischmemo.utils.Connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by User on 2018-04-15.
 */

public class ServerConnThreads extends  Thread {

    private String gameName;
    private static final int serverPort = 8080;
    private static ServerSocket server;
    private static boolean startedServer = false;
    private static HashMap<Socket,String> connectedUserMap = new HashMap();

    public ServerConnThreads(String gameName){this.gameName = gameName;}
    @Override
    public void run(){
        if(server == null) {
            try {
                server = new ServerSocket(serverPort);
                startedServer = true;
                while(true){
                    //let server accept connections ( server as in the phone hosting the room)
                    Socket socket = server.accept();
                    //Create a listener
                    ServerListener listener = new ServerListener(socket);
                    //Start the listener for the server
                    listener.start();
                    ServerSender roomName = new ServerSender(socket, gameName);
                    //start roomname thread to propagate the room name to connected clients
                    roomName.start();
                    //add server to map of connected users
                    userConnected(socket,"server");

                }

                } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void userConnected(Socket socket, String userName){connectedUserMap.put(socket, userName);}
}
