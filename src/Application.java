
import org.jdom2.*;
import org.jdom2.output.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Application {

    final static String NORMAL_STYLE = "normalStyle";
    final static String HIGHLIGHT_STYLE = "highlightStyle";
    final static String STYLEMAP = "labo3Style";

    public static void main(String[] args) {

        final String PATH_GEOJSON_FILE = "src/countries.geojson";
        final String OUTPUT_PATHFILE = "src/newKMLfile.kml";



        // Lecture : JSON parser object pour lire le fichier
        JSONParser jsonParser = new JSONParser();

        // si on arrive à lire le fichier...
        try (FileReader reader = new FileReader(PATH_GEOJSON_FILE)) {

            // ********************* intro *********************

            // Écriture : Jdom2 elements pour créer un fichier kml
            Element root = new Element("Document");
            Document document = new Document(root);

            // Lecture : parsing du fichier, met toutes les features in a JSONArray
            Object obj = jsonParser.parse(reader);
            JSONObject featureCollection = (JSONObject) obj;
            JSONArray featuresArray = (JSONArray) featureCollection.get("features"); // get features

            // Lecture :
            String[] propertiesName = {"ADMIN", "ISO_A3"};
            // comment extraire ces données du geojson file, à la place de hardcoder ici?

            // ********************* add style to kml file *********************

            // create styleMap
            Element style1 = style(NORMAL_STYLE, "ff0000ff", 3);
            Element style2 = style(HIGHLIGHT_STYLE, "ffff7fff", 3);
            Element styleMap = styleMap(STYLEMAP);

            document.getRootElement().addContent(styleMap);
            document.getRootElement().addContent(style1);
            document.getRootElement().addContent(style2);


            // ********************* get info from geojson file and write it in kml file *********************
            // pour chaque feature, on récupère les properties et la geometry (coordinates) pour les mettre dans un fichier kml
            for(int featureIndex = 0; featureIndex < featuresArray.size(); featureIndex++){

                // Lecture :
                JSONObject feature = (JSONObject) featuresArray.get(featureIndex);

                // Écriture : one feature -> one placemark
                Element placemark = new Element("Placemark");
                Element name = new Element("name");

                // --------- properties -----------
                Element extendedData = new Element("ExtendedData");

                // for every property, create element-balises data and value, get value from properties JsonObject
                JSONObject properties = (JSONObject) feature.get("properties"); // Lecture
                for(String propertyName : propertiesName){

                    // Écriture
                    Element data = new Element("Data").setAttribute(new Attribute("name", propertyName));
                    Element value = new Element("value").addContent((String)properties.get(propertyName));
                    data.addContent(value);

                    extendedData.addContent(data);

                    name.addContent((String)properties.get(propertyName)); // pour afficher nom dans google earth
                }

                // add <name> (pourafficher le nom du pays dans interface google earth) et <styleUrl>
                placemark.addContent(name);
                placemark.addContent(new Element("styleUrl").addContent("#"+STYLEMAP));

                // ajoute properties and geometry-coordinates in placemark
                placemark.addContent(extendedData);

                // --------- geometry -----------
                // Lecture : get geometry and its coordinates ...
                JSONObject geometry = (JSONObject) feature.get("geometry");
                String typeGeometry = (String) geometry.get("type");
                JSONArray coordinateData = (JSONArray) geometry.get("coordinates");
                String coordinatesString = "";

                // Écriture
                if(typeGeometry.equals("MultiPolygon")){
                    Element multiGeometry = new Element("MultiGeometry");
                    for(int i = 0; i < coordinateData.size(); i++){
                        coordinatesString = getPolygonCoordString((JSONArray) coordinateData.get(i));
                        multiGeometry.addContent(createPolygonElement(coordinatesString));
                    }
                    placemark.addContent(multiGeometry);
                }

                if(typeGeometry.equals("Polygon")) {
                    coordinatesString = getPolygonCoordString(coordinateData);
                    placemark.addContent(createPolygonElement(coordinatesString));
                }

                document.getRootElement().addContent(placemark);

            }


            XMLOutputter xmlOutputer = new XMLOutputter();
            xmlOutputer.setFormat(Format.getPrettyFormat());
            xmlOutputer.output(document, new FileWriter(OUTPUT_PATHFILE));

            System.out.println("XML File was created successfully!");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    // Lecture d'un polygon dans geojson
    private static String getPolygonCoordString(JSONArray coordinate){
        String result = "";
        JSONArray coordinates = (JSONArray) coordinate.get(0);
        for (int i = 0; i < coordinates.size(); i++) {
            JSONArray coord = (JSONArray) coordinates.get(i);
            double x = (Double) coord.get(0);
            double y = (Double) coord.get(1);
            result += x + "," + y + " ";
            //if (i != coordinates.size() - 1)
            //    result += " ";
        }
        return result;
    }

    // Écriture d'un element polygon
    private static Element createPolygonElement(String coordinatesString){
        Element coordinates = new Element("coordinates").addContent(coordinatesString);
        Element linearRing = new Element("LinearRing").addContent(coordinates);
        Element outerBoundaryIs = new Element("outerBoundaryIs").addContent(linearRing);
        return new Element("Polygon").addContent(outerBoundaryIs);
    }

    // create a sytle (besoin de deux style pour un element StyleMap)
    private static Element style(String styleId, String colorLine, int widthLine){ ;
        Element style = new Element("Style").setAttribute("id", styleId);
        Element lineStyle = new Element("LineStyle");
        Element color = new Element("color").addContent(colorLine);
        Element width = new Element("width").addContent(Integer.toString(widthLine));
        lineStyle.addContent(color).addContent(width);
        Element polyStyle = new Element("PolyStyle");
        Element fill = new Element("fill").addContent("0");
        polyStyle.addContent(fill);
        return style.addContent(lineStyle).addContent(polyStyle);
    }

    // create styleMap
    private static Element styleMap(String styleId){
        Element styleMap = new Element("StyleMap").setAttribute("id", styleId);

        Element pair1 = new Element("Pair");
        Element keyNormal = new Element("key").addContent("normal");
        Element styleUrl1 = new Element("styleUrl").addContent("#" + NORMAL_STYLE);
        pair1.addContent(keyNormal).addContent(styleUrl1);

        Element pair2 = new Element("Pair");
        Element keyHighlight = new Element("key").addContent("highlight");
        Element styleUrl2 = new Element("styleUrl").addContent("#" + HIGHLIGHT_STYLE);
        pair2.addContent(keyHighlight).addContent(styleUrl2);

        return styleMap.addContent(pair1).addContent(pair2);
    }


}
