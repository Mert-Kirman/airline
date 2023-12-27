// Create airport objects which will be used as nodes in "MyGraph"
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Airport implements Comparable<Airport> {
    // Airport features
    public String airportCode;  // A unique identifier for the airport
    public String airfieldName;  // Name of the airfield which the airport belongs
    public double latitude;
    public double longitude;
    public int parkingCost;  // The cost of parking at the airport for 6 hours

    // Adjacency list, the airports where planes go to from this airport. Will be used to find the shortest path.
    public ArrayList<Airport> neighborAirports;

    // Keep note of the shortest path from a source airport to this airport
    public LinkedList<Airport> shortestPath;

    // Cost of this airport node
    public double cost;

    Airport(String airportCode, String airfieldName, double latitude, double longitude, int parkingCost) {
        this.airportCode = airportCode;
        this.airfieldName = airfieldName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingCost = parkingCost;

        this.neighborAirports = new ArrayList<>();
        this.shortestPath = new LinkedList<>();
        this.cost = Double.MAX_VALUE;
    }

    public void updateCost(double cost) {
        this.cost = cost;
    }

    public void add(Airport neighborAirport) {
        this.neighborAirports.add(neighborAirport);
    }

    public int compareTo(Airport otherAirport) {
        if(this.cost > otherAirport.cost)
            return 1;
        else if(this.cost < otherAirport.cost)
            return -1;
        else
            return 0;
    }


    // Static methods for various calculations

    // Calculate the distance between 2 airports in kms using the Haversine Formula
    public static double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        int r = 6371;  // Radius of the Earth in km
        return 2 * r * Math.asin(Math.sqrt(Math.pow(Math.sin(Math.toRadians((latitude2 - latitude1) / 2)), 2) + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * Math.pow(Math.sin(Math.toRadians((longitude2 - longitude1) / 2)), 2)));
    }

    // Calculate the flight cost between 2 airports according to weather multipliers and distance
    public static double calculateFlightCost(double departedWeatherMultiplier, double landingWeatherMultiplier, double distance) {
        return 300 * departedWeatherMultiplier * landingWeatherMultiplier + distance;
    }

    // Calculate weatherMultiplier from a given weatherCode. Represent the effects of weather conditions on the cost of flight from an airport to another one
    public static double calculateWeatherMultiplier(int weatherCode) {
        String weatherCodeString = convertToBinary(weatherCode);
        int[] weatherCodeArray = new int[5];
        int padding = 5 - weatherCodeString.length();
        for(int i = 0; i < weatherCodeString.length(); i++) {
            weatherCodeArray[padding + i] = weatherCodeString.charAt(i) - '0';
        }
        return (weatherCodeArray[0] * 1.05 + (1 - weatherCodeArray[0])) * (weatherCodeArray[1] * 1.05 + (1 - weatherCodeArray[1])) * (weatherCodeArray[2] * 1.10 + (1 - weatherCodeArray[2])) * (weatherCodeArray[3] * 1.15 + (1 - weatherCodeArray[3])) * (weatherCodeArray[4] * 1.20 + (1 - weatherCodeArray[4]));
    }

    // Convert an integer to its binary representation in String format
    private static String convertToBinary(int weatherCode) {
        if(weatherCode == 0) {
            return "";
        }
        return convertToBinary(weatherCode / 2) + (weatherCode % 2);
    }

}
