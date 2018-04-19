package org.liberty.android.fantastischmemo.entity;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2018-04-16.
 */

public class Player implements Serializable {

    private String name;
    private ArrayList<String> dbOfPlayer = new ArrayList<>();


    public Player(String name, ArrayList<String> dbOfPlayer){
        this.name = name;
        this.dbOfPlayer =dbOfPlayer;
    }
    public String getName(){return this.name;}

    public ArrayList<String> getDB(){return this.dbOfPlayer;}

}
