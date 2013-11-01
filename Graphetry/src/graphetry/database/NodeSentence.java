/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphetry.database;

import java.util.ArrayList;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Sam
 */
public class NodeSentence {
    private final Node[] nodeArray;
    private int syllableCount;
    
    public NodeSentence(Node[] inputNodes){
        nodeArray = inputNodes;
    
    }
    
    public int totalSyllables(){
        if (syllableCount<1){
            syllableCount = 0;
            for (Node n : nodeArray){
                if (n.hasProperty(DatabaseControl.P_SYLLABLES)){
                    syllableCount += Integer.parseInt(n.getProperty(DatabaseControl.P_SYLLABLES).toString());
                }
            }
        }
        return syllableCount;
    }
    
    //TODO make this more efficient (calculate on creation?)
    public String[] toStringArray(){
        ArrayList<String> returnTokens = new ArrayList<String>();

        for (int i = 0; i < nodeArray.length; i++) {
            if (nodeArray[i].hasProperty(DatabaseControl.P_WORD)){// If there's no word on this node, don't try to string-ify it.  e.g. end nodes
                returnTokens.add((String) nodeArray[i].getProperty(DatabaseControl.P_WORD));
            }
        }

        return returnTokens.toArray(new String[returnTokens.size()]);
    }
}
