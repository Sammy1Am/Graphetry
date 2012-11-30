/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphetry.util;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.ColognePhonetic;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.codec.language.Nysiis;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.codec.language.bm.BeiderMorseEncoder;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.codec.language.bm.Languages.LanguageSet;
import org.apache.commons.codec.language.bm.NameType;
import org.apache.commons.codec.language.bm.PhoneticEngine;
import org.apache.commons.codec.language.bm.RuleType;

/**
 *
 * @author Sam
 */
public class WordUtils {

    public static String[] justWords(String richSentence) {
        ArrayList<String> processedWords = new ArrayList<String>();

        Scanner s = new Scanner(richSentence);
        s.useDelimiter("\\s+");

        while (s.hasNext()) {
            String nextWord = s.next().trim().toLowerCase();
            nextWord = nextWord.replaceAll("\\W", "");
            if (!nextWord.isEmpty()) {
                processedWords.add(nextWord);
            }
        }

        return processedWords.toArray(new String[processedWords.size()]);
    }

    public static int countSyllables(String word) {
        String modifiedWord = word.trim().toLowerCase().replaceAll("\\W", "");

        if (modifiedWord.length() > 0 && modifiedWord.length() < 4) {
            return 1;
        }

        modifiedWord = word.toLowerCase().replaceAll("(?:[^laeiouy]es|ed|[^laeiouy]e)$", "");
        Matcher m = Pattern.compile("[aeiouy]{1,2}").matcher(modifiedWord);

        int syllableCount = 0;

        while (m.find()) {
            syllableCount++;
        }

        return syllableCount;
    }
    
    public static String lastSound(String word){
        String modifiedWord = word.trim().toLowerCase().replaceAll("\\W", "");
        
        Nanaphone np = new Nanaphone();
        String returnString = np.getRhymingPart(modifiedWord);
        
        //String returnString = word;
        
        //PhoneticEngine pe = new PhoneticEngine(NameType.GENERIC,RuleType.EXACT,true);
        //String returnString = pe.encode(word);
        
        //Nanaphone np = new Nanaphone();
        //String returnString = np.encode(word);
        
        //System.out.println(returnString);
        
//        Matcher m = Pattern.compile(".*?([^aeiouy]{0,1}[aeiouy]+[^aeiouy]*)$").matcher(returnString);
//        if (m.matches()){
//          returnString = m.group(1);
//        }

        //System.out.println(returnString);
        
        //LanguageSet ls = new LanguageSet() {}
        
        
        //BeiderMorseEncoder bme = new BeiderMorseEncoder();
        
        //LanguageSet ls = LanguageSet.from(Languages.getInstance("english").getLanguages());
            
        
        return returnString;
    }
}
