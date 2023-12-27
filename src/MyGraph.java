import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Implementation of a weighted graph
public class MyGraph {
    // Store all airport nodes
    public HashMap<String, Airport> allAirports;

    // Store airfield objects to access weatherCode at an airfield at a specific time
    public HashMap<String, Airfield> airfields;

    public MyGraph() {
        this.allAirports = new HashMap<>();
        this.airfields = new HashMap<>();
    }

    // Find successive possible flight operations from origin to destination. There is no deadline and all flights are happening at TimeOrigin
    public void findShortestPathTask1(Airport airportOrigin, long timeOrigin, Airport airportDestination, FileWriter output) throws IOException {
        HashSet<String> alteredAirports = new HashSet<>();  // Keep track of the airports whose cost were altered

        airportOrigin.cost = 0;
        alteredAirports.add(airportOrigin.airportCode);

        PriorityQueue<Airport> minHeap = new PriorityQueue<>();
        minHeap.add(airportOrigin);

        HashSet<Airport> settledNodes = new HashSet<>();

        while(settledNodes.size() != this.allAirports.size()) {
            if(minHeap.isEmpty()) {
                return;
            }

            Airport minDistanceAirport = minHeap.poll();
            if(settledNodes.contains(minDistanceAirport)) {
                continue;
            }
            settledNodes.add(minDistanceAirport);

            // If the target airport is reached, terminate the method
            if(minDistanceAirport == airportDestination) {
                // Print out the shortest path with its total cost
                minDistanceAirport.shortestPath.add(minDistanceAirport);
                for(Airport airport : minDistanceAirport.shortestPath) {
                    output.write(airport.airportCode + " ");
                }
                output.write(String.format("%.5f", minDistanceAirport.cost) + "\n");

                // Reset the costs of the airports which were altered during the process
                for(String airportCode : alteredAirports) {
                    Airport alteredAirport = this.allAirports.get(airportCode);
                    alteredAirport.cost = Double.MAX_VALUE;
                    alteredAirport.shortestPath.clear();
                }

                return;
            }

            for(Airport neighborAirport : minDistanceAirport.neighborAirports) {
                if(settledNodes.contains(neighborAirport)) {
                    continue;
                }

                // Calculate the edge weight connecting nodes minDistanceAirport and neighborAirport
//                System.out.println(this.airfields.get(minDistanceAirport.airfieldName).weatherMultipliers.size());
                double departedWeatherMultiplier = this.airfields.get(minDistanceAirport.airfieldName).weatherMultipliers.get(timeOrigin);
                double landingWeatherMultiplier = this.airfields.get(neighborAirport.airfieldName).weatherMultipliers.get(timeOrigin);
                double distance = Airport.calculateDistance(minDistanceAirport.latitude, minDistanceAirport.longitude, neighborAirport.latitude, neighborAirport.longitude);
                double edgeCost = Airport.calculateFlightCost(departedWeatherMultiplier, landingWeatherMultiplier, distance);

                // If the neighbor airport can be visited with a less flight cost, update the shortest path and cost
                if(minDistanceAirport.cost + edgeCost < neighborAirport.cost) {
                    neighborAirport.cost = minDistanceAirport.cost + edgeCost;
                    alteredAirports.add(neighborAirport.airportCode);

                    LinkedList<Airport> shortestPath = new LinkedList<>(minDistanceAirport.shortestPath);
                    shortestPath.add(minDistanceAirport);
                    neighborAirport.shortestPath = shortestPath;
                }

                minHeap.add(neighborAirport);
            }
        }
    }

    // Find a sequence of successive possible flight and park operations starting from the airportOrigin at timeOrigin to reach the airportDestination before the deadline with the minimum total cost
    public void findShortestPathTask2(Airport airportOrigin, long timeOrigin, Airport airportDestination, long deadline, FileWriter output) {}
}
