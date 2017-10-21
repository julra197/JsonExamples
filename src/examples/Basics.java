package examples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;

/**
 * This class illustrates how you can build JSON objects in Java, and access
 * their data. JSON processing is supported by classes in the javax.json package
 * as part of Java EE. There are also many third party libraries that you can
 * use, such as google-gson.
 *
 * @author tm352
 */
public class Basics
{

   /**
    * The main method to demonstrate some basics of JSON object construction
    *
    * @param args
    */
   public static void main(String[] args)
   {
      //Make a JSON ojbect representing a car
      JsonObject myObj = Basics.createCarObject();

      System.out.println("Made a json object " + myObj);

      Basics.tellMeAbout(myObj);

      //Write to a file
      String fname = "JSON.txt";
      Basics.persist(myObj, fname);

      //Read it back
      JsonObject read = Basics.read(fname);

      System.out.println("read JSON object " + read);

      //Make an array
      JsonArray myAr = Basics.createExampleArray();

      System.out.println("Made a JSON array " + myAr);

      Basics.tellMeAbout(myAr);
   }

   /**
    * Example code to create a JsonObject. This one represents a car
    *
    * @return the JsonObject we made
    */
   static JsonObject createCarObject()
   {
      System.out.println("\nMaking a car object\n");

      //A JSON object can be created using a builder object
      JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();

      /*
       * Name-value pairs can be added to the object using add methods.
       * The names are strings, and the values are numbers, strings, booleans,
       * null, or objects or arrays
       */
      //E.g.
      jsonObjBuilder.add("Make", "Ford");
      jsonObjBuilder.add("Model", "Focus");
      jsonObjBuilder.add("Engine size", 2);

      //Next we need to create a JSON object from the builder, using its build method:
      JsonObject jsonObj = jsonObjBuilder.build();

      return jsonObj;
   }

   /**
    * Example code to create an array, showing examples of the types that can be
    * stored in an array
    *
    * @return the created JsonArray
    */
   static JsonArray createExampleArray()
   {
      System.out.println("\nMaking an array\n");

      //You can use a JSON array builder to make a JSON array
      JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

      //Here is one way to add items to a JsonArray
      //By chaining add() method calls one after another
      arrayBuilder.add(1.2).add(createCarObject()).add("A string").add(true).add(101);

      //You can also add values one at a time like this:
      arrayBuilder.add("Another string"); //add a string      
      arrayBuilder.add(false); //Add a boolean
      arrayBuilder.addNull(); //add a null value

      //Finally build the array and store a reference to it
      JsonArray ar = arrayBuilder.build();

      //we return this reference so we can see it outside this method
      return ar;
   }

   /**
    * JsonObjects have many methods to tell you about them. A JSON value is one
    * of the following: an object (JsonObject), an array (JsonArray), a number
    * (JsonNumber), a string (JsonString), true (JsonValue.TRUE), false
    * (JsonValue.FALSE), or null (JsonValue.NULL). These are all immutable.
    *
    * @param obj and object you want to know about
    *
    */
   static void tellMeAbout(JsonObject obj)
   {
      System.out.println("\nAbout your object ......................................");

      //Iterate over the keys (names)      
      for (String name : obj.keySet())
      {
         //inside a JsonObject, the value is a JsonValue
         JsonValue jv = obj.get(name);
         System.out.printf("%-20s is type %-10s with value %s%n", name, jv.getValueType(), jv);
      }
   }

   /**
    * iterate over a JSON array.
    *
    * @param ar the array
    */
   static void tellMeAbout(JsonArray ar)
   {
      System.out.println("\nAbout your array ......................................");

      //Iterate over the values    
      for (int i = 0; i < ar.size(); i++)
      {
         JsonValue jv = (JsonValue) ar.get(i);
         System.out.printf("At index %s is %8s  %-10s%n", i, jv.getValueType(), jv);
      }
   }

   /**
    * JSON Objects can be written to disk, or to other output streams. After
    * running this, check the Files tab for JSON.txt.
    *
    * @param obj the object to write to the output file
    * @param filename the name of the output file
    */
   static void persist(JsonObject obj, String filename)
   {
      try (JsonWriter jsonWriter = Json.createWriter(new FileWriter(filename)))
      {
         jsonWriter.writeObject(obj);

         System.out.println("Object was written to " + filename);
      }
      catch (IOException ex)
      {
         System.out.println("Writing object failed... " + ex);
      }
   }

   /**
    * Read a json object from a file and return it
    *
    * @param filename the file to read from
    */
   static JsonObject read(String filename)
   {
      System.out.println("\nReading object from file " + filename);
      try
      {
         JsonReader jsonReader = Json.createReader(new FileReader(filename));

         JsonObject obj = jsonReader.readObject();

         System.out.println("Your object was read from file " + filename);

         return obj;
      }
      catch (FileNotFoundException ex)
      {
         System.out.println("Couldn't find file " + ex);

         return null;
      }
   }

   static JsonObject emptyObject()
   {
      return Json.createObjectBuilder().build();
   }
}
