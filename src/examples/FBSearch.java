package examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * Example accessing facebook 
 * See also https://www.youtube.com/watch?v=7YcW25PHnAA
 *
 * To run this file, right-click and Run File.
 *
 * @author tm352
 */
public class FBSearch
{
   static URL url;

   /**
    * A main method to run this example
    * @param args
    * @throws MalformedURLException
    */
   public static void main(String[] args) throws MalformedURLException
   {
      //Note, the access_token needs to be replaced with your own access token
      //from https://developers.facebook.com/tools/explorer
      //copy your token inbetween the quotes on the next line
      String access_token = "Insert API Key here";

      //You don't need to change this URL, but there's another example URL you could 
      //use below, to access information about your own facebook account
      url = new URL("https://graph.facebook.com/youtube?access_token=" + access_token);

      //to try a different URL, edit the setURL method and uncomment the next line
      setURL();
      //We open a stream to the URL
      //Here we are using an auto-closing try-catch
      try (InputStream is = url.openStream();
              JsonReader rdr = Json.createReader(is))
      {
         System.out.println("The url we visited was " + url);

         //Read a JsonObject from the JsonReader object, similar to building an object
         JsonObject obj = rdr.readObject();

         //This is a simple (toString) print out of the JSON object we recevied from facebook
         System.out.println("Received " + obj + "\n");

         //Our utility method in Basics can help print out information about this object
         Basics.tellMeAbout(obj);

         //You can also retrieve information for specific keys (names), e.g username
         System.out.println("\nThe " + obj.get("username") + " facebook page");
         System.out.println("Created in " + obj.get("founded"));

         //Another name we can get a value for is "likes"
         System.out.println(obj.get("likes") + " people liked this page");

      }
      catch (IOException ex)
      {
         System.out.println("Failed to connected to " + url);
         System.out.println(ex);

         System.out.println("\nRemember that you need to replace the access_token with your own");
      }
   }

   static void setURL() throws MalformedURLException
   {
      String access_token = "Insert API Key here";

      //This is another example URL for using this API, for your own account
      //This uses the path "/me"
      url = new URL("https://graph.facebook.com/v2.10/me?access_token=" + access_token);
   }

}
