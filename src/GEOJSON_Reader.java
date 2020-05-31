import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static java.util.Collections.reverse;

class UnexpectedDataException extends RuntimeException{
    UnexpectedDataException(String msg)
    {
        super(msg);
    }
}

public class GEOJSON_Reader {

    List<Feature> parseFile(String fileToReadPath){

        JSONParser jsonParser = new JSONParser();
        List<Feature> featuresCollection = new ArrayList<>();

        try (FileReader reader = new FileReader(fileToReadPath)) {

            Object obj = jsonParser.parse(reader);
            JSONObject featureCollection = (JSONObject) obj;
            JSONArray featuresArray = (JSONArray) featureCollection.get("features"); // get features


            for(int featureIndex = 0; featureIndex < featuresArray.size(); featureIndex++) {

                JSONObject featureJson = (JSONObject) featuresArray.get(featureIndex);

                // Récupération des keys des propriétés d'une feature
                //List<String> propertiesKeys = getPropertiesKeys(featureJson);
                // Récupération des valeurs des propriétés d'une feature
                //List<String> propertiesValues = getPropertiesValues(featureJson);
                Map<String, String> properties = getProperties(featureJson);

                // Récupération du type de geometry d'une feature
                JSONObject geometryJson = (JSONObject) featureJson.get("geometry");
                String typeGeometry = (String) geometryJson.get("type");

                // Récupération des coordonnées d'une geometry d'une feature
                JSONArray coordinatesJson = (JSONArray) geometryJson.get("coordinates");

                Geometry geometry = null;
                try{
                    switch(typeGeometry)
                    {
                        case "Polygon":
                            geometry = new Polygon(getPolygonCoordinates(coordinatesJson));
                            break;
                        case "MultiPolygon":
                            geometry = new MultiPolygon(getMultiPolygonCoordinates(coordinatesJson));
                            break;
                        default :
                            throw new UnexpectedDataException("Unexpected Geometry type");
                    }
                    // Creation de la feature
                    Feature feature = new Feature(properties, geometry);
                    featuresCollection.add(feature);
                }

                catch(UnexpectedDataException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return featuresCollection;
    }

    private Map<String, String> getProperties(JSONObject feature) throws UnexpectedDataException{

        Map<String, String> properties = new HashMap<>();
        JSONObject propertiesGesojon = (JSONObject) feature.get("properties");

        // Récupère les clés des propriétés d'une feature et les mets dans le bon ordre
        Set propertiesKeysSet = propertiesGesojon.keySet();
        List<String> propertiesKeysList = new ArrayList<String>();
        for (Object propertyKey : propertiesKeysSet)
            propertiesKeysList.add((String) propertyKey);
        reverse(propertiesKeysList);

        // Ajoute à la map les clés et les valeurs d'une prorpriétés d'une feature
        for (Object propertyKey : propertiesKeysList)
            properties.put((String) propertyKey,(String) propertiesGesojon.get(propertyKey));

        //On s'assure que les éléments "ADMIN" et "ISO_A3" soient bien présents
        String name = properties.get("ADMIN");
        String id = properties.get("ISO_A3");

        if(id == null)
        {
            throw new UnexpectedDataException("No ISO_A3 (id) property");
        }
        if(name == null)
        {
            throw new UnexpectedDataException("No ADMIN (name) property");
        }
        System.out.println("("+id+") "+name);
        return properties;
    }

    private String getCoordinatesPair(JSONArray pair){

        double x = (Double) pair.get(0);
        double y = (Double) pair.get(1);

        return x + "," + y + " ";
    }

    // donne les coordonées d'un polygon en une string
    private String getPolygonCoordinates(JSONArray polygon){

        String coordinatesString = "";
        // On récupère un tableau de paires de coordonées (x,y)
        JSONArray coordinatesGeojson = (JSONArray) polygon.get(0);

        // Pour chaque paire, on récupère les coordonnées x et y qu'on ajoute au résultat
        for (int i = 0; i < coordinatesGeojson.size(); i++) {
            JSONArray coord = (JSONArray) coordinatesGeojson.get(i);
            coordinatesString += getCoordinatesPair(coord);
        }
        System.out.println("     - "+coordinatesGeojson.size()+" coordinates");
        return coordinatesString;
    }

    // donne les coordonnées d'une liste de polygon en un liste de string
    private  List<String> getMultiPolygonCoordinates(JSONArray multiPolygon){

        List<String> cooridnatesListString = new ArrayList<>();
        // Pour chaque polygon, on réupère les coordonées en une string du polygon qu'on rajoute à la liste
        for(int i = 0; i < multiPolygon.size(); i++){
            cooridnatesListString.add(getPolygonCoordinates((JSONArray) multiPolygon.get(i)));
        }

        return cooridnatesListString;
    }

}
