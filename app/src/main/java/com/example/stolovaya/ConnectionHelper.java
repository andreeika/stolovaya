package com.example.stolovaya;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
public class ConnectionHelper {
    Connection con;
    String uname, pass, ip, port, database;
    @SuppressLint("NewApi")
    public Connection connectionclass()
    {
      ip = "192.168.0.180";
       database = "столовая";
       uname = "user2";
       pass = "user123456";
        port = "1433";


//        ip = "192.168.0.102";
//        database = "столовая2";
//        uname = "ya_user";
//        pass = "aboba123";
//        port = "1433"; //андрюх, это тоже не трогай

//       ip = "192.168.0.32";
//        database = "столовая2";
//        uname = "youruser";
//        pass = "password";
//        port = "52446";


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";" + "databasename=" +
                    database + ";user=" + uname + ";password=" + pass + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (Exception ex){
            Log.e("Error ", ex.getMessage());
        }

        return connection;
    }


}