import edu.princeton.cs.algs4.Digraph;

public class SAP 
{
   private Digraph graph;
   // constructor takes a digraph (not necessarily a DAG)
   public SAP(Digraph G)
   {
      graph = new Digraph(G);
   }
   // length of shortest ancestral path between v and w; -1 if no such path
   public int length(int v, int w)
   {  
      // if ((v < 0 || v > Graph.V() - 1) || (w < 0 || w > Graph.V())
      DeluxeBFS bfs = new DeluxeBFS(graph, v, w);
      return bfs.get_length();
   }
   // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
   public int ancestor(int v, int w)
   {
      DeluxeBFS bfs = new DeluxeBFS(graph, v, w);
      return bfs.get_ancestor();
   }
   // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
   public int length(Iterable<Integer> v, Iterable<Integer> w)
   {
      DeluxeBFS bfs = new DeluxeBFS(graph, v, w);
      return bfs.get_length();
      // return 0;
   }
   // a common ancestor that participates in shortest ancestral path; -1 if no such path
   public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
   {  
      DeluxeBFS bfs = new DeluxeBFS(graph, v, w);
      return bfs.get_ancestor();
   }
   // do unit testing of this class
   public static void main(String[] args)
   {

   }
}