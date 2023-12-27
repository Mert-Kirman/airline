import java.util.HashSet;

public class Scenario implements Comparable<Scenario> {
    String sequence;  // Flight and park sequence
    Airport latestAirport;  // Last airport flown in the sequence
    double totalCost;  // Total cost of the sequence
    long currentTime;  // Current time in the scenario
    HashSet<Airport> settledAirports;  // Airports in the sequence string are marked as settled in current scenario

    public Scenario(String sequence, Airport latestAirportCode, double cost, long currentTime) {
        this.sequence = sequence;
        this.latestAirport = latestAirportCode;
        this.totalCost = cost;
        this.currentTime = currentTime;
        this.settledAirports = new HashSet<>();
    }

    public int compareTo(Scenario otherScenario) {
        if(this.totalCost > otherScenario.totalCost)
            return 1;
        else if(this.totalCost < otherScenario.totalCost)
            return -1;
        else
            return 0;
    }
}
