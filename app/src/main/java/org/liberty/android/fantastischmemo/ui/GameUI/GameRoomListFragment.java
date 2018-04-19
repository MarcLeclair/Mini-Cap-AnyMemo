package org.liberty.android.fantastischmemo.ui.GameUI;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.logger.Log;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.BaseFragment;
import org.liberty.android.fantastischmemo.ui.helper.SelectableAdapter;
import org.liberty.android.fantastischmemo.utils.Connections.ServerConnThreads;
import org.liberty.android.fantastischmemo.utils.Connections.ServerDataHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2018-04-16.
 */

public class GameRoomListFragment extends BaseFragment {

    private ArrayList<String> dbInCommon  = new ArrayList();
    private static Activity mActivity;
    private static ArrayList<String> deviceConnectedList = new ArrayList();
    private static PlayerListAdapter plAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_room, container, false);
        Button playGame = (Button) v.findViewById(R.id.playGame);


        RecyclerView listOfPlayer = (RecyclerView) v.findViewById(R.id.gameList);
        listOfPlayer.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        plAdapter = new PlayerListAdapter(deviceConnectedList);
        listOfPlayer.setAdapter(plAdapter);
        playGame.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose a database");

                // add a radio button list
                final String[] dbChoice = dbInCommon.toArray(new String[dbInCommon.size()]);
                int checkedItem = 1;
                builder.setSingleChoiceItems(dbChoice, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.util.Log.d("WHICH VALUE", String.valueOf(which));
                    }
                });

                // add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dbChoice.length >0) {
                            if (which == dbChoice.length) {
                                ServerConnThreads.sendToAll(dbChoice[which]);
                            } else
                                ServerConnThreads.sendToAll(dbChoice[which]);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
        return v;
    }

    /**
     *  The recent list adapter to handle the actual recycler view logic
     */
    public void addDbs(ArrayList<String> dbInCommon){this.dbInCommon = dbInCommon;}
    public  static void addDevice(final String userName){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                deviceConnectedList.add(userName);
                plAdapter.notifyItemChanged(deviceConnectedList.size() -1);
            }
        });
    }

    private static class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {


        private ArrayList<String> mDataSet;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView playerName;


            public ViewHolder(View view) {
                super(view);

                playerName = (TextView)view.findViewById(R.id.textView);
            }

            public TextView getTextView() {
                return playerName;
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_of_players, parent, false);

            return new ViewHolder(v);
        }

        public PlayerListAdapter(ArrayList<String> dataSet) {
            mDataSet = dataSet;
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

                    holder.getTextView().setText(mDataSet.get(position));

            }

           /* holder.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dbPath = currentItem.dbPath;
                    DialogFragment df = new OpenActionsFragment();
                    Bundle b = new Bundle();
                    b.putString(OpenActionsFragment.EXTRA_DBPATH, dbPath);
                    df.setArguments(b);
                    df.show(((FragmentActivity) context).getSupportFragmentManager(), "OpenActions");
                }
            });*/
        public synchronized  void setItems(ArrayList<String> devices){
            this.mDataSet.clear();
            this.mDataSet.addAll(devices);

            this.notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }


}
