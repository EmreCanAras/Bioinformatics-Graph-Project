import java.util.Iterator;
import java.util.List;

public interface VertexInterface {

    // Gets the name (ID) of the vertex.
    String getName();
    // Sets the name (ID) of the vertex.
    void setName(String name);

    // Adds a new connection (edge) to this vertex.
    void addEdge(Edge e);
    // Returns the list of all connections (edges) from this vertex.
    List<Edge> getEdges();
    // Checks if there is a connection to the given neighbor.
    boolean hasEdge(String neighborName);
    // Returns the weight (cost) of the edge to the specified destination.
    int getWeight(String destinationName);

    // Marks the vertex as visited.
    void visit();
    // Resets the visited status (marks as unvisited).
    void unVisit();
    // Checks if the vertex has been visited.
    boolean isVisited();
    // Finds and returns a neighbor that has not been visited yet.
    Vertex getUnvisitedNeighbor();
    // Returns an iterator to loop through the neighbors.
    Iterator<Vertex> getNeighborIterator();

    // Gets the distance value used in shortest path algorithms (like Dijkstra).
    long getDistance();
    // Sets the distance value for shortest path calculations.
    void setDistance(long distance);

    // Gets the previous vertex in the path (used for backtracking).
    Vertex getParent();
    // Sets the previous vertex in the path.
    void setParent(Vertex parent);
}