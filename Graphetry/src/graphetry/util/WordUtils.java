/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphetry.util;

import com.sun.speech.freetts.lexicon.LetterToSound;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

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
    
    public static String lastSound(LetterToSound lts, int numberOfPhonemes, String word){
        String modifiedWord = word.trim().toLowerCase().replaceAll("\\W", "");
        
        
        String[] phones = lts.getPhones(modifiedWord, null);
        String returnString = StringUtils.join(Arrays.copyOfRange(phones,Math.max(0, phones.length-numberOfPhonemes) ,phones.length), ".");   
        
        return returnString;
    }
}
