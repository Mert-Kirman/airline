// Plane class for creating the plane object and getting flight duration in hours according to the plane model
public class Plane {
    public String model;

    public Plane(String model) {
        this.model = model;
    }

    // Get the duration of flight according to the plane model
    public int getDuration(int distance) {
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

    private int getDurationCarreidas(int distance) {
        if(distance <= 175)
            return 6;
        else if(distance <= 350)
            return 12;
        else
            return 18;
    }

    private int getDurationOrion(int distance) {
        if(distance <= 1500)
            return 6;
        else if(distance <= 3000)
            return 12;
        else
            return 18;
    }

    private int getDurationSkyfleet(int distance) {
        if(distance <= 500)
            return 6;
        else if(distance <= 1000)
            return 12;
        else
            return 18;
    }

    private int getDurationSkyhopper(int distance) {
        if(distance <= 2500)
            return 6;
        else if(distance <= 5000)
            return 12;
        else
            return 18;
    }
}
