import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static java.util.Collections.reverse;

public class GEOJSON_Reader {

    List<Feature> parseFile(String fileToReadPath) {

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

                // TODO régler ce" "if Polygon, if MultiPolygon..." voir rfc geojson : "GeoJSON supports the following geometry types:
                //   Point, LineString, Polygon, MultiPoint, MultiLineString,
                //   MultiPolygon, and GeometryCollection" dans ce labo, il y a que Polygon et MultiPolygon
                Geometry geometry = null;
                if(typeGeometry.equals("Polygon")){
                    String coordinates = getPolygonCoordinates(coordinatesJson);
                    geometry = new Polygon(coordinates);
                } else if (typeGeometry.equals("MultiPolygon")){
                    List<String> coordinates = getMultiPolygonCoordinates(coordinatesJson);
                    geometry = new MultiPolygon(coordinates);
                }

                // Creation de la feature
                Feature feature = new Feature(properties, geometry);
                featuresCollection.add(feature);
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return featuresCollection;
    }

    private Map<String, String> getProperties(JSONObject feature){

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
        // On récupère un tableau de pairs de coordonées (x,y)
        JSONArray coordinatesGeojson = (JSONArray) polygon.get(0);

        // Pour chaque pair, on récupère les coordonnées x et y qu'on ajoute au résultat
        for (int i = 0; i < coordinatesGeojson.size(); i++) {
            JSONArray coord = (JSONArray) coordinatesGeojson.get(i);
            coordinatesString += getCoordinatesPair(coord);
        }
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
