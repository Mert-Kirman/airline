import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        // Array for storing plane objects
        Plane[] planes = new Plane[4];
        planes[0] = new Plane("Carreidas 160");
        planes[1] = new Plane("Orion III");
        planes[2] = new Plane("Skyfleet S570");
        planes[3] = new Plane("T-16 Skyhopper");

        // Plane that will be used for the current missions
        Plane plane;

        // Store all the airport objects
        MyGraph myGraph = new MyGraph();

        FileWriter task1 = new FileWriter("task1-out.txt", true);
        FileWriter task2 = new FileWriter("task2-out.txt", true);

        // Create and store airport objects
        File file = new File("TR-0-airports.csv");
        Scanner input = new Scanner(file);
        input.nextLine();

        while(input.hasNextLine()) {
            String[] airportInfo = input.nextLine().strip().split(",");
            Airport airport = new Airport(airportInfo[0], airportInfo[1], Double.parseDouble(airportInfo[2]), Double.parseDouble(airportInfo[3]), Integer.parseInt(airportInfo[4]));
            myGraph.allAirports.put(airport.airportCode, airport);
        }
        input.close();

        // Create adjacency lists from the input file which includes info about which airports planes fly to from an airport
        file = new File("TR-0-directions.csv");
        input = new Scanner(file);
        input.nextLine();

        while(input.hasNextLine()) {
            String[] fromTo = input.nextLine().strip().split(",");
            Airport mainAirport = myGraph.allAirports.get(fromTo[0]);
            Airport neighborAirport = myGraph.allAirports.get(fromTo[1]);
            mainAirport.neighborAirports.add(neighborAirport);
        }
        input.close();

        // Create airfield objects, insert weatherMultiplier values at given times to object's hashmap
        file = new File("weather.csv");  // File including weatherCode values of airfields at given times
        input = new Scanner(file);
        input.nextLine();

        while(input.hasNextLine()) {
            String[] timeWeatherCode = input.nextLine().strip().split(",");
            Airfield airfield;
            if(!myGraph.airfields.containsKey(timeWeatherCode[0])) {
                airfield = new Airfield(timeWeatherCode[0]);
                myGraph.airfields.put(airfield.airfieldName, airfield);
            }
            airfield = myGraph.airfields.get(timeWeatherCode[0]);
            airfield.weatherMultipliers.put(Long.parseLong(timeWeatherCode[1]), Airport.calculateWeatherMultiplier(Integer.parseInt(timeWeatherCode[2])));
        }
        input.close();

        // Carry out missions
        file = new File("TR-0-missions.in");  // File containing plane model and mission details
        input = new Scanner(file);
        String planeModel = input.nextLine().strip();
        switch (planeModel) {  // Choose plane model to use in the missions
            case "Carreidas 160" -> plane = planes[0];
            case "Orion III" -> plane = planes[1];
            case "Skyfleet S570" -> plane = planes[2];
            case "T-16 Skyhopper" -> plane = planes[3];
            default -> throw new IllegalStateException("Unexpected value: " + planeModel);
        }

        while(input.hasNextLine()) {
            String[] missionDetails = input.nextLine().strip().split(" ");
            Airport airportOrigin = myGraph.allAirports.get(missionDetails[0]);
            Airport airportDestination = myGraph.allAirports.get(missionDetails[1]);
            Long timeOrigin = Long.parseLong(missionDetails[2]);  // Start hour of the current mission
            Long deadline = Long.parseLong(missionDetails[3]);  // Deadline of the current mission

            myGraph.findShortestPathTask1(airportOrigin, timeOrigin, airportDestination, task1);
            myGraph.findShortestPathTask2(airportOrigin, timeOrigin, airportDestination, deadline, plane, task2);
        }
        input.close();


        task1.close();
        task2.close();
    }
}