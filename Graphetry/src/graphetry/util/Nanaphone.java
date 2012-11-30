package graphetry.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A vowel-respecting, last-syllable, rhyming utility.
 *
 * Other available phonetic encoders generally drop vowels, or produce too many
 * variations for a given word. Nanaphone attempts to combine syllable-splitting
 * with phonetic rules to return a normalized last-syllable suitable for
 * rhyming.
 *
 * TODO: Support various levels of rhyme.
 *
 * @author Sam
 */
public class Nanaphone {

    static Pattern MULTI_VOWEL = Pattern.compile(".*?([aeiouy]{2,}[^aeiouy]*)$");
    static Pattern CVC_GUESS = Pattern.compile(".*?([^aeiouy]{0,1}[aeiouy]+[^aeiouy]*)$");
    static Pattern VCV_GUESS = Pattern.compile(".*?([aeiouy]+[^aeiouy]{0,1}[aeiouy]+[^aeiouy]*)$");

    /*
     * TODO:
     * plague, greg, hauge
     * tion, shun, cion
     * tion/shun, son (level 2?)
     * brown, down
     * new, shoe, flue, boo
     * great, ate, plate
     */
    
    
    /**
     * Returns the normalized ending, rhyming part of the word.
     * First this looks for ending double vowels (i.e. -VV, -VVC*).
     * Failing that, we look for a consonant vowel grouping, with an optional starting consonant (i.e. -VC*, -CVC*)
     * If that doesn't match or isn't long enough, we add an extra vowel in font (i.e. -VCVC*)
     * 
     * TODO: Need to make sure these patterns are tighter (i.e. no redundant matching)
     * @param word
     * @return 
     */
    public String getRhymingPart(String word) {
        String processedWord = word;
        processedWord = processedWord.toLowerCase();

        //Combo-sounds
        processedWord = processedWord.replaceAll("ch", "X");
        processedWord = processedWord.replaceAll("sh", "S");
        processedWord = processedWord.replaceAll("th", "O");
        
        processedWord = processedWord.replaceAll("ease$|eas$|eeze$", "ees"); // bees, freeze, peas
        
        String processedEnding;

        Matcher mvm = MULTI_VOWEL.matcher(processedWord);
        Matcher cvcm = CVC_GUESS.matcher(processedWord);
        Matcher vvm = VCV_GUESS.matcher(processedWord);
        
        if (mvm.matches()){
            processedEnding = mvm.group(1);
        } else if (cvcm.matches() && cvcm.group(1).length() > 2){
            processedEnding = cvcm.group(1);
        } else if (vvm.matches()){
            processedEnding = vvm.group(1);
        } else {
            processedEnding = processedWord;
        }

        processedEnding = processedEnding.replaceAll("ue", "oo");

        return processedEnding;
    }
}
