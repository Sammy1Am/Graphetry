/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphetry;

import com.sun.speech.freetts.lexicon.LetterToSoundImpl;
import graphetry.database.CorpusManagement;
import graphetry.database.DatabaseControl;
import graphetry.util.WordUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

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
    public static void main(String[] args) throws IOException {

        LetterToSoundImpl lts = new LetterToSoundImpl(new URL("jar:file:lib/freetts/cmudict04.jar!/com/sun/speech/freetts/en/us/cmudict04_lts.bin"), true);

        
        DatabaseControl dbc = new DatabaseControl("gdbdir");
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
                    System.out.println(BOLD + WordUtils.lastSound(lts, 3, inputLine) + PLAIN);
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
    
    /*
     * NOTES:
     * For limerick generation, only very start needs to be START and very end needs to be END.  Other
     * lines are optional endings, and only need a START if the last one was an END.
     * 
     * Check on rhyming multiple words like "Nantuckett" and "cluck it". ;)
     */
    
}
