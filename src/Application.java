
import java.util.List;

public class Application {

    public static void main(String[] args) {

        final String GEOJSON_PATHFILE = "resources/countries.geojson";
        final String OUTPUT_PATHFILE = "out/countries.kml";

        List<Feature> featuresCollection = new GEOJSON_Reader().parseFile(GEOJSON_PATHFILE);
        new KML_Writer().writeKMLFile(OUTPUT_PATHFILE, featuresCollection);

    }
}
