/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graphetry.database;

import graphetry.util.WordUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Metaphone;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.helpers.collection.IteratorUtil;
import scala.actors.threadpool.Arrays;

/**
 *
 * @author Sam
 */
public class DatabaseControl {

    private GraphDatabaseService graphDb;
    private ExecutionEngine engine;
    private final int ORDER = 2;
    private final Index<Node> nodeIndex;

    private final DoubleMetaphone metap = new DoubleMetaphone();
    private final static int RHYME_PHONES = 3; // Number of metaphone characters to store for rhyming.
    
    private static enum RelTypes implements RelationshipType {

        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX
    }
    private static String P_WORD = "wordkey";
    private static String P_SYLLABLES = "syllables";
    private static String P_END = "endnode";
    private static String I_WORD = "word";
    private static String I_END = "endnode";
    private static String I_PHON = "phoneme";
 
    public DatabaseControl(String dbPath) {
        this(dbPath, 2);
    }

    private DatabaseControl(String dbPath, int order) {
        System.out.println("Initializing database of order "+order+"...");
        //this.ORDER = order;
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
        engine = new ExecutionEngine(graphDb);
        nodeIndex = graphDb.index().forNodes("node_idx");
        //keywordIndex = graphDb.index().forNodes("keyword");
        //userIndex = graphDb.index().forNodes("user");
        registerShutdownHook(graphDb);
        System.out.println("Database initialized.");
    }

    /**
     * Writes an array of pre-processed, cleaned, words to the graph database.
     * @param inputArray 
     */
    public void writeArrayToGraph(String[] inputArray) {
        if (inputArray.length < ORDER + 1) {
            throw new IllegalArgumentException("Input array must be at least " + (ORDER + 1) + " items long (ORDER+1)");
        }

        Transaction tx = graphDb.beginTx();
        try {
            Node[] workingNodes = new Node[inputArray.length];

            for (int i = 0; i < inputArray.length; i++) {
                // Create Node
                Node newNode;
                Node foundNode = nodeIndex.get(I_WORD, inputArray[i]).getSingle();
                if (foundNode != null) {
                    newNode = foundNode;
                } else {
                    newNode = graphDb.createNode();
                    newNode.setProperty(P_WORD, inputArray[i]);
                    newNode.setProperty(P_SYLLABLES, WordUtils.countSyllables(inputArray[i]));
                    nodeIndex.putIfAbsent(newNode, I_WORD, inputArray[i]);
                    nodeIndex.add(newNode, I_PHON, WordUtils.lastSound(inputArray[i]));
                    
                    
                    // If it's first, index it as first
                    if (i == 0) {
                        nodeIndex.add(newNode, I_END, "START");
                        newNode.setProperty(P_END, "START");
                    }
                    
                    // If it's last, index it as last
                    if (i == inputArray.length - 1) {
                        nodeIndex.add(newNode, I_END, "END");
                        newNode.setProperty(P_END, "END");
                    }
                }
                // Add to working nodes
                workingNodes[i] = newNode;

                /* Create relationships, looking back.
                 * So for sentence A B C D:
                 * A -3-> D
                 * B -2-> D
                 * C -1-> D
                 */
                for (int r = i - Math.min(i, ORDER); r < i; r++) {
                    workingNodes[r].createRelationshipTo(workingNodes[i], RelTypes.values()[i - r]);
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }

    }

    /**
     * Finds all rhyming nodes (regardless of end-property), and returns as a
     * String array.
     * @param inputWord
     * @return 
     */
    public String[] findRhymingWords(String inputWord){
        return nodesToStringArray(findRhymingNodes(inputWord,false));
    }
    
    /**
     * Searches on the phoneme index to find words that rhyme with the inputWord.
     * If endNodesOnly is true, it will only search on words that end a phrase.
     * @param inputWord
     * @param endNodesOnly
     * @return 
     */
    public Node[] findRhymingNodes(String inputWord, boolean endNodesOnly){
        IndexHits<Node> rhymeHits;
        if (endNodesOnly){
            rhymeHits = nodeIndex.query(I_PHON+":"+ WordUtils.lastSound(inputWord)+" AND "+ I_END +":"+ "END");
            
            // If there's no matches, try again with a looser query
            if (rhymeHits.size() < 1){
                rhymeHits = nodeIndex.query(I_PHON+":*"+ WordUtils.lastSound(inputWord).substring(1) +" AND "+ I_END +":"+ "END");
            }
        } else {
            rhymeHits = nodeIndex.query(I_PHON, WordUtils.lastSound(inputWord));
        }
        
        ArrayList<Node> rhymingNodes = new ArrayList<Node>();
        
        for (Node n:rhymeHits){
            rhymingNodes.add(n);
        }
        
        return rhymingNodes.toArray(new Node[rhymingNodes.size()]);
    }
    
    public String[] buildRhymingSentence(String inputWord){
        Node[] rhymingEnds = findRhymingNodes(inputWord, true);
        
        if (rhymingEnds.length > 0){
        Node[] sentenceOption = randomBuild(new Node[]{rhymingEnds[new Random().nextInt(rhymingEnds.length)]});
            return nodesToStringArray(sentenceOption);
        } else {
            return null;
        }
    }
    
    
    
    public String[] getRandomSentence(){
        //TODO Make this more random...
        
        ArrayList<ArrayList<Node>> seedOptions = getEndings();
        
        Node[] seedNodes = seedOptions.get(new Random().nextInt(seedOptions.size())).toArray(new Node[0]);
        
        Node[] resultNodes = randomBuild(seedNodes);
        
        return nodesToStringArray(resultNodes);
        
    }
    
    private Node[] randomBuild(Node[] seedNodes){
        
        ArrayList<Node> workingNodes = new ArrayList<Node>(Arrays.asList(seedNodes));
        
        //TODO Sometimes keep going past end if there are more relationships.
        while (!workingNodes.get(workingNodes.size()-1).hasProperty(P_END) || !workingNodes.get(workingNodes.size()-1).getProperty(P_END).equals("END")){
            ArrayList<Node> options = getFollowingNodes(workingNodes.subList(workingNodes.size()-Math.min(1,ORDER), workingNodes.size()).toArray(new Node[0]));
            
            workingNodes.add(options.get(new Random().nextInt(options.size())));
        }
        
        while (!workingNodes.get(0).hasProperty(P_END) || !workingNodes.get(0).getProperty(P_END).equals("START")){
            ArrayList<Node> options = getPreceedingNodes(workingNodes.subList(0, Math.min(workingNodes.size(), ORDER)).toArray(new Node[0]));
            
            workingNodes.add(0,options.get(new Random().nextInt(options.size())));
        }
        
        return workingNodes.toArray(new Node[workingNodes.size()]);
    }
    
    private ArrayList<Node> getFollowingNodes(Node[] currentNodes){

        int comparisonOrder = Math.min(ORDER, currentNodes.length);
        
        ExecutionResult result;
        
        if (comparisonOrder == 1){
             result = engine.execute("START a=node("+currentNodes[0].getId()+") MATCH a-[:ONE]->next RETURN next");
        } else {
             result = engine.execute("START a=node("+currentNodes[currentNodes.length-2].getId()+"),b=node("+currentNodes[currentNodes.length-1].getId()+") MATCH a-[:ONE]->b-[:ONE]->next WHERE a-[:TWO]->next RETURN next");
        }
        
        ArrayList<Node> followingNodeOptions = new ArrayList<Node>();
        
        Iterator<Node> n_column = result.columnAs( "next" );
        for (Node n : IteratorUtil.asIterable( n_column )){
            followingNodeOptions.add(n);
        }
        
        return followingNodeOptions;
    }
    
    private ArrayList<Node> getPreceedingNodes(Node[] currentNodes){
        
        int comparisonOrder = Math.min(ORDER, currentNodes.length);
        ExecutionResult result;
        
        if (comparisonOrder == 1){
             result = engine.execute("START a=node("+currentNodes[0].getId()+") MATCH pre-[:ONE]->a RETURN pre");
        } else {
            result = engine.execute("START a=node("+currentNodes[0].getId()+"),b=node("+currentNodes[1].getId()+") MATCH pre-[:ONE]->a-[:ONE]->b WHERE pre-[:TWO]->b RETURN pre");
        }
        ArrayList<Node> followingNodeOptions = new ArrayList<Node>();
        
        Iterator<Node> n_column = result.columnAs( "pre" );
        for (Node n : IteratorUtil.asIterable( n_column )){
            followingNodeOptions.add(n);
        }
        
        return followingNodeOptions;
    }
    
    //TODO Make this order-independent
    private ArrayList<ArrayList<Node>> getEndings() {
        ExecutionResult result = engine.execute("START end=node:node_idx(endnode='END') MATCH m2-[:ONE]->m1-[:ONE]->end WHERE m2-[:TWO]->end RETURN m2,m1,end");

        ArrayList<ArrayList<Node>> endNodeOptions = new ArrayList<ArrayList<Node>>();
        
        //endNodes
        for (Map<String,Object> row : result){
            ArrayList<Node> endNodes = new ArrayList<Node>(); 
            for (Object n : row.values()){
                endNodes.add((Node)n);
            }
            endNodeOptions.add(endNodes);
        }
        return endNodeOptions;
    }

    
    
    //
    //// Utility Methods
    //
    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running example before it's completed)
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                graphDb.shutdown();
                System.out.println("Database shutdown.");
            }
        });
    }
    
    private String[] nodesToStringArray(Node[] inputNodeArray) {
        ArrayList<String> returnTokens = new ArrayList<String>();

        for (int i = 0; i < inputNodeArray.length; i++) {
            returnTokens.add((String)inputNodeArray[i].getProperty(P_WORD));
        }

        return returnTokens.toArray(new String[returnTokens.size()]);
    }
}
