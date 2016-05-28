import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import java.util.HashMap;
import java.util.ArrayList;


public class WordNet 
{
   
   private HashMap<String, ArrayList<Integer>> synsetHash;
   private ArrayList<String> synsetList;
   private int synsetNum;
   private Digraph wordGraph;
   private SAP shortestPath;
   // constructor takes the name of the two input files
   public WordNet(String synsets, String hypernyms)
   {
        In text = new In(synsets);
        synsetList = new ArrayList<String>();
        synsetHash = new HashMap<String, ArrayList<Integer>>();
        // parse words into array synsetList
        synsetNum = 0;
        while (!text.isEmpty())
        {
            String temp = text.readLine().split(",")[1];
            synsetList.add(temp);
            synsetNum++;
        }
        //initialize synsetHash with word:list of integers
        for (int i = 0; i < synsetNum; i++)
            {
                String[] stringArray = synsetList.get(i).split(" ");
                for (String s : stringArray)
                {
                    if (synsetHash.containsKey(s))
                    {
                        ArrayList<Integer> temp = synsetHash.get(s);
                        temp.add(i);
                    }
                    else
                    {
                        ArrayList<Integer> temp = new ArrayList<Integer>();
                        temp.add(i);
                        synsetHash.put(s, temp);
                    }
                }
            }

        wordGraph = new Digraph(synsetNum);
        text = new In(hypernyms);
        while (!text.isEmpty())
        {
            String[] edges = text.readLine().split(",");
            int mainEdge = Integer.parseInt(edges[0]);
            for (int i = 1; i < edges.length; i++)
            {
                wordGraph.addEdge(mainEdge, Integer.parseInt(edges[i]));
            }
        }
        // check if input is a rooted DAG
        int count = 0;
        for (int v = 0; v < synsetNum; v++)
        {
            if (wordGraph.outdegree(v) == 0) count++;
            if (count > 1) throw new IllegalArgumentException();
        }
        // may be redundant check, but wanna make sure
        if (count != 1) throw new IllegalArgumentException();

        DirectedCycle cycle = new DirectedCycle(wordGraph);
        if (cycle.hasCycle()) throw new IllegalArgumentException();

        shortestPath = new SAP(wordGraph);
                
   }
   // returns all WordNet nouns
   public Iterable<String> nouns()
   {
        return synsetHash.keySet();
   }
   // is the word a WordNet noun?
   public boolean isNoun(String word)
   {
        if (word == null) throw new NullPointerException();
        return synsetHash.containsKey(word);
   }
   // distance between nounA and nounB (defined below)
   public int distance(String nounA, String nounB)
   {
        if (nounA == null || nounB == null) throw new NullPointerException();
        if (!(isNoun(nounA) && isNoun(nounB))) throw new IllegalArgumentException();
        return shortestPath.length(synsetHash.get(nounA), synsetHash.get(nounB));
   }
   // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
   // in a shortest ancestral path (defined below)
   public String sap(String nounA, String nounB)
   {
        if (nounA == null || nounB == null) throw new NullPointerException();
        if (!(isNoun(nounA) && isNoun(nounB))) throw new IllegalArgumentException();
        return synsetList.get(shortestPath.ancestor(synsetHash.get(nounA), synsetHash.get(nounB)));
   }

   // do unit testing of this class
   public static void main(String[] args)
   {

   }
}