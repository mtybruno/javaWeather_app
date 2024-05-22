//Retrive weather data from the API - backend logic to fetch the latest weather data
//from external API and return it.The GUI will display the data to the user.

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class weatherApp {
    //fetch data for given location
    @SuppressWarnings("unchecked")
    public static  JSONObject getWeatherData(String locationName) {
        //get location coordinates from geolocation API
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitued data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //Build Api request url with coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
            "latitude=" + latitude + "&longitude=" + longitude +
            "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FSao_Paulo";

        try {

            //call Api and gent response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            //200 for a OK response and sucessful connection
            if(conn.getResponseCode() != 200) {
                System.out.println("Error: could not connect to API");
                return null;
            }

            //store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                    //read and store into string builder
                resultJson.append(scanner.nextLine());
            }
                    //close scanner
                scanner.close();

                    //close url connection
                conn.disconnect();

                    //parse through data
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                    // retrieve hourly data
                JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

                    // hourly property to get current hour data
                JSONArray time = (JSONArray) hourly.get("time");
                int index = findIndexOfCurrentTime(time);

                    //get temperature
                JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                double temperature = (double) temperatureData.get(index);

                    //get weather code
                JSONArray weather_code = (JSONArray) hourly.get("weather_code");
                String weatherCondition = convertWeatherCode ((long) weather_code.get(index));

                    //get humidity
                JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
                long humidity = (long) relativeHumidity.get(index);

                    //get windspeed
                JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
                double windspeed = (double) windspeedData.get(index);

                // build the weather json data object that is going to be accessed in the frontend
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windspeed);

            return weatherData;
        

        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    //retrive geolocation coordinates for given location name
    public static JSONArray getLocationData(String locationName) {
        //replace white spaces in locations name with "+" to adhere to Api's format
        locationName = locationName.replaceAll(" ", "+");

        //build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="+
        locationName + "&count=10&language=en&format=json";

        try {
            //call the api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            //200 means sucessful connection
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                //store API result
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store resulting json data in the string builder
                while(scanner.hasNext() ) {
                    resultJson.append(scanner.nextLine());
                }
                //close scanner
                scanner.close();

                //close url connection
                conn.disconnect();

                //parse the json string intoa json obj
                JSONParser parser = new JSONParser();
                JSONObject resulJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get a list of locations data the API generated from the location named
                JSONArray locationData = (JSONArray) resulJsonObject.get("results");
                return locationData;

            }

        }catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try{
            //attempt to create a connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to our API
            conn.connect();
            return conn;

        }catch(IOException e){
            e.printStackTrace();
            
        }
            //could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //iterate through the time list and see which one matches our current time 
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // return the index
                return i;
            }
        } 
    
        return 0;
    }

    public static String getCurrentTime(){
        // get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // format date to be 2023-09-02T00:00 (this is how it is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }
        //convert weathercode to something readable
    private static String convertWeatherCode(long weather_code) {
        String weatherCondition = "";
            if(weather_code == 0L){
            // clear
            weatherCondition = "Clear";
            }else if(weather_code > 0L && weather_code <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
            }else if((weather_code >= 51L && weather_code <= 67L)
                    || (weather_code >= 80L && weather_code <= 99L)){
            // rain
            weatherCondition = "Rain";
            }else if(weather_code >= 71L && weather_code <= 77L){
            // snow
            weatherCondition = "Snow";
    
        }
            return weatherCondition;    
    }   
}
