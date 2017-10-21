/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.*;
import javax.json.*;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Ralf Ebert
 */
public class ZWayReader
{

    static final String PROTOCOL = "http://";
    static final String IP = "192.168.178.35:8083";
    static final String API = "ZAutomation/api/v1";
    static final String SERVER = String.format("%s%s/%s/", PROTOCOL, IP, API);
    static final String NAMESPACES = "namespaces";
    static final String ALL_DEVICES = "devices_all";
    static final String USER = "admin";
    static final String PASSWORD = "strongPw";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
        JsonObject aJsonObject = getObjectFromZway(NAMESPACES + "/" + ALL_DEVICES);
        printObjectContent(aJsonObject);
    }

    /**
     * Prints the content of a given JsonObject
     *
     * @param obj a JsonObject
     */
    static void printObjectContent(JsonObject obj)
    {
        JsonArray data = obj.getJsonArray("data");
        
        for (int i = 0; i < data.size(); i++)
        {
            //inside a JsonObject, the value is a JsonValue
            JsonObject jo = data.getJsonObject(i);
            String id = jo.getJsonString("deviceId").getString();
            String name = jo.getJsonString("deviceName").getString();
            System.out.printf("ID: %-33s Name: %-40s complete     %s%n", id,
                    name, jo);
        }
    }

    /**
     * Returns a JsonObject
     *
     * @param path subdirectory of http://yourIP:Port/ZAutomation/api/v1/
     * @return JsonObject from given path
     */
    static JsonObject getObjectFromZway(String path)
    {
        JsonObject result = Json.createObjectBuilder().build();
        String urlString = SERVER + path;
        String auth = USER + ":" + PASSWORD;
        URL url;
        try
        {
            url = new URL(urlString);
        }
        catch (MalformedURLException ex)
        {
            System.out.println("Sorry, the URL " + urlString + " was bad.");

            return result;
        }
        try
        {
            URLConnection con = url.openConnection();
            con.setRequestProperty("Authorization",
                    "Basic " + DatatypeConverter.printBase64Binary(
                            auth.getBytes()));
            InputStream is = con.getInputStream();
            JsonReader rdr = Json.createReader(is);
            //Read the object from the reader
            JsonObject obj = rdr.readObject();

            return obj;
        }
        catch (IOException ex)
        {
            System.out.println("Sorry, that didn't work " + ex);

            return result;
        }
    }
}
