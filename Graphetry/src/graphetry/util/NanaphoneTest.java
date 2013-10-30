/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphetry.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sam
 */
public class NanaphoneTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Nanaphone np = new Nanaphone();

        boolean running = true;

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (running) {
            try {
                String inputLine = in.readLine();
                
                System.out.println(np.getRhymingPart(inputLine));
                
            } catch (IOException ex) {
                Logger.getLogger(NanaphoneTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
