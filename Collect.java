/**
 * Interface for collecting data
 *
 * @author morgan
 * @date created: 7/1/2013
 * @last changed:
 *
 */
import java.util.*;
import java.io.*;

public class Collect{
    public static GetUser guser;
    public static GetTweet gtweet;
    public static GetFollowers gfollower;
    public static GetFriends gfriend;

    public static UpdateTweet  utweet;

    public static List<String> al_tokens;
    public static List<String> al_ids;

    /*************data base info***************/
    public static String host_str;
    public static String db_name;
    public static String username;
    public static String password;
    public static String port_str;
    public static List<String> al_tables;

    public static boolean collect_follower = false;
    public static boolean collect_friend = false;
    public static boolean collect_userinfo = false;
    public static boolean collect_tweets_urls = false;

    public static boolean collect_hourly = false;
    //   
    public static boolean update_tweets = false;


    public static void parseCmds(String cmd){
        collect_userinfo = (cmd.contains("u") || cmd.contains("U"));
        collect_tweets_urls = (cmd.contains("t") || cmd.contains("T"));
        collect_friend = (cmd.contains("fr") || cmd.contains("FR"));
        collect_follower = (cmd.contains("fo") || cmd.contains("FO"));
        collect_hourly = (cmd.contains("h") || cmd.contains("H"));
        update_tweets = (cmd.contains("p") || cmd.contains("P"));

    }

    public static void getDatabaseInfo(String filename){
        int counter = 0; //make sure that the info is enough!
        try{
            Scanner sc = new Scanner(new File(filename));
            while(sc.hasNextLine()){
                String s = sc.nextLine().trim();
                if( s.contains("host") ){
                    String[] ss = s.split(":");
                    assert(ss.length == 2);
                    host_str = ss[1].trim();
                    counter++;
                } else if( s.contains("database") ){
                    String[] ss = s.split(":");
                    assert(ss.length == 2);
                    db_name = ss[1].trim();
                    counter++;
                } else if( s.contains("username") ){
                    String[] ss = s.split(":");
                    assert(ss.length == 2);
                    username = ss[1].trim();
                    counter++;
                } else if( s.contains("password") ){
                    String[] ss = s.split(":");
                    assert(ss.length == 2);
                    password = ss[1].trim();
                    counter++;
                } else if( s.contains("port") ){
                    String[] ss = s.split(":");
                    assert(ss.length == 2);
                    port_str = ss[1].trim();
                    counter++;
                } else if( s.contains("tables") ){
                    //            
                    assert(collect_hourly==false);   //this must be false in this case
                    String[] ss = s.split(":");
                    assert(ss.length == 2);
                    al_tables = new ArrayList<String>();
                    Scanner sc2 = new Scanner(ss[1].trim());
                    while(sc2.hasNext()){
                        al_tables.add(sc2.next().trim());
                    }
                    assert(al_tables.size()==3 || al_tables.size()==4);
                    counter++;
                } 
            }	
            assert(counter == 5 || counter == 6);  //contains manually built tables or not
        }catch (Exception e){
            System.err.println("File execepton in Collect:"+ e.getMessage());
            return ;
        }

        //if al_tables is not input, then do is automatically based on time
        if(al_tables==null || al_tables.size()==0){
            al_tables = new ArrayList<String>();
            Calendar c = new GregorianCalendar();
            String year = String.valueOf( c.get(Calendar.YEAR) );
            String month = String.valueOf( c.get(Calendar.MONTH)+1 );
            String day_in_month = String.valueOf( c.get(Calendar.DAY_OF_MONTH) );
            String day_in_week = c.getTime().toString().split(" ")[0];

            al_tables.add(year+"_"+month+"_"+day_in_month+"_"+day_in_week+"_"+"users");
            if(collect_hourly){
                String hour = String.valueOf( c.get(Calendar.HOUR_OF_DAY) );	
                al_tables.add(year+"_"+month+"_"+day_in_month+"_"+day_in_week+"_"+hour+"_"+"tweets");
                al_tables.add(year+"_"+month+"_"+day_in_month+"_"+day_in_week+"_"+hour+"_"+"urls");
            } else {
                al_tables.add(year+"_"+month+"_"+day_in_month+"_"+day_in_week+"_"+"tweets");
                al_tables.add(year+"_"+month+"_"+day_in_month+"_"+day_in_week+"_"+"urls");
            }
        }
    }


    public static void main(String[] args) throws Exception{
        if(args.length != 4 ){System.out.println("database info file,token file,id file and cmds!");return;}		
        parseCmds(args[3]);
        getDatabaseInfo(args[0]);
        al_tokens = Utility.getTokens(args[1]);
        al_ids = Utility.getIds(args[2]);

        List<String> al_ids_backup = new ArrayList<String>();

        //deep copy the list, since the user_info collection removes the ids
        for(String s: al_ids){
            al_ids_backup.add(s);
        }

        String[] db_attrs = {host_str, db_name, username,password,port_str};
        String[] ff_attrs = {"friend_id","follower_id"};

        // for now, build tables here
        System.out.println(collect_follower);
        if(collect_userinfo || collect_tweets_urls || collect_follower || collect_friend){
            DatabaseManager dm = new DatabaseManager(host_str,db_name,username,password,port_str,null,0);
            dm.connect();
            dm.getStatement();
            Utility.buildTables(al_tables,dm);		
        }

        //collcect followers info
        if( collect_follower ){
            if(al_tables.size() != 4){System.out.println("There should be 4 tables in the db!");}
            gfollower.start(al_tokens,al_ids,ff_attrs,db_attrs,2,al_tables);
            System.out.println("followers info finishes!");
            System.out.println("Sleep 10 mins!!\nSuspended at " + (new java.util.Date()));
            System.out.println();
            Thread.sleep(1000 * 6);
        }

        //collcect friends info
        if( collect_friend ){
            if(al_tables.size() != 4){System.out.println("There should be 4 tables in the db!");}
            gfriend.start(al_tokens,al_ids,ff_attrs,db_attrs,2,al_tables);
            System.out.println("friends info finishes!");
            System.out.println("Sleep 10 mins!!\nSuspended at " + (new java.util.Date()));
            System.out.println();
            Thread.sleep(1000 * 600);
        }

        //collcect user_info
        if( collect_userinfo ){
            String[] user_attrs = {"id","name","screen_name","friends_count","followers_count","location","created_at","favourites_count","time_zone","statuses_count","lang","url","description","utc_offset","verified","geo_enabled","protected","contributors_enabled","listed_count","is_translator","profile_image_url","profile_background_image_url","time_collected","account_age"};
            guser.start(al_tokens,al_ids,user_attrs,db_attrs,1, al_tables);
            System.out.println("User info finishes!");
            System.out.println("Sleep 10 mins!!\nSuspended at " + (new java.util.Date()));
            System.out.println();
            Thread.sleep(1000 * 6);
        }

        //collect tweets
        if( collect_tweets_urls ){
            System.out.println("Starting collecting tweets");
            String[] tweet_attrs = {"tweet_id", "text", "time_created","is_reply","is_retweet","original_tweet_id","original_user_id","original_screen_name","retweet_count","favorite_count","user_id","in_reply_to_sid", "in_reply_to_uid","is_truncated","is_possibly_sensitive","geo_location","lang","source","hashtag_count","user_mention_count","url_count","time_collected"};//22
            gtweet.start(al_tokens,al_ids_backup,tweet_attrs,db_attrs,1,al_tables,collect_hourly);
            System.out.println("Tweets collection finishes!");
        }

        //whether or not to update the tweet
        if( update_tweets ){
            System.out.println("Starting to update tweets");
            String[] tweet_attrs = {"tweet_id", "text", "time_created","is_reply","is_retweet","original_tweet_id","original_user_id","original_screen_name","retweet_count","favorite_count","user_id","in_reply_to_sid", "in_reply_to_uid","is_truncated","is_possibly_sensitive","geo_location","lang","source","hashtag_count","user_mention_count","url_count","time_collected"};//22
            //utweet = new UpdateTweet();
            utweet.start(al_tokens,al_ids_backup,tweet_attrs,db_attrs,1,al_tables,collect_hourly);
            System.out.println("Tweets update finishes!");
        } 
    }
}
