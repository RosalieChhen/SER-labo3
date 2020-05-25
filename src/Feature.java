import java.util.List;
import java.util.Set;

public class Feature {

    private List<String> propertiesKey;
    private List<String> propertiesValues;
    private Geometry geometry;
    private String name;

    public Feature(List<String> propertiesKey, List<String> propertiesValues, Geometry geometry){
        this.propertiesKey = propertiesKey;
        this.propertiesValues = propertiesValues;
        this.geometry = geometry;
        this.name = propertiesValues.get(1); //....TO FIX
    }

    public List<String> getPropertiesKey(){
        return propertiesKey;
    }

    public List<String> getPropertiesValues(){
        return propertiesValues;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getName(){
        return name;
    }
}
