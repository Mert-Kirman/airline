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
    public String bestSequence;
    public double leastCost;

    public MyGraph() {
        this.allAirports = new HashMap<>();
        this.airfields = new HashMap<>();

        this.bestSequence = "";
        this.leastCost = Double.MAX_VALUE;
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

    // Take a base scenario and calculate and push all of its park combinations (including no park case) to the scenarios stack
    private void getScenarios(Scenario baseScenario, long deadline, Stack<Scenario> scenarios) {
        String newSequence = baseScenario.sequence + " ";
        int parkCount = 0;
        long tempTime = baseScenario.currentTime;
        while(tempTime < deadline) {
            Scenario newScenario = new Scenario(newSequence, baseScenario.latestAirport, baseScenario.totalCost + parkCount * baseScenario.latestAirport.parkingCost, tempTime);
            scenarios.push(newScenario);
            if(tempTime + 21600 < deadline) {  // If deadline is not reached, add 1 park operation to the scenario sequence
                newSequence += "PARK ";
                parkCount++;
                tempTime += 21600;
            }
            else
                break;
        }
    }

    // Find a sequence of successive possible flight and park operations starting from the airportOrigin at timeOrigin to reach the airportDestination before the deadline with the minimum total cost
    public void findShortestPathTask2(Airport airportOrigin, long timeOrigin, Airport airportDestination, long deadline, FileWriter output) throws IOException {
        // Create starting scenarios
        Stack<Scenario> scenarios = new Stack<>();  // Stack that will hold scenarios of flight - park combination sequences
        Scenario originScenario = new Scenario(airportOrigin.airportCode + " ", airportOrigin, 0, timeOrigin);
        originScenario.settledAirports.add(airportOrigin);
        getScenarios(originScenario, deadline, scenarios);  // Add all possible park combinations of the starting airport node to the scenarios stack

        // Try out all possible scenarios
        while(!scenarios.isEmpty()) {
            Scenario currentScenario = scenarios.pop();
            dijkstraForScenario(currentScenario, airportDestination, deadline);
        }

        // Print out the solution to the mission
        output.write(this.bestSequence + " " + this.leastCost);

        // Reset solution fields
        this.bestSequence = "";
        this.leastCost = Double.MAX_VALUE;
    }

    private void dijkstraForScenario(Scenario currentScenario, Airport airportDestination, long deadline) {
        Airport airportOrigin = currentScenario.latestAirport;
        long timeOrigin = currentScenario.currentTime;

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
}
