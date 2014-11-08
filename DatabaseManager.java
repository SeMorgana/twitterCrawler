/**
 * An database interface
 *
 */
//all the databases can use this as the db manager now..

import java.io.*;
import java.sql.*;
import java.util.*;

public class DatabaseManager{
    public String host;
    public String database ;// "ruben_twitter"; // twitter_businesses
    private String username;
    private String password ;
    private String port;

    public Connection conn;
    public Statement statement;

    public Map<String, String> mdata;			//for database update
    public String[] sattr;
    public int num_primary_keys;	//used in initMap()

    public DatabaseManager(String host, String db, String username, String pw, String port, String[] attributes, int num_primary_keys){
        this.host = host;
        database = db;
        this.username = username;
        password = pw;
        this.port = port;	
        sattr = attributes;
        this.num_primary_keys = num_primary_keys;

    }
    public void	initMap(){
        mdata = new HashMap<String, String>();
        //need to put primary keys at the beginning every time
        for(int i=0;i<num_primary_keys;i++){
            mdata.put(sattr[i], "");

        }

        for(int i=num_primary_keys;i<sattr.length;i++){
            mdata.put(sattr[i], null);
        }
    }

    public void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch(IllegalAccessException iae){
            System.err.println(">> illegal access problem during getting an instance of the driver<<");
            iae.printStackTrace();
        } catch(InstantiationException ie){
            System.err.println(">> instantiation problem during getting an instance of the driver<<");
            ie.printStackTrace();
        } catch(ClassNotFoundException cnfe){
            System.err.println(">> \"class not found\" problem during getting an instance of the driver<<");
            cnfe.printStackTrace();
        }


        try{
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":"+ port + "/"
                    +database,  username ,password);
        } catch(SQLException sqle){
            System.err.println(">> problem during getting the connection <<");
            sqle.printStackTrace();
        }

    }

    public void getStatement(){
        try{
            statement = conn.createStatement();
        } catch(SQLException sqle) {
            System.err.println(">> problem during getting the statement <<");
            sqle.printStackTrace();
        }
    }

    public void execute(String query){
        try{
            statement.execute(query);		
        } catch(SQLException sqle){
            System.err.println(">> problem during the execution <<");
            sqle.printStackTrace();
            System.out.println(query);
        }
    }
}
