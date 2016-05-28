import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import java.util.HashMap;
import java.util.HashSet;


public class DeluxeBFS
{
    private static final int INFINITY = Integer.MAX_VALUE;
    private HashMap<Integer, Integer> distTov; // distances to each node from v
    private HashMap<Integer, Integer> distTow; // distances to each node from w
    private HashSet<Integer> markedv; // is there an s -> v path?
    private HashSet<Integer> markedw; // is there an s -> w path?
    private int length;
    private int ancestor;

    public DeluxeBFS(Digraph G, int s)
    {
        distTov = new HashMap<Integer, Integer>();
        markedv = new HashSet<Integer>();
        // bfs(G, s);
    }

    public DeluxeBFS(Digraph G, int v, int w)
    {
        distTov = new HashMap<Integer, Integer>();
        markedv = new HashSet<Integer>();
        distTow = new HashMap<Integer, Integer>();
        markedw = new HashSet<Integer>();
        naive_ancestor(G, v, w);   
    }

    public DeluxeBFS(Digraph G, Iterable<Integer> v, Iterable<Integer> w)
    {
        distTov = new HashMap<Integer, Integer>();
        markedv = new HashSet<Integer>();
        distTow = new HashMap<Integer, Integer>();
        markedw = new HashSet<Integer>();
        naive_ancestor(G, v, w);   
    }

    private void bfs(Digraph G, int s, HashSet<Integer> marked, HashMap<Integer, Integer> distTo)
    {
        Queue<Integer> q = new Queue<Integer>();
        marked.add(s);
        distTo.put(s, 0);
        q.enqueue(s);
        while (!q.isEmpty())
        {
            int v = q.dequeue();
            for (int w : G.adj(v))
            {
                if (!marked.contains(w))
                {
                    distTo.put(w, distTo.get(v) + 1);
                    marked.add(w);
                    q.enqueue(w);
                }
            }
        }
    }

    private void bfs(Digraph G, Iterable<Integer> sources, HashSet<Integer> marked, HashMap<Integer, Integer> distTo)
    {
        Queue<Integer> q = new Queue<Integer>();
        for (int s : sources)
        {
            marked.add(s);
            distTo.put(s, 0);
            q.enqueue(s);
        }
        while (!q.isEmpty())
        {
            int v = q.dequeue();
            for (int w : G.adj(v))
            {
                if(!marked.contains(w))
                {
                    distTo.put(w, distTo.get(v) + 1);
                    marked.add(w);
                    q.enqueue(w);
                }
            }
        }
    }

    private void naive_ancestor(Digraph G, int v, int w)
    {
        //bfs for v
        bfs(G, v, markedv, distTov);
        //bfs for w
        bfs(G, w, markedw, distTow);
        //create auxilliary set
        HashSet<Integer> ancestors = new HashSet<Integer>(markedv);
        //find intersection between two sets(common ancestors)
        ancestors.retainAll(markedw);
        // initialize length to the common ancestor
        length = INFINITY;
        if (ancestors.size() > 0)
        {
            // find shortest distance to a common ancestor
            for (int ans: ancestors)
            {
                int temp = distTow.get(ans) + distTov.get(ans);
                if (temp < length)
                {
                    length = temp;
                    ancestor = ans;
                }
            }
        }
        else 
        {
            length = -1;
            ancestor = -1;
        }
    }

    private void naive_ancestor(Digraph G, Iterable<Integer> v, Iterable<Integer> w)
    {
        // bfs for v[]
        bfs(G, v, markedv, distTov);
        //bfs for w[]
        bfs(G, w, markedw, distTow);
        //create auxilliary set
        HashSet<Integer> ancestors = new HashSet<Integer>(markedv);
        //find intersection between two sets(common ancestors)
        ancestors.retainAll(markedw);
        // initialize length to the common ancestor
        length = INFINITY;
        if (ancestors.size() > 0)
        {
            // find shortest distance to a common ancestor
            for (int ans: ancestors)
            {
                int temp = distTow.get(ans) + distTov.get(ans);
                if (temp < length)
                {
                    length = temp;
                    ancestor = ans;
                }
            }
        }
        else 
        {
            length = -1;
            ancestor = -1;
        }
    }

    public int get_length()
    {
        return length;
    }
    public int get_ancestor()
    {
        return ancestor;
    }
    public static void main(String[] args)
    {

    }
}