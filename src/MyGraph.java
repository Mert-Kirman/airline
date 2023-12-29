import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Implementation of a weighted graph
public class MyGraph {
    // Store all airport nodes
    public HashMap<String, Airport> allAirports;

    // Store airfield objects to access weatherCode at an airfield at a specific time
    public HashMap<String, Airfield> airfields;

    // Name and cost of the sequence with the least cost in task 2
    public LinkedList<Airport> bestSequence;
    public double leastCost;
    public boolean solutionFound;

    public MyGraph() {
        this.allAirports = new HashMap<>();
        this.airfields = new HashMap<>();

        this.bestSequence = new LinkedList<>();
        this.leastCost = Double.MAX_VALUE;
        this.solutionFound = false;
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
    public void findShortestPathTask2(Airport airportOrigin, long timeOrigin, Airport airportDestination, long deadline, Plane plane, FileWriter output) throws IOException {
        Airport startingAirport = new Airport(airportOrigin.airportCode, timeOrigin, 0);
        startingAirport.shortestPath.add(startingAirport);

        PriorityQueue<Airport> minHeap = new PriorityQueue<>();
        minHeap.add(startingAirport);

        while(!minHeap.isEmpty()) {
            Airport minDistanceAirport = minHeap.poll();  // Dummy airport object
            if(minDistanceAirport.currentTime > deadline) {
                continue;
            }
            Airport originalAirportObject = this.allAirports.get(minDistanceAirport.airportCode);  // Original airport object

            // If the target airport is reached, check if a better path with less cost is found
            if(minDistanceAirport.airportCode.equals(airportDestination.airportCode)) {
                this.bestSequence = minDistanceAirport.shortestPath;
                this.leastCost = minDistanceAirport.cost;
                this.solutionFound = true;
                break;
            }

            // Add the parking scenario of the current airport into minheap if it does not exceed the deadline
            Airport parkedVertex = new Airport(minDistanceAirport.airportCode, minDistanceAirport.currentTime + 21600, minDistanceAirport.cost + originalAirportObject.parkingCost);
            if(parkedVertex.currentTime < deadline) {
                LinkedList<Airport> shortestPath = new LinkedList<>(minDistanceAirport.shortestPath);
                shortestPath.add(new Airport("PARK", parkedVertex.currentTime, parkedVertex.cost));
                parkedVertex.shortestPath = shortestPath;
                minHeap.add(parkedVertex);
            }

            for(Airport neighborAirport : originalAirportObject.neighborAirports) {

                // Calculate the edge weight connecting nodes minDistanceAirport and neighborAirport
                double distance = Airport.calculateDistance(originalAirportObject.latitude, originalAirportObject.longitude, neighborAirport.latitude, neighborAirport.longitude);
                long flightDuration = plane.getDuration(distance);
                if(minDistanceAirport.currentTime + flightDuration > deadline) {  // This flight exceeds deadline so ignore it
                    continue;
                }
                double departedWeatherMultiplier = this.airfields.get(originalAirportObject.airfieldName).weatherMultipliers.get(minDistanceAirport.currentTime);
                double landingWeatherMultiplier = this.airfields.get(neighborAirport.airfieldName).weatherMultipliers.get(minDistanceAirport.currentTime + flightDuration);
                double edgeCost = Airport.calculateFlightCost(departedWeatherMultiplier, landingWeatherMultiplier, distance);

                // If the neighbor airport can be visited with a less flight cost, update the shortest path and cost
                if(minDistanceAirport.cost + edgeCost < neighborAirport.cost) {
                    // Create a new dummy neighbor airport with updated time and total cost to add into minheap
                    Airport dummyNeighborAirport = new Airport(neighborAirport.airportCode, minDistanceAirport.currentTime + flightDuration, minDistanceAirport.cost + edgeCost);

                    LinkedList<Airport> shortestPath = new LinkedList<>(minDistanceAirport.shortestPath);
                    shortestPath.add(dummyNeighborAirport);
                    dummyNeighborAirport.shortestPath = shortestPath;

                    minHeap.add(dummyNeighborAirport);
                }
            }
        }

        printLeastCostPath(output);
    }

    // Print the path with the least cost if it is found
    private void printLeastCostPath(FileWriter output) throws IOException {
        if(this.solutionFound) {
            for(Airport airport :this.bestSequence) {
                output.write(airport.airportCode + " ");
            }
            output.write(String.format("%.5f", this.leastCost) + "\n");

            // Reset data fields
            this.bestSequence = null;
            this.leastCost = Double.MAX_VALUE;
            this.solutionFound = false;
        }
        else {
            output.write("No possible solution.\n");
        }
    }
}
