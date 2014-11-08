/*Collecting user account info
 *
 *@author: morgan
 *@data created: 5/2/2013
 *@last changed:
 *
 */

import twitter4j.auth.AccessToken;
import twitter4j.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class GetUser{
    public static DatabaseManager dm;
    public static List<String> al_tokens;
    public static List<String> al_ids;

    public static String table_name_user;

    public static boolean writeResponseTo(long user_id, Twitter twitter){
        String[] userErrorInfo = new String[2];
        try{
            User user = twitter.showUser(user_id);

            dm.mdata.put("id", String.valueOf(user.getId()) ); userErrorInfo[0] = String.valueOf(user.getId());
            dm.mdata.put("name", user.getName()); 
            dm.mdata.put("screen_name",user.getScreenName()); userErrorInfo[1] = user.getScreenName();
            dm.mdata.put("friends_count", String.valueOf(user.getFriendsCount()) );
            dm.mdata.put("followers_count", String.valueOf(user.getFollowersCount()) );
            dm.mdata.put("location", user.getLocation());
            dm.mdata.put("created_at", user.getCreatedAt().toString());
            dm.mdata.put("favourites_count", String.valueOf(user.getFavouritesCount()) );
            dm.mdata.put("time_zone", user.getTimeZone());
            dm.mdata.put("statuses_count", String.valueOf(user.getStatusesCount()) );
            dm.mdata.put("lang", user.getLang());
            dm.mdata.put("url", user.getURL());	
            dm.mdata.put("description", user.getDescription());
            dm.mdata.put("utc_offset", String.valueOf(user.getUtcOffset()) );
            dm.mdata.put("verified", String.valueOf(user.isVerified()) );
            dm.mdata.put("geo_enabled", String.valueOf(user.isGeoEnabled()) );
            dm.mdata.put("protected", String.valueOf(user.isProtected()));
            dm.mdata.put("contributors_enabled", String.valueOf(user.isContributorsEnabled()));

            dm.mdata.put("listed_count", String.valueOf(user.getListedCount()));
            dm.mdata.put("is_translator", String.valueOf(user.isTranslator()));
            dm.mdata.put("profile_image_url", user.getBiggerProfileImageURL());
            dm.mdata.put("profile_background_image_url", user.getProfileBackgroundImageURL());
            dm.mdata.put("time_collected", String.valueOf((new java.util.Date()).getTime()) );
            java.util.Date date1 = user.getCreatedAt();
            java.util.Date date2 = new java.util.Date();
            long difference = date2.getTime() - date1.getTime();
            double days = difference / (1000.0*3600*24);
            double years = days / 365.0;
            dm.mdata.put("account_age", String.format("%.2f", years)+"years" );

            if( !dm.mdata.get("id").equals("") ){
                String query = "INSERT DELAYED "+table_name_user+" (";//\'" + dm.mdata.get("id") + "\');";
                String headers = dm.sattr[0];
                String values = "(\'" + dm.mdata.get(dm.sattr[0]) + "\'";

                for( int i=1;i<dm.sattr.length;i++ ){
                    if( dm.mdata.get(dm.sattr[i]) != null && !dm.mdata.get(dm.sattr[i]).equals("") ){//null becomes "" ?
                        headers += ("," + dm.sattr[i]);
                        values += (",\'" +dm.mdata.get(dm.sattr[i]).replace("\\","\\\\").replace("'","\\'") + "\'" );
                    }
                }
                headers += ") ";
                values += ");";
                query = query + headers + " values " + values;
                dm.statement.execute(query);
                //System.out.println(dm.mdata.get(dm.sattr[0]));
            }			

        } catch (SQLException sqle){
            System.err.println("sqlError in GetUser:"+ sqle.getMessage());		
            return true;//including primary key conflicts
        } catch (Exception e){
            String error_str = e.getMessage();
            System.err.println("error in GetUser:"+ error_str);
            System.err.println("user id: " + userErrorInfo[0] + "\tuser screen name: " + userErrorInfo[1]);
            if(error_str.startsWith("404")){return true;}//if no this user returns true
            else if(error_str.startsWith("403")){return true;}//The request is understood, but
            else{return false;}
        }

        try{
            ResultSet rs = dm.statement.executeQuery("select count(*) from "+table_name_user);
            rs.next();
            System.out.println("Now there are :" + rs.getObject(1) + " rows in "+table_name_user);
        } catch (Exception e){
            System.err.println("error in GetUser:"+ e.getMessage());
            return false;
        }
        return true;
    }

    public static void start(List<String> l_tokens, List<String> l_ids, String[] user_attrs, String[] db_attrs, int num_primary_key, List<String> al_tables) throws Exception{
        al_tokens = l_tokens;
        al_ids = l_ids;
        table_name_user = al_tables.get(0); // the first one

        dm = new DatabaseManager(db_attrs[0],db_attrs[1],db_attrs[2],db_attrs[3],db_attrs[4],user_attrs,num_primary_key );		
        dm.connect();
        dm.getStatement();
        dm.initMap();

        //Random rd = new Random();
        Set<Integer> limitReachingTokenIndeces = new HashSet<Integer>();

        //int not_work=0;
        while(al_ids.size()>0){
            System.out.println("Now the size of the remaining ids is: "+al_ids.size());
            dm.initMap();
            //boolean work=false;
            for(int i=0;i<al_tokens.size();i++){			
                try{		
                    Twitter twitter = new TwitterFactory().getInstance();
                    String[] tokens = al_tokens.get(i).split(" ");
                    twitter.setOAuthConsumer(tokens[0], tokens[1]);//tokens[0]:consumer token; toknes[1]:consumer secret
                    AccessToken accessToken = new AccessToken(tokens[2], tokens[3]);
                    twitter.setOAuthAccessToken(accessToken);

                    //check the limit here
                    Map<String,RateLimitStatus> m = twitter.getRateLimitStatus();
                    Set<String> s = m.keySet();
                    RateLimitStatus rls1 = m.get("/statuses/user_timeline");
                    RateLimitStatus rls2 = m.get("/users/show/:id");
                    int user_timeline_remaining = rls1.getRemaining();
                    int users_show_remaining = rls2.getRemaining();	

                    if(user_timeline_remaining < 10 || users_show_remaining < 10){
                        System.out.println("The " + String.valueOf(i+1)+ "th token is reaching limit!!");
                        limitReachingTokenIndeces.add(i);
                        if(limitReachingTokenIndeces.size() == al_tokens.size()){
                            System.out.println("Limit reached! sleep 10 mins!!\nSuspended at " + (new java.util.Date()));
                            Thread.sleep(1000 * 600);
                            limitReachingTokenIndeces.clear();//clear it after suspending
                        } else {//the set doesn't contain all the indices
                            if( i == al_tokens.size()-1 ){
                                i = -1;
                            }
                            continue;
                        }
                    } else {
                        limitReachingTokenIndeces.remove(i);
                    }
                    /**********************checking the limit above****************************/

                    String sid = al_ids.get(0).trim();
                    long id = Long.parseLong(sid);

                    boolean can_remove = writeResponseTo(id, twitter);			
                    //work =  can_remove || work;

                    if(can_remove){System.out.println(al_ids.get(0)+" removing");al_ids.remove(0);}//added or added previously
                    if(al_ids.size() == 0) return;//return if it is done

                } catch (Exception e){
                    System.err.println("start function error in GetUser:"+ e.getMessage());
                    continue;
                }
            }
        }
    }
}
