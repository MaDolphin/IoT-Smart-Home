package de.montigem.be;


import de.se_rwth.commons.logging.Log;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class NeuesFormularAnpassen {

  // TODO MV: write as test
    public static void main(String[] args) {
        //Locales testen
        Log.trace("Working Directory = " +
                System.getProperty("user.dir"), NeuesFormularAnpassen.class.getName());
        // name der Datei dei zu Testen
        String datei="kreditor";

        String confdatei = "01.Backend"+File.separator+"src" + File.separator + "main" + File.separator + "resources" + File.separator + datei+".yaml";
        String pdf = "01.Backend"+File.separator+"src" + File.separator + "main" + File.separator + "resources" + File.separator + datei+".pdf";


        Reader reader;

        try {
            reader = new FileReader(confdatei);


            Yaml yaml = new Yaml();
            Map<String, Map<String, String>> values = (Map<String, Map<String, String>>) yaml.load(reader);
            Overlay overlay = new Overlay();
            try {
                PDDocument original = new PDDocument().load(new File(pdf));

                PDDocument over = new PDDocument();

                for (int i = 0; i < original.getNumberOfPages(); i++) {

                    PDPage page = new PDPage();
                    over.addPage(page);
                }

                for (String a : values.get("Config").keySet()) {
                    String[] position = values.get("Config").get(a).split(",");
                    PDPageContentStream contentStream = new PDPageContentStream(over, over.getPage(Integer.parseInt(position[0])), PDPageContentStream.AppendMode.APPEND, false);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(Integer.parseInt(position[1]), Integer.parseInt(position[2]));
                    if (Integer.parseInt(position[3])>0) {
                        contentStream.setFont(PDType1Font.HELVETICA, Integer.parseInt(position[3]));
                    }
                    else{
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                    }
                    contentStream.showText("abcdefg");
                    contentStream.endText();
                    contentStream.close();
                }

                overlay.setOverlayPosition(Overlay.Position.BACKGROUND);
                overlay.setInputPDF(original);
                overlay.setAllPagesOverlayPDF(over);
                Map<Integer, String> ovmap = new HashMap<>();
                PDDocument res = overlay.overlay(ovmap);
                res.save("test.pdf");
                over.close();
                original.close();


            } catch (IOException e) {
              Log.warn("MAB0x5200: write pdf", e);
            }

        } catch (FileNotFoundException e) {
          Log.warn("MAB0x5201: read File", e);
        }
        Log.trace("Fertige test.pdf Datei liegt im Ordner implementation ", NeuesFormularAnpassen.class.getName());
    }

}

