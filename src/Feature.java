import java.util.*;

public class Feature {

    private Map<String, String> properties;
    private Geometry geometry;
    private String name;

    public Feature(Map<String, String> properties, Geometry geometry){
        this.properties = properties;
        this.geometry = geometry;
        this.name = properties.get("ADMIN");
    }

    public Map<String, String> getProperties(){
        return properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getName(){
        return name;
    }
}
