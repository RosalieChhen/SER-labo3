import org.jdom2.Element;
import java.util.ArrayList;
import java.util.List;

public class MultiPolygon implements Geometry {

    List<Polygon> polygons;

    public MultiPolygon(List<String> coordinates){
        polygons = new ArrayList<Polygon>();
        for(String polygon : coordinates)
        {
            polygons.add(new Polygon(polygon));
        }
    }

    public Element toKML()
    {
        Element multiGeometry = new Element("MultiGeometry");

        for(int j = 0; j < polygons.size(); j++){
            multiGeometry.addContent(polygons.get(j).toKML());
        }

        return multiGeometry;
    }
}
