import java.util.*;
import java.util.LinkedList;

public class DirectedGraph implements DirectedGraphInterface {
    private HashMap<String, Vertex> vertices;
    private int numberOfEdges;


    private long maxDiameter;
    private long foundDijkstraDistance;
    private String foundDijkstraVertexName;

    private int reciprocalEdgesCount;
    private double averageDegree;

    public DirectedGraph() {
        this.vertices = new HashMap<>();
        maxDiameter = 0;
        foundDijkstraDistance = 0;
        foundDijkstraVertexName = "";
    }

    public void addEdge(String source, String destination, int weight) {

        Vertex sourceVertex = vertices.get(source);
        Vertex destinationVertex = vertices.get(destination);

        if (sourceVertex != null && destinationVertex != null && sourceVertex.hasEdge(destination)) {
            System.out.println("Error: This edge has already added!");
        } else {
            if (sourceVertex == null) {
                sourceVertex = new Vertex(source);
                vertices.put(source, sourceVertex);
            }

            if (destinationVertex == null) {
                destinationVertex = new Vertex(destination);
                vertices.put(destination, destinationVertex);
            }

            Edge edge = new Edge(sourceVertex, destinationVertex, weight);
            sourceVertex.addEdge(edge);


            //if we find two reciprocal edges increase the count
            if (hasEdge(destination, source)) reciprocalEdgesCount += 2;

            numberOfEdges++;
            averageDegree = ((averageDegree * (numberOfEdges - 1)) + weight) / numberOfEdges;
        }
    }

    public int size() {
        return vertices.size();
    }

    private void resetVertices() {
        for (Vertex v : vertices.values()) {
            v.unVisit();
            v.setEdgeCost(0);
            v.setParent(null);
            v.setWeightCost(0);
            v.setDistance(Long.MAX_VALUE);
        }
    }

    public Queue<String> getBreadthFirstTraversal(String origin) {
        resetVertices();
        Queue<String> traversalOrder = new LinkedList<>(); // Queue of vertex labels
        Queue<Vertex> vertexQueue = new LinkedList<>(); // Queue of Vertex objects

        Vertex originVertex = vertices.get(origin);
        originVertex.visit();

        traversalOrder.add(origin);    // Enqueue vertex label
        vertexQueue.add(originVertex); // Enqueue vertex

        while (!vertexQueue.isEmpty()) {
            Vertex frontVertex = vertexQueue.remove();
            Iterator<Vertex> neighbors = frontVertex.getNeighborIterator();

            while (neighbors.hasNext()) {
                Vertex nextNeighbor = neighbors.next();
                if (!nextNeighbor.isVisited()) {
                    nextNeighbor.visit();
                    traversalOrder.add(nextNeighbor.getName());
                    vertexQueue.add(nextNeighbor);
                } // end if
            } // end while
        } // end while

        return traversalOrder;
    } // end getBreadthFirstTraversal

    public Queue<String> getDepthFirstTraversal(String origin) {
        resetVertices();

        Queue<String> traversalOrder = new LinkedList<>();
        Stack<Vertex> vertexStack = new Stack<>();

        Vertex originVertex = vertices.get(origin);

        originVertex.visit();
        traversalOrder.add(origin);
        vertexStack.push(originVertex);

        while (!vertexStack.isEmpty()) {
            Vertex topVertex = vertexStack.peek();

            Vertex nextNeighbor = topVertex.getUnvisitedNeighbor();

            if (nextNeighbor != null) {
                nextNeighbor.visit();
                traversalOrder.add(nextNeighbor.getName());
                vertexStack.push(nextNeighbor);
            } else {
                vertexStack.pop();
            }
        }
        return traversalOrder;
    } // end getDepthFirstTraversal


    private void findFarthestNodeUsingDijkstra(String originNode) {
        resetVertices();

        Vertex originVertex = vertices.get(originNode);

        if (originVertex == null) {
            System.out.println("Error: Start node can not be found!");
        } else {

            originVertex.setDistance(0);

            PriorityQueue<Vertex> pq = new PriorityQueue<>(new Comparator<Vertex>() {

                public int compare(Vertex v1, Vertex v2) {
                    if (v1.getDistance() < v2.getDistance()) {
                        return -1;
                    } else if (v1.getDistance() > v2.getDistance()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

            pq.add(originVertex);

            long maxDistFound = 0;
            String farthestNodeName = originNode;

            while (!pq.isEmpty()) {
                Vertex current = pq.poll();
                current.visit();


                if (current.getDistance() == Long.MAX_VALUE) {
                    break;
                }

                if (current.getDistance() > maxDistFound) {
                    maxDistFound = current.getDistance();
                    farthestNodeName = current.getName();
                }


                for (Edge edge : current.getEdges()) {
                    Vertex neighbor = edge.getDestination();
                    neighbor.visit();
                    long newDist = current.getDistance() + edge.getWeight();

                    if (newDist < neighbor.getDistance()) {
                        neighbor.setDistance(newDist);
                        pq.add(neighbor);
                    }
                }
            }
            foundDijkstraDistance = maxDistFound;
            foundDijkstraVertexName = farthestNodeName;
            vertices.get(farthestNodeName).unVisit();
        }
    }


    private long calculateDiameter() {
        if (!vertices.isEmpty()) {

            resetVertices();
            maxDiameter = 0;

            for (String startNode : vertices.keySet()) {

                if (vertices.get(startNode).isVisited()) { // this is jump for calculated diameter of disconnected parts.
                    continue;
                }

                long currentDiameter = 0;
                while (true) {
                    findFarthestNodeUsingDijkstra(startNode);// foundDijkstraDistance and foundDijkstraVertexName updates in this function as result of dijkstra.


                    if (foundDijkstraDistance > currentDiameter) {
                        currentDiameter = foundDijkstraDistance;

                    } else if (foundDijkstraDistance == currentDiameter) {
                        //This case means dijkstra came back to last origin vertex
                        break;
                    }// new distance can not be smaller than last distance in a connected graph, but its possible in another part of graph.
                    startNode = foundDijkstraVertexName;
                }
                if (currentDiameter > maxDiameter) { // currentDiameter is max value of connected piece of graph, maxDiameter will be the greatest of all disconnected pieces in the end of vertex for each loop
                    maxDiameter = currentDiameter;
                }
            }

            return maxDiameter; // calculated diameter value

        } else {
            System.out.println("Error: There are no vertices!");
            return -1;
        }
    }


    public Path getMostConfidentPath(String origin, String destination) {
        resetVertices();
        Queue<Vertex> vertexQueue = new LinkedList<>();
        Stack<String> mostConfidentPath = new Stack<>();

        Vertex originVertex = vertices.get(origin);
        Vertex endVertex = vertices.get(destination);

        originVertex.visit();
        vertexQueue.add(originVertex);

        double maxTotalWeight = 0;
        double minEdgeCost = Integer.MAX_VALUE;

        while (!vertexQueue.isEmpty()) {
            Vertex front = vertexQueue.poll();
            Iterator<Vertex> neighbors = front.getNeighborIterator();
            while (neighbors.hasNext()) {
                Vertex nextNeighbor = neighbors.next();
                if (!nextNeighbor.isVisited()) {
                    nextNeighbor.visit();
                    //total edge
                    nextNeighbor.setEdgeCost(front.getEdgeCost() + 1);
                    //total weight of path from start to nextNeighbor
                    nextNeighbor.setWeightCost(front.getWeightCost() + front.getWeight(nextNeighbor.getName()));
                    nextNeighbor.setParent(front);
                    vertexQueue.add(nextNeighbor);
                }
                if (nextNeighbor.getName().equals(endVertex.getName())) {
                    nextNeighbor.unVisit();

                    if(nextNeighbor.getEdgeCost() < minEdgeCost) {
                        minEdgeCost = nextNeighbor.getEdgeCost();
                        mostConfidentPath.clear();
                        mostConfidentPath.push(nextNeighbor.getName());
                        while (nextNeighbor.getParent() != null) {
                            nextNeighbor = nextNeighbor.getParent();
                            mostConfidentPath.push(nextNeighbor.getName());
                        }
                    }
                    else if (nextNeighbor.getEdgeCost() == minEdgeCost && nextNeighbor.getWeightCost() > maxTotalWeight) {
                        maxTotalWeight = nextNeighbor.getWeightCost();
                        mostConfidentPath.clear();
                        mostConfidentPath.push(nextNeighbor.getName());
                        while (nextNeighbor.getParent() != null) {
                            nextNeighbor = nextNeighbor.getParent();
                            mostConfidentPath.push(nextNeighbor.getName());
                        }
                    }
                }
            }
        }
        return new Path(mostConfidentPath, maxTotalWeight);
    }


    public int getWeight(String source, String destination) {
        return vertices.get(source).getWeight(destination);
    }

    public boolean containsVertex(String name) {
        return vertices.containsKey(name);
    }

    public boolean hasEdge(String source, String destination) {
        return vertices.get(source).hasEdge(destination);
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public double getAverageDegree() {
        return averageDegree;
    }


    public double getReciprocity() {
        return (double) reciprocalEdgesCount / numberOfEdges;
    }

    public long getDiameter() {
        return calculateDiameter();
    }

}
