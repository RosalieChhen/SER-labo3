public class Polygon extends Geometry {

    String coordinates;

    public Polygon(String coordinates){
        super();
        this.coordinates = coordinates;
    }

    public String getCoordinates(){
        return coordinates;
    }
}
