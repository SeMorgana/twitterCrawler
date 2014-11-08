/*For now, this only gets lang, expanded_url, favorite_count of a tweet(not retweet)
 *
 *@date created 6/24/2013
 *@last changed
 *
 *@author morgan
 *
 */

import java.util.*;
import java.io.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;

public class Tweets{
    /*******************unused**********************/
    public String contributors;//

    List<Double> geo;//
    public String retweeted;
    public String in_reply_to_screen_name;
    public String possibly_sensitive;//
    public String truncated;//
    public String in_reply_to_status_id_str;
    public String in_reply_to_user_id_str;
    public int retweet_count;
    public String created_at;
    public String place;//

    /***********************************************/

    //a new User class

    public Tweet_User user;//this needs id_str and screen_name
    public String lang;
    public Tweet_Entity entities;	//for now, just urls
    public int favorite_count;
    public String id_str;
    public String text; 

    //read tweet
    public static  Tweets readTweets(JsonReader reader) throws IOException {
        Tweets tw = new Tweets();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("text")) {       //retweet needs this, since including "RT" might truncate the original tweets
                tw.text = reader.nextString();
            } else if (name.equals("id_str")) {
                tw.id_str = reader.nextString();
            } else if (name.equals("entities")) {
                tw.entities = readTweetEntity(reader);
            } else if (name.equals("favorite_count")){
                tw.favorite_count = reader.nextInt();
            } else if (name.equals("user")){
                tw.user = readTweetUser(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return tw;
    }

    //read retweet
    public static  Tweets readRetweets(JsonReader reader) throws IOException {

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("retweeted_status")) {
                return readTweets(reader);
            }  else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return null;		//this can't be executed
    }

    //read entity
    public static Tweet_Entity readTweetEntity(JsonReader reader) throws IOException {
        Tweet_Entity entities = new Tweet_Entity();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("urls")) {
                entities.urls = readTweetURL(reader);
            } else if (name.equals("hashtags")) {
                entities.hashtags = readTweetHashtag(reader);
            } else if (name.equals("user_mentions")) {
                entities.user_mentions = readTweetUserMention(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return entities;
    }

    //read user
    public static Tweet_User readTweetUser(JsonReader reader) throws IOException {
        Tweet_User user = new Tweet_User();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id_str")) {
                user.id_str = reader.nextString();
            } else if(name.equals("screen_name")){
                user.screen_name = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return user;
    }

    //read entities.url
    //if there are more attibute, then use the way readTweetUserMention() use!!!
    public static List<String> readTweetURL(JsonReader reader) throws IOException {
        List<String> urls = new ArrayList<String>();

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext() ){
                String name = reader.nextName();
                if (name.equals("expanded_url") ){
                    urls.add(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();
        return urls;
    }	

    //entities.hashtags	
    //if there are more attibute, then use the way readTweetUserMention() use!!!
    public static List<String> readTweetHashtag(JsonReader reader) throws IOException {
        List<String> hashtags = new ArrayList<String>();

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();
            while (reader.hasNext() ){
                String name = reader.nextName();
                if (name.equals("text") ){
                    hashtags.add(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        }
        reader.endArray();
        return hashtags;
    }

    //entities.user_mentions
    public static List<UserMention> readTweetUserMention(JsonReader reader) throws IOException {
        List<UserMention> user_mentions = new ArrayList<UserMention>();

        reader.beginArray();
        while (reader.hasNext()) {
            reader.beginObject();

            UserMention um = new UserMention();
            while (reader.hasNext() ){
                String name = reader.nextName();
                if (name.equals("name") ){
                    um.name = reader.nextString();
                } else if (name.equals("screen_name") ){
                    um.screen_name = reader.nextString();
                } else if (name.equals("id_str") ){
                    um.id_str = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            user_mentions.add(um);
        }
        reader.endArray();
        return user_mentions;
    }

}

class Tweet_Entity{
    /*****************unused*****************/
    public static List<String> symbols;	//
    /****************************************/	

    public List<String> urls;
    public List<String> hashtags;
    public List<UserMention> user_mentions;

    public Tweet_Entity(){
        //symbols = new ArrayList<String>();
        //hashtags = new ArrayList<String>();					
        //user_mentions = new ArrayList<UserMention>();	//need object
        //urls = new ArrayList<String>();				//dealt in funtion above
    }

}

/*class Tweet_URL{
  public String expanded_url;
  }*/

/*class Hashtag{
  public String text;

  }*/


class UserMention{
    public String name;
    public String screen_name;
    public String id_str;
}

class Tweet_User{
    public String id_str;
    public String screen_name;
}
