import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast 
{
   private WordNet w;
   public Outcast(WordNet wordnet)         // constructor takes a WordNet object
   {
        w = wordnet;
   }
   public String outcast(String[] nouns)   // given an array of WordNet nouns, return an outcast
   {
        int d = Integer.MIN_VALUE;
        String best = null;
        for (String word1 : nouns)
        {
            int tempDist = 0;
            for (String word2: nouns)
            {
                if (!word1.equals(word2))
                {
                    tempDist += w.distance(word1, word2);
                }
            }

            if (tempDist > d)
            {
                d = tempDist;
                best = word1;
            }
        }
        return best;
            
    }
    public static void main(String[] args) 
    {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) 
        {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
