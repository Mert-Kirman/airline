import java.util.HashMap;

public class Airfield {
    public String airfieldName;
    public HashMap<Long, Double> weatherMultipliers;  // Key is time in seconds and value is the weatherMultiplier at that time

    public Airfield(String airfieldName) {
        this.airfieldName = airfieldName;
        this.weatherMultipliers = new HashMap<>();
    }
}
