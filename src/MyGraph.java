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
    public boolean solutionFound;
    public MyGraph() {
        this.allAirports = new HashMap<>();
        this.airfields = new HashMap<>();

        this.bestSequence = "";
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

    // Take a base scenario and calculate and push all of its park combinations (including no park case) to the scenarios stack
    private void getScenarios(Scenario baseScenario, long deadline, Stack<Scenario> scenarios) {
        String newSequence = baseScenario.sequence + " ";
        int parkCount = 0;
        long tempTime = baseScenario.currentTime;
        while(tempTime < deadline) {
            Scenario newScenario = new Scenario(newSequence, baseScenario.latestAirport, baseScenario.totalCost + parkCount * baseScenario.latestAirport.parkingCost, tempTime);
            if(newScenario.totalCost > this.leastCost) {
                break;
            }
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
    public void findShortestPathTask2(Airport airportOrigin, long timeOrigin, Airport airportDestination, long deadline, Plane plane, FileWriter output) throws IOException {
        // Create starting scenarios
        Stack<Scenario> scenarios = new Stack<>();  // Stack that will hold scenarios of flight - park combination sequences
        Scenario originScenario = new Scenario(airportOrigin.airportCode + " ", airportOrigin, 0, timeOrigin);
        originScenario.settledAirports.add(airportOrigin);
        getScenarios(originScenario, deadline, scenarios);  // Add all possible park combinations of the starting airport node to the scenarios stack

        // Try out all possible scenarios
        while(!scenarios.isEmpty()) {
            Scenario currentScenario = scenarios.pop();
            String remainingPartOfPath = "false";
            if(currentScenario.totalCost < this.leastCost) {
                remainingPartOfPath = dijkstraForScenario(currentScenario, airportDestination, deadline, plane, scenarios);
            }

            if(!remainingPartOfPath.equals("false")) {  // A cheaper cost path is discovered
                this.solutionFound = true;
                this.bestSequence = "";
                this.bestSequence += currentScenario.sequence + remainingPartOfPath;
            }
        }

        if(this.solutionFound) {
            // Print out the solution to the mission
            output.write(this.bestSequence + " " + this.leastCost + "\n");

            // Reset solution fields
            this.bestSequence = "";
            this.leastCost = Double.MAX_VALUE;
            this.solutionFound = false;
        }
        else {
            output.write("No possible solution.");
        }
    }

    private String dijkstraForScenario(Scenario currentScenario, Airport airportDestination, long deadline, Plane plane, Stack<Scenario> scenarios) {
        Airport airportOrigin = currentScenario.latestAirport;
        airportOrigin.currentTime = currentScenario.currentTime;

        HashSet<String> alteredAirports = new HashSet<>();  // Keep track of the airports whose cost were altered

        airportOrigin.cost = currentScenario.totalCost;
        alteredAirports.add(airportOrigin.airportCode);

        PriorityQueue<Airport> minHeap = new PriorityQueue<>();
        minHeap.add(airportOrigin);

        HashSet<Airport> settledNodes = currentScenario.settledAirports;

        while(settledNodes.size() != this.allAirports.size()) {
            if(minHeap.isEmpty()) {
                return "false";
            }

            Airport minDistanceAirport = minHeap.poll();
            if(settledNodes.contains(minDistanceAirport)) {
                continue;
            }
            settledNodes.add(minDistanceAirport);

            // Time is up
            if(minDistanceAirport.currentTime > deadline) {
                break;
            }

            // If the target airport is reached, terminate the method
            if(minDistanceAirport == airportDestination) {
                if(minDistanceAirport.cost < this.leastCost) {  // If this solution is even cheaper than the min recorded previously
                    String solutionToAppend = "";
                    // Return the part of the shortest path with no park operations with its total cost
                    minDistanceAirport.shortestPath.add(minDistanceAirport);
                    for(Airport airport : minDistanceAirport.shortestPath) {
                        solutionToAppend += airport.airportCode + " ";
                    }
                    solutionToAppend += String.format("%.5f", minDistanceAirport.cost) + "\n";
                    return solutionToAppend;
                }

                // Reset the costs of the airports which were altered during the process
                for(String airportCode : alteredAirports) {
                    Airport alteredAirport = this.allAirports.get(airportCode);
                    alteredAirport.cost = Double.MAX_VALUE;
                    alteredAirport.shortestPath.clear();
                }

                return "false";
            }


            for(Airport neighborAirport : minDistanceAirport.neighborAirports) {
                if(settledNodes.contains(neighborAirport)) {
                    continue;
                }

                // Calculate the edge weight connecting nodes minDistanceAirport and neighborAirport
                double distance = Airport.calculateDistance(minDistanceAirport.latitude, minDistanceAirport.longitude, neighborAirport.latitude, neighborAirport.longitude);
                long landingTime = minDistanceAirport.currentTime + plane.getDuration(distance);  // If plane flies to this neighbor airport, time will be landingTime
                if(this.airfields.get(minDistanceAirport.airfieldName).weatherMultipliers.get(minDistanceAirport.currentTime) == null || this.airfields.get(neighborAirport.airfieldName).weatherMultipliers.get(landingTime) == null) {
                    continue;
                }
                double departedWeatherMultiplier = this.airfields.get(minDistanceAirport.airfieldName).weatherMultipliers.get(minDistanceAirport.currentTime);
                double landingWeatherMultiplier = this.airfields.get(neighborAirport.airfieldName).weatherMultipliers.get(landingTime);
                double edgeCost = Airport.calculateFlightCost(departedWeatherMultiplier, landingWeatherMultiplier, distance);

                // If the neighbor airport can be visited with a less flight cost, update the shortest path and cost
                if(minDistanceAirport.cost + edgeCost < neighborAirport.cost) {
                    neighborAirport.cost = minDistanceAirport.cost + edgeCost;
                    alteredAirports.add(neighborAirport.airportCode);

                    LinkedList<Airport> shortestPath = new LinkedList<>(minDistanceAirport.shortestPath);
                    if(minDistanceAirport != airportOrigin) {  // Do not add origin airport to solution path as it is already added with all the park operations in findShortestPathTask2()
                        shortestPath.add(minDistanceAirport);
                    }
                    neighborAirport.shortestPath = shortestPath;

                    // The parent Airport node which relaxes this neighbor Airport node will give it its current time
                    neighborAirport.currentTime = landingTime;

                    if(minDistanceAirport == airportOrigin) {  // Will work only for starting airport node
                        Scenario newScenario = new Scenario(currentScenario.sequence + neighborAirport.airportCode + " ", neighborAirport, neighborAirport.cost, landingTime);
                        getScenarios(newScenario, deadline, scenarios);
                    }
                }

                minHeap.add(neighborAirport);
            }
        }

        // A solution is not found

        // Reset the costs of the airports which were altered during the process
        for(String airportCode : alteredAirports) {
            Airport alteredAirport = this.allAirports.get(airportCode);
            alteredAirport.cost = Double.MAX_VALUE;
            alteredAirport.shortestPath.clear();
        }

        return "false";
    }
}
