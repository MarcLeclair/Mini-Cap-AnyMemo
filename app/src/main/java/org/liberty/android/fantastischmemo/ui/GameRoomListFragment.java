package org.liberty.android.fantastischmemo.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.liberty.android.fantastischmemo.R;
import org.liberty.android.fantastischmemo.common.BaseFragment;
import org.liberty.android.fantastischmemo.ui.helper.SelectableAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2018-04-16.
 */

public class GameRoomListFragment extends BaseFragment {


    public static ArrayList<String> deviceConnectedList = new ArrayList();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.game_room, container, false);
        Button gameSettings = (Button) v.findViewById(R.id.gameSettings);
        Button playGame = (Button) v.findViewById(R.id.playGame);


        RecyclerView listOfPlayer = (RecyclerView) v.findViewById(R.id.gameList);
        PlayerListAdapter adapter = new PlayerListAdapter(deviceConnectedList);
        listOfPlayer.setAdapter(adapter);

        return v;
    }

    /**
     *  The recent list adapter to handle the actual recycler view logic
     */
    private static class PlayerListAdapter extends SelectableAdapter<PlayerListAdapter.ViewHolder> {


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

            return new PlayerListAdapter.ViewHolder(v);
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

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

    }


}
