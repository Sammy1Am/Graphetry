/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphetry;

import graphetry.database.CorpusManagement;
import graphetry.database.DatabaseControl;
import graphetry.util.WordUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Sam
 */
public class Graphetry {

    static String BOLD = "\033[0;1m";
    static String PLAIN = "\033[0;0m";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        DatabaseControl dbc = new DatabaseControl("gdbfolder");
        CorpusManagement corpMan = new CorpusManagement(dbc);

        boolean running = true;

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (running) {
            try {
                String inputLine = in.readLine();

                if (inputLine.equalsIgnoreCase("/exit")) {
                    running = false;
                    System.out.println("Shutting down...");
                } else if (inputLine.startsWith("/read")) {
                    try {
                        String[] params = inputLine.split(" ", 2);
                        corpMan.readText(params[1]);
                    } catch (Exception ex) {
                        Logger.getLogger(Graphetry.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (inputLine.equalsIgnoreCase("/rand")) {
                    String response = StringUtils.join(dbc.getRandomSentence(), " ");
                    System.out.println(BOLD + response + PLAIN);
                } else {
                    System.out.println(BOLD + WordUtils.lastSound(inputLine) + PLAIN);
                    System.out.println(BOLD + StringUtils.join(dbc.findRhymingWords(inputLine),", ") + PLAIN);

                    String[] inputWords = WordUtils.justWords(inputLine);
                    
                    String response = StringUtils.join(dbc.buildRhymingSentence(inputWords[inputWords.length-1])," ");
                    
                    //String response = ArrayUtils.toString(WordUtils.justWords(inputLine));
                    //dbc.writeArrayToGraph(WordUtils.justWords(inputLine));

                    System.out.println(BOLD + response + PLAIN);
                }


            } catch (IOException ex) {
                Logger.getLogger(Graphetry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
