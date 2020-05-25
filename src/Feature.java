import java.util.List;

public class Feature {

    private List<String> properties;
    private Geometry geometry;
    private String name;

    public Feature(List<String> properties, Geometry geometry){
        this.properties = properties;
        this.geometry = geometry;
        this.name = properties.get(0); //....TO FIX
    }

    public List<String> getProperties(){
        return properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getName(){
        return name;
    }
}
