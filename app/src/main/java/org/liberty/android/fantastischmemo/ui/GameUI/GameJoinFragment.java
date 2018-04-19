package org.liberty.android.fantastischmemo.ui.GameUI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import org.liberty.android.fantastischmemo.utils.Connections.ServerConnThreads;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by User on 2018-04-16.
 */

public class GameJoinFragment extends BaseFragment {

    private Activity mActivity;
    private ArrayList<String> dbList;
    private String hostChoice;
    private static String playerName;
    private static TextView gameName;
    private TextView serverChoice;
    private static TextView userName;

    public GameJoinFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
             dbList = bundle.getStringArrayList("listOfDb");
             playerName = bundle.getString("playerName", "");
        }

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.join_game, container, false);
        Button joinGame = (Button) rootView.findViewById(R.id.joinGame);
        gameName = (TextView) rootView.findViewById(R.id.gameName);
        userName = (TextView) rootView.findViewById(R.id.userName);
        userName.setText(playerName);
        serverChoice = (TextView) rootView.findViewById(R.id.hostChoice);
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

    public void selectDb(final ArrayList<String> dbList){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose a database");

                // add a radio button list
                final String[] dbChoice = dbList.toArray(new String[dbList.size()]);
                int checkedItem = 1;
                builder.setSingleChoiceItems(dbChoice, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                // add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ServerConnThreads.sendToAll(dbChoice[which]);
                    }
                });
                builder.setNegativeButton("Cancel", null);

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    public void setGameName(final String lobbyName){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameName.setText(lobbyName);
            }
        });
    }
    public void setHostChoice(final String hostChoice){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverChoice.setText(hostChoice);
            }
        });
    }
    public boolean isGameNameSet(){ return gameName.getText().toString().isEmpty(); }


}


