package software.cache.cli;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Cli client to read from rest server
 */
public class RestCli {


    public static void main(String[] args) throws UnsupportedEncodingException {

        Scanner input = new Scanner(System.in);
        System.out.println("Enter the URL with port below (http://<host>:<port>) :  \n");
        String url = input.nextLine();
       try {
           while (true) {
               System.out.println("Enter option number as below \n");
               System.out.println("1.how many trips a particular cab for given date \n" +
                       "2.find out how many trips each medallion made  \n" +
                       "3.Clear the server cache \n" +
                       "4.Exit \n\n");
               int option = Integer.parseInt(input.nextLine().trim());
               if (option == 5) {
                   System.out.println("Thanks for using Rest cli, bye bye !!");
                   System.exit(0);
               }
               switch (option) {
                   case 1:
                       System.out.println("Enter date in (yyyy-mm-dd) format : \n");
                       String date = input.nextLine();
                       gettripsbydate(date.trim(),url);
                       break;
                   case 2:
                       System.out.println("Enter medallions in comma separated value : \n");
                       String medallions = input.nextLine();
                       gettripsbymed(medallions.trim(),url);
                       break;
                   case 3:
                       clearCache(url);
                       System.out.println("Cleared caches from server");
                       break;
                   default:
                       System.out.println("Entered invalid option");
                       System.exit(0);
               }
           }

       }
       catch(InputMismatchException im){
           System.out.println("Entered invalid format");
           System.exit(-1);
       }


    }

    private static void gettripsbydate(String date,String url) throws UnsupportedEncodingException {

        HttpGet get = new HttpGet(url+"/tripsbyday?pickup_date="+date);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {
            System.out.println("\n");
            String output=EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = new JsonParser().parse(output).getAsJsonObject();
            JsonArray paymentsArray = jsonObject.getAsJsonArray("medallions");
            System.out.println("|Medallion|Trips|");

            for (JsonElement pa : paymentsArray) {
                JsonObject paymentObj = pa.getAsJsonObject();
                String     medallion     = paymentObj.get("medallion").getAsString();
                String     trips = paymentObj.get("trips").getAsString();
                System.out.println("|"+medallion+"|"+trips+"|");
            }
            System.out.println("\n");
        }catch (IOException e){
            System.out.println("error");
        }

    }

    private static void gettripsbymed(String medallions,String url) throws UnsupportedEncodingException {

        HttpGet get = new HttpGet(url+"/tripsbymed?medallions="+medallions);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {
            System.out.println("\n");
            String output=EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = new JsonParser().parse(output).getAsJsonObject();
            JsonArray paymentsArray = jsonObject.getAsJsonArray("medallions");
            System.out.println("|Medallion|Trips|");
            for (JsonElement pa : paymentsArray) {
                JsonObject paymentObj = pa.getAsJsonObject();
                String     medallion     = paymentObj.get("medallion").getAsString();
                String     trips = paymentObj.get("trips").getAsString();
                System.out.println("|"+medallion+"|"+trips+"|");
            }            System.out.println("\n");
        }catch (IOException e){
            System.out.println("error");
        }

    }

    private static void clearCache(String url) throws UnsupportedEncodingException {

        HttpPost clear = new HttpPost(url+"/clear");

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(clear)) {
            System.out.println("\n");
            System.out.println(EntityUtils.toString(response.getEntity()));
            System.out.println("\n");
        }catch (IOException e){
            System.out.println("error");
        }

    }


}
