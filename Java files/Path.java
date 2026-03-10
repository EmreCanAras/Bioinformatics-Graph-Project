import java.util.Stack;

public class
Path {
    private Stack<String> path;
    private double totalWeight;

    public Path(Stack<String> path, double totalWeight) {
        this.path = path;
        this.totalWeight = totalWeight;
    }

    public Stack<String> getPath() {return path;}
    public double getTotalWeight() {return totalWeight;}
}
