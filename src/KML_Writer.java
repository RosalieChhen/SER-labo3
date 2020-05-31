import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class KML_Writer {

    final static String NORMAL_STYLE = "normalStyle";
    final static String HIGHLIGHT_STYLE = "highlightStyle";
    final static String STYLEMAP = "labo3Style";

    void writeKMLFile(String outputPath, List<Feature> featuresCollection) {
        try {

            // Création du document kml
            Element root = new Element("Document");
            Document document = new Document(root);

            // ********************* add style to kml file *********************
            // create styleMap
            Element style1 = style(NORMAL_STYLE, "ff0000ff", 3);
            Element style2 = style(HIGHLIGHT_STYLE, "ffff7fff", 3);
            Element styleMap = styleMap(STYLEMAP);

            document.getRootElement().addContent(styleMap);
            document.getRootElement().addContent(style1);
            document.getRootElement().addContent(style2);

            // ********************* write properties and geometry (coordinates) *********************
            for(int i = 0; i < featuresCollection.size(); i++){

                Feature feature = featuresCollection.get(i);
                Element placemark = new Element("Placemark");

                // add name pour l'affichage dans l'interface google earth
                Element name = new Element("name");
                name.addContent(feature.getName()); // pour afficher nom des pays dans interface google earth
                placemark.addContent(name);
                placemark.addContent(new Element("styleUrl").addContent("#"+STYLEMAP));

                // ------------------ properties ------------------
                Element extendedData = new Element("ExtendedData");
                Map<String, String> properties = feature.getProperties();
                for (Map.Entry<String, String> property : properties.entrySet()) {
                    Element data = new Element("Data").setAttribute(new Attribute("name", property.getKey()));
                    Element value = new Element("value").addContent(property.getValue());
                    data.addContent(value);
                    extendedData.addContent(data);
                }

                placemark.addContent(extendedData);

                // ------------------ geometry ------------------

                placemark.addContent(feature.getGeometry().toKML());

                document.getRootElement().addContent(placemark);

            }

            XMLOutputter xmlOutputer = new XMLOutputter();
            xmlOutputer.setFormat(Format.getPrettyFormat());
            xmlOutputer.output(document, new FileWriter(outputPath));

            System.out.println("KML File was created successfully!");

        } catch (IOException e){
            System.out.println(e.getMessage());
        }
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
