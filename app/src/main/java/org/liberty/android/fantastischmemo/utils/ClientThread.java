package org.liberty.android.fantastischmemo.utils;

import org.liberty.android.fantastischmemo.entity.Player;
import org.liberty.android.fantastischmemo.utils.WorkOutListUtil;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;


/**
 * Created by User on 2018-04-17.
 */

public class ClientThread extends Thread {

    private Socket soc;
    private String address;
    private int port = 8080;
    private String userName;
    private ArrayList<String> dbList;

    @Inject WorkOutListUtil workOutListUtil;
    public ClientThread(String userName, ArrayList<String> dbList){
        this.userName = userName;
        this.dbList = dbList;
    }

    @Override
    public void run() {
        if (soc == null) {
            try {
                ArrayList<String> deviceList = DevicesConnectedHelper.getDeviceList();
                if (deviceList.size() > 0) {
                    address = deviceList.get(0);
                    if (address != null) {
                        soc = new Socket(address, port);
                        if (soc.isConnected()) {
                            ClientListener clientListener = new ClientListener(soc);
                            clientListener.start();

                            Player playerInfo = new Player(userName, dbList);
                            ClientSender sendUserName = new ClientSender(soc, playerInfo);
                            sendUserName.start();
                        }
                    }
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
