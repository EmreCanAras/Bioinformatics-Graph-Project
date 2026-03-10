public interface EdgeInterface {

    // Gets the starting vertex of this edge.
    Vertex getSource();
    // Sets the starting vertex of this edge.
    void setSource(Vertex source);

    // Gets the destination (target) vertex of this edge.
    Vertex getDestination();
    // Sets the destination (target) vertex of this edge.
    void setDestination(Vertex destination);

    // Gets the weight (cost) of this connection.
    int getWeight();
    // Sets the weight (cost) of this connection.
    void setWeight(int weight);
}