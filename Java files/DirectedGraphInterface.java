import java.util.Queue;

public interface DirectedGraphInterface {

    // Adds a new connection (edge) between two vertices with a weight.
    void addEdge(String source, String destination, int weight);
    // Returns the total number of vertices in the graph.
    int size();
    // Checks if a vertex with the given name exists in the graph.
    boolean containsVertex(String name);
    // Returns the total number of edges in the graph.
    int getNumberOfEdges();

    // Performs Breadth-First Search (BFS) starting from the origin vertex.
    Queue<String> getBreadthFirstTraversal(String origin);
    // Performs Depth-First Search (DFS) starting from the origin vertex.
    Queue<String> getDepthFirstTraversal(String origin);

    // Finds the best path between two vertices based on confidence score.
    Path getMostConfidentPath(String origin, String destination);

    // Calculates the average degree of the graph.
    double getAverageDegree();
    // Calculates the reciprocity ratio of the graph.
    double getReciprocity();
}