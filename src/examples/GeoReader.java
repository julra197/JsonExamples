package examples;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

/**
 * Example code to access Google maps API. See also
 * https://developers.google.com/maps/
 *
 * To run this file, right-click and Run File.
 * @author tm352
 *
 */
public class GeoReader
{
   //This API lives at the ROOT URL below
   static final String ROOT = "https://maps.googleapis.com/maps/api/geocode/json?";

   //To use this google maps API you once needed an API Key. 
   //At the time of writing you don't, so we've made the key an empty string.
   static final String API_KEY = "";

   /**
    * A main method to test the GeoReader example
    * @param args the command line arguments
    * @throws java.io.IOException
    */
   public static void main(String[] args) throws IOException
   {
      //GeoReader.example1();

      //GeoReader.example2("Google headquarter");
      //GeoReader.example3("Google headquarter");
      GeoReader.printInformation("Google headquarter");
   }

   /**
    * Some code to illustrate the steps to retrieve a formatted address using
    * object interface
    */
   static void example1()
   {
      //A place we would like to look up on maps
      String address = "Lusaka";

      JsonObject obj = getJsonFromGoogle(address);

      //Proof we were able to access the API
      System.out.println("We got " + obj);

      //What did we get back? Look at each value-name pair
      Basics.tellMeAbout(obj);

      //We can see that the results are under the name "results"         
      //Next we can get the value for the key "results", which we know is an array.
      //Since we know this, we can use the getJsonArray method
      JsonArray results = obj.getJsonArray("results");

      Basics.tellMeAbout(results);

      if (results.size() > 0)
      {
         //In fact, there is only one object in this array, so we can get it
         //at index 0, and we know it is a JsonObject, so we can use the getJsonObject method
         JsonObject obj2 = results.getJsonObject(0);

         //Let's look more closely at this object, which we got from the array
         Basics.tellMeAbout(obj2); //It has address_components, formatted_address, geometry, place_id, and types

         //We can get the address_components value, which is an array
         JsonArray address_components = obj2.getJsonArray("address_components");

         //More about the address components array
         Basics.tellMeAbout(address_components);

         //We can get other parts of the obj, like the formatted address value
         //which is of type string, so we can use the getJsonString method
         System.out.println("\nFormatted address was ");
         System.out.println(obj2.getJsonString("formatted_address"));
      }
      else
      {
         System.out.println("\nThere were no results, sorry.");
      }
   }

   /**
    * A more general example. We need to be careful about spaces
    *
    * @param address
    */
   static void example2(String address)
   {
      JsonObject obj = getJsonFromGoogle(address.replace(" ", "%20"));

      Basics.tellMeAbout(obj);

      JsonArray jar = obj.getJsonArray("results");

      Basics.tellMeAbout(jar);

      String fa = getFirstAddress(obj);

      System.out.println("First address was " + fa);
   }
   
   static void printInformation(String address)
   {
       JsonObject obj = getJsonFromGoogle(address.replace(" ", "%20"));
       try{
           String info = getLocationInfo(obj);
           System.out.println("This is the most pretinent information for " + address + "\n" + info);
       }
       catch(Exception ex)
       {
           String info = "Sorry, no Information found";
           System.out.println("This is the most pretinent information for " + address + "\n" + info);
       }
       
   }

   /**
    * Given a JsonObject representing a place on a map, get the first entry for
    * its formatted address and return it
    *
    * @param obj a suitable json object
    * @return the formatted address
    */
   static String getFirstAddress(JsonObject obj)
   {
      //The steps to retrieve the formatted address can be chained like this
      String fa = obj
              .getJsonArray("results")
              .getJsonObject(0) //just the first entry, there could be others
              .getJsonString("formatted_address")
              .getString();

      return fa;
   }

   static String getLocationInfo(JsonObject obj)
   {
       JsonArray components = obj.getJsonArray("results").getJsonObject(0).getJsonArray(
               "address_components");
       int size = components.size();
       String info = "";
       for (int i =0; i<size; i++)
       {
           JsonObject component = components.getJsonObject(i);
           JsonArray typeArray = component.getJsonArray("types");
           String type = typeArray.getJsonString(0).getString().replace("_", " ");
           String name = component.getJsonString("long_name").getString();
           info = String.format(info + "%s : %s%n", type, name);
       }
       return info;
   }
   /**
    * Apart from the object API, you can also use a streaming interface
    *
    * @param place
    */
   static void example3(String place)
   {
      try
      {
         URL url2 = new URL(ROOT + "address=" + place.replace(" ", "%20") + "&key=" + API_KEY);
         try (InputStream is = url2.openStream();
                 JsonParser psr = Json.createParser(is))
         {
            while (psr.hasNext())
            {
               Event e = psr.next();
               if (e == Event.KEY_NAME)
               {
                  if (psr.getString().equals("formatted_address"))
                  {
                     psr.next();
                     System.out.println("Result using streaming API is " + psr.getString());
                  }
               }
            }
            System.out.println("---------------");
         }
         catch (IOException ex)
         {
            System.out.println("Oops... " + ex);
         }

      }
      catch (MalformedURLException ex)
      {
         Logger.getLogger(GeoReader.class
                 .getName()).log(Level.SEVERE, null, ex);
      }
   }

   //When we first wrote this code, a key was required to access the API. 
   //We left the code to append the key in here for illustration of how it used to work
   //But the API_KEY is an empty string at the moment.
   static JsonObject getJsonFromGoogle(String address)
   {
      JsonObject res = Basics.emptyObject();

      //The key is appended to the root URL as a parameter, along with the address
      //we want to look for on maps, i.e. the place we want to find on maps       
      String urlString = ROOT + "address=" + address + "&key=" + API_KEY;
      URL url;

      try
      {
         url = new URL(urlString);
      }
      catch (MalformedURLException ex)
      {
         System.out.println("Sorry, the URL " + urlString + " was bad.");

         return res;
      }

      //This is an auto-closing try-catch
      try (InputStream is = url.openStream();
              JsonReader rdr = Json.createReader(is))
      {
         //Read the object from the reader
         JsonObject obj = rdr.readObject();

         return obj;
      }
      catch (IOException ex)
      {
         System.out.println("Sorry, that didn't work " + ex);

         return res;
      }
   }
}
