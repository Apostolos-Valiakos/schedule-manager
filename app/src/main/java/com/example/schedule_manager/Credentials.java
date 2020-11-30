package com.example.schedule_manager;

import android.content.Context;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Credentials {
    private int eid;
    private boolean admin;
    private String username;
    private String password;

    public Credentials(int eid, boolean admin, String username, String password) {
        this.eid = eid;
        this.admin = admin;
        this.username = username;
        this.password = password;
    }

    public int getEid() {
        return eid;
    }

    public boolean is_admin() {
        return admin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    static public boolean isValid(String username, String password, boolean is_Admin, ArrayList<Credentials> listOfCreds){

        for(Credentials cred : listOfCreds){
            if(cred.getUsername().toString().equals(username.toString()) && cred.getPassword().toString().equals(password.toString()) && cred.is_admin() == is_Admin){

                return true;
            }
        }
        return false;
    }
}
