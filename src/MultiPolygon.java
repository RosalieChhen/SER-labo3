import java.util.List;

public class MultiPolygon extends Geometry {

    List<String> coordinates;

    public MultiPolygon(List<String> coordinates){
        this.coordinates = coordinates;
    }

    public List<String> getCoordinates(){
        return coordinates;
    }
}
