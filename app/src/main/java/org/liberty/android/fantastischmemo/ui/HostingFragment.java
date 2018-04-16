package org.liberty.android.fantastischmemo.ui;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.BaseFragment;
import org.liberty.android.fantastischmemo.utils.ServerConnThreads;
import org.liberty.android.fantastischmemo.utils.ServerDataHandler;

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

                    ServerConnThreads startServerThread = new ServerConnThreads();
                    startServerThread.start();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new GameRoomListFragment()).addToBackStack(GameRoomListFragment.class.getName())
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Missing game name or number of players", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
}
