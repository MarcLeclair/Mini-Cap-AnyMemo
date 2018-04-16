package org.liberty.android.fantastischmemo.ui;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.liberty.android.fantastischmemo.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by User on 2018-04-15.
 */

public class MultiPlayerRegistrationFragment extends Fragment {

    private static MaterialEditText playerName;
    public MultiPlayerRegistrationFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.join_game_layout, container, false);
        Button hostGame = (Button) v.findViewById(R.id.hostGame);
        Button joinGame = (Button) v.findViewById(R.id.joinGame);
        final WifiManager wifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            final Method method = wifi.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true); //in the case of visibility change in future APIs
            boolean isHotSpotEnabled = (boolean) method.invoke(wifi);
            playerName = (MaterialEditText) v.findViewById(R.id.userName);
            if (isHotSpotEnabled) {
                joinGame.setVisibility(View.GONE);
                hostGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ((playerName.getText().toString().compareTo("")) > 0) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, new HostingFragment()).addToBackStack(HostingFragment.class.getName())
                                    .commit();
                        } else {
                            Toast.makeText(getActivity(), "Check your Username. Please enter a valid value", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return v;
    }
}
