// Plane class for creating the plane object and getting flight duration in hours according to the plane model
public class Plane {
    public String model;

    public Plane(String model) {
        this.model = model;
    }

    // Get the duration of flight according to the plane model in seconds
    public long getDuration(double distance) {
        switch (this.model){
            case "Carreidas 160" -> {
                return getDurationCarreidas(distance);
            }
            case "Orion III" -> {
                return getDurationOrion(distance);
            }
            case "Skyfleet S570" -> {
                return getDurationSkyfleet(distance);
            }
            case "T-16 Skyhopper" -> {
                return getDurationSkyhopper(distance);
            }
        }
        return -1;
    }

    private long getDurationCarreidas(double distance) {
        if(distance <= 175)
            return 6 * 3600;
        else if(distance <= 350)
            return 12 * 3600;
        else
            return 18 * 3600;
    }

    private long getDurationOrion(double distance) {
        if(distance <= 1500)
            return 6 * 3600;
        else if(distance <= 3000)
            return 12 * 3600;
        else
            return 18 * 3600;
    }

    private long getDurationSkyfleet(double distance) {
        if(distance <= 500)
            return 6 * 3600;
        else if(distance <= 1000)
            return 12 * 3600;
        else
            return 18 * 3600;
    }

    private long getDurationSkyhopper(double distance) {
        if(distance <= 2500)
            return 6 * 3600;
        else if(distance <= 5000)
            return 12 * 3600;
        else
            return 18 * 3600;
    }
}
