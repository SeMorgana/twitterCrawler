/**
 * Contain some common functions used in other classes
 *
 * @author Morgan
 * @data created: 7/1/2013
 * @last changed:
 */
import java.util.*;
import java.io.*;
import java.sql.*;

public class Utility{

    public static List<String> getTokens(String filename){
        List<String> al_tokens = new ArrayList<String>();
        try{
            Scanner sc = new Scanner(new File(filename));
            while(sc.hasNextLine()){
                al_tokens.add(sc.nextLine());
            }	
        }catch (Exception e){
            System.err.println("File execepton in Utility:"+ e.getMessage());
            return null;
        }

        return al_tokens;
    }

    public static List<String> getIds(String filename){
        List<String> al_ids = new ArrayList<String>();
        try{
            Scanner sc = new Scanner(new File(filename));
            while(sc.hasNextLine()){
                al_ids.add(sc.nextLine());
            }	
        }catch (Exception e){
            System.err.println("File execepton in Utility:"+ e.getMessage());
            return null;
        }

        return al_ids;
    }

    public static List<String> getTableNames(String raw_table_name){
        List<String> al_tables = new ArrayList<String>();
        try{
            Scanner sc = new Scanner(raw_table_name);
            while(sc.hasNext()){
                al_tables.add(sc.next());
            }	
        }catch (Exception e){
            System.err.println("File execepton in Utility:"+ e.getMessage());
            return null;
        }

        return al_tables;
    }

    /*
     * create table if not exist
     */
    public static void buildTables(List<String> al_tables, DatabaseManager dm){
        List<String> al_queries = new ArrayList<String>();
        al_queries.add("CREATE DATABASE IF NOT EXISTS "+ dm.database +"  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        al_queries.add("USE "+dm.database+";");
        //users:
        //al_queries.add("DROP TABLE IF EXISTS `"+dm.database+"`.`"+al_tables.get(0)+"`;");
        al_queries.add("CREATE TABLE IF NOT EXISTS `"+dm.database+"`.`"+al_tables.get(0)+"` (  `id` char(20) NOT NULL default '',  `name` varchar(255) default NULL,  `screen_name` char(50) default NULL,  `friends_count` char(10) default NULL,  `followers_count` char(10) default NULL,  `location` varchar(150) default NULL,  `created_at` char(50) default NULL,  `favourites_count` char(30) default NULL,  `time_zone` char(50) default NULL,  `statuses_count` char(10) default NULL,  `lang` char(10) default NULL,  `url` TEXT default NULL,  `description` TEXT default NULL,  `utc_offset` char(30) default NULL,  `verified` char(10) default NULL,  `geo_enabled` char(10) default NULL,  `protected` char(10) default NULL,  `contributors_enabled` char(10) default NULL,  `listed_count` char(30) default NULL,  `is_translator` char(10) default NULL,  `profile_image_url` TEXT default NULL,  `profile_background_image_url` TEXT default NULL,  `time_collected` char(40) default NULL,  `account_age` char(40) default NULL,  PRIMARY KEY  (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;");
        //tweets:
        //al_queries.add("DROP TABLE IF EXISTS `"+dm.database+"`.`"+al_tables.get(1)+"`;");
        al_queries.add("CREATE TABLE IF NOT EXISTS `"+dm.database+"`.`"+al_tables.get(1)+"` (  `tweet_id` char(30) NOT NULL default '',  `text` varchar(255) default NULL,  `time_created` char(50) default NULL,  `is_reply` char(10) default NULL,  `is_retweet` char(10) default NULL,    `original_tweet_id` char(30) default NULL,  `original_user_id` char(20) default NULL,  `original_screen_name` char(50) default NULL,  `retweet_count` char(10) default NULL,  `favorite_count` char(10) default NULL,  `user_id` char(20) default NULL,  `in_reply_to_sid` char(30) default NULL,  `in_reply_to_uid` char(20) default NULL,    `is_truncated` char(10) default NULL,  `is_possibly_sensitive` char(10) default NULL,  `geo_location` char(30) default NULL,  `lang` char(10) default NULL,    `source` varchar(255) default NULL,  `hashtag_count` char(10) default NULL,  `user_mention_count` char(10) default NULL,  `url_count` char(10) default NULL,	  `time_collected` char(40) default NULL,  PRIMARY KEY  (`tweet_id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;");
        //urls:
        //al_queries.add("DROP TABLE IF EXISTS `"+dm.database+"`.`"+al_tables.get(2)+"`;");
        al_queries.add("CREATE TABLE IF NOT EXISTS `"+dm.database+"`.`"+al_tables.get(2)+"` (  `tweet_id` char(30) NOT NULL default '',  `url` TEXT NOT NULL) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;");
        //assume the query above is correct..
        System.out.println(al_tables.size());	
        if(al_tables.size() > 3){
            al_queries.add("CREATE TABLE IF NOT EXISTS `"+dm.database+"`.`"+al_tables.get(3)+"` (  `friend_id` char(20) NOT NULL default '',  `follower_id` char(20) NOT NULL default '', PRIMARY KEY  (`friend_id`,`follower_id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;");
        }
        try{
            for(int i=0; i<al_queries.size();i++){
                dm.statement.execute(al_queries.get(i));
            }
        } catch (SQLException sqle){
            System.out.println("sql exception in Utility: " + sqle.getMessage());
            return;
        }
    }
}
