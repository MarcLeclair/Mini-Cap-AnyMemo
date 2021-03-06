package org.liberty.android.fantastischmemo.ui;


import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.BaseFragment;
import org.liberty.android.fantastischmemo.ui.GameUI.GameRoomListFragment;
import org.liberty.android.fantastischmemo.utils.DbBrowser;
import org.liberty.android.fantastischmemo.utils.Connections.ServerConnThreads;
import org.liberty.android.fantastischmemo.utils.Connections.ServerDataHandler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 2018-04-16.
 */

public class HostingFragment extends BaseFragment {

    private static ServerDataHandler serverHandler;
    private static MaterialEditText gameName;
    private static MaterialEditText numberOfPlayers;
    private static int numberPlayers;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        DbBrowser trial = new DbBrowser(getActivity());
        File currentDirectory = new File( "/");
        ArrayList<String> result = trial.browseTo(currentDirectory);

        serverHandler = new ServerDataHandler((AppCompatActivity)getActivity(),result);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_room_creation,container,false);
        Button startGame = (Button) v.findViewById(R.id.startGame);
        gameName = (MaterialEditText) v.findViewById(R.id.gameName);
        numberOfPlayers = (MaterialEditText) v.findViewById(R.id.numberOfPlayers);
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((gameName.getText().toString().compareTo("")) > 0 && numberOfPlayers.getText() != null) {
                    numberPlayers = Integer.valueOf(numberOfPlayers.getText().toString());

                    ServerConnThreads startServerThread = new ServerConnThreads(gameName.getText().toString());
                    startServerThread.start();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new GameRoomListFragment(), "gameRoom").addToBackStack(GameRoomListFragment.class.getName())
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Missing game name or number of players", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
    public static void handleMessage(Message msg){
        if(serverHandler != null){
            serverHandler.handleMessage(msg);
        }
    }

}
