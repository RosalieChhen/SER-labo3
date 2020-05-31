import org.jdom2.Element;

public class Polygon implements Geometry {

    String coordinates;

    public Polygon(String coordinates){
        this.coordinates = coordinates;
    }

    public Element toKML()
    {
        Element coordinatesAsKML = new Element("coordinates").addContent(coordinates);
        Element linearRing = new Element("LinearRing").addContent(coordinatesAsKML);
        Element outerBoundaryIs = new Element("outerBoundaryIs").addContent(linearRing);
        return new Element("Polygon").addContent(outerBoundaryIs);
    }
}
