/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphetry.database;

import graphetry.util.WordUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Sam
 */
public class CorpusManagement {
    
    private DatabaseControl dbc;
    
    public CorpusManagement(DatabaseControl db){
        this.dbc = db;
    }
    
    public void readText(String filePath){
        File f = new File(filePath);
        Pattern sentencePattern = Pattern.compile("(((?<=(?<!Mr|Mrs)[\\.\\!\\?])\"?(\\s|[\\r\\n]+))|((\\r\\n){2,}))");
        try {
            Scanner sentences = new Scanner(new FileInputStream(f));
            sentences.useDelimiter(sentencePattern);
            
            long counter = 0;
            
            System.out.println("Begin reading file \""+filePath+"\".  Lines read:");
            System.out.print("0");
            
            while (sentences.hasNext()){
                String nextSentence = sentences.next();
                nextSentence = nextSentence.replaceAll("[\\r\\n]+", " "); // Replace line-breaks with spaces.
                nextSentence = nextSentence.replaceAll("\"", ""); // Remove quotes.
                nextSentence = nextSentence.replaceAll("\\s{2,}", " "); // Shorten long white-space breaks to normal spaces.
                String[] words = WordUtils.justWords(nextSentence);
                
                
                if (words.length > 2){
                    dbc.writeArrayToGraph(words);
                }
                
                counter++;
                
                if (counter%100 == 0){
                    System.out.print("\r"+counter);
                }
            }
            
            System.out.println("\rFinished reading!");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CorpusManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
