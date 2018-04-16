package org.liberty.android.fantastischmemo.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2018-04-16.
 */

public class Player implements Serializable {

    private String name;
    private HashSet<String> dbOfPlayer = new HashSet<>();


    public Player(String name, HashSet<String> dbOfPlayer){
        this.name = name;
        this.dbOfPlayer =dbOfPlayer;
    }

}
