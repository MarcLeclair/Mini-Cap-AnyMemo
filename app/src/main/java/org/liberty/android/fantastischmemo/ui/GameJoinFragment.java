package org.liberty.android.fantastischmemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.liberty.android.fantastischmemo.common.BaseFragment;
import org.liberty.android.fantastischmemo.R;
/**
 * Created by User on 2018-04-16.
 */

public class GameJoinFragment extends BaseFragment {

    private static String playerName;
    private static TextView gameName;
    private static TextView userName;

    public GameJoinFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
             playerName = bundle.getString("playerName", "");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.join_game, container, false);
        Button joinGame = (Button) rootView.findViewById(R.id.joinGame);
        gameName = (TextView) rootView.findViewById(R.id.gameName);
        userName = (TextView) rootView.findViewById(R.id.userName);
        userName.setText(playerName);
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        /*joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameobject != null) {
                    GameFragment gameFragment = new GameFragment();
                    gameFragment.setParameters(gameobject, ClientConnectionThread.socket);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, gameFragment).addToBackStack(JoinGameFragment.class.getName())
                            .commit();
                } else {
                    Toast.makeText(getActivity(), "Game setup not complete. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        return rootView;
    }

}


