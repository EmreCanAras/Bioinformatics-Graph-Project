import java.io.*;
import java.text.BreakIterator;
import java.util.*;

public class UserInterface {
    private DirectedGraph proteinLinksGraph = new DirectedGraph();
    private HashMap<String, Protein> proteinsInfo = new HashMap();
    private int confidenceScore;
    private Scanner scanner = new Scanner(System.in);
    private static int count = 0;

    public UserInterface() {
        System.out.println("Welcome to the protein interaction network!\n");
        double startTime = System.currentTimeMillis();
        loadGraph();
        double endTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (endTime - startTime) + " ms");
        menu();


    }

    public void loadGraph() {
        loadProteinsToAnArray();
        System.out.print("Please pick a confidence score threshold: \n" +
                "(Interactions with scores smaller than the specified threshold will be eliminated)\n" +
                "1 - Interactions at highest confidence (score >= 0.9) \n" +
                "2 - Interactions at high confidence or better (score >= 0.7)\n" +
                "3 - Interactions at medium confidence or better (score >= 0.4)\n" +
                "4 - Low confidence links (score < 0.4)\n" +
                "Please type 1, 2, 3 or 4: ");
        while (true) {
            String choice = scanner.nextLine();
            if (choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4")) {
                confidenceScore = Integer.parseInt(choice);
                break;
            }
            System.out.print("Please enter a valid input: ");
        }

        try {
            FileReader fr = new FileReader("src/9606.protein.links.v12.0.txt");
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            if (confidenceScore == 4) { //we need to find scores less than 400
                while ((line = br.readLine()) != null) {
                    String[] lineSplit = line.split(" "); //protein1 protein2 score
                    if (Integer.parseInt(lineSplit[2]) < 400) {
                        proteinLinksGraph.addEdge(lineSplit[0], lineSplit[1], Integer.parseInt(lineSplit[2]));
                    }
                    count++;
                    if(count%10000 == 0) System.out.print("\rLOADING...  " + count + " lines checked");
                }
            } else { //find scores higher than the desired threshold
                if (confidenceScore == 1) {
                    confidenceScore = 900;
                }
                if (confidenceScore == 2) {
                    confidenceScore = 700;
                }
                if (confidenceScore == 3) {
                    confidenceScore = 400;
                }

                while ((line = br.readLine()) != null) {
                    String[] lineSplit = line.split(" "); //protein1 protein2 score
                    if (Integer.parseInt(lineSplit[2]) >= confidenceScore) {
                        proteinLinksGraph.addEdge(lineSplit[0], lineSplit[1], Integer.parseInt(lineSplit[2]));
                    }
                    count++;
                    if(count%10000 == 0) System.out.print("\rLOADING... " + count + " lines checked");
                }
            }
            System.out.print("\r" + count + " lines checked");
            System.out.println("\nLoad finished!\n");
        } catch (Exception e) {
            System.out.println("An error occured when reading file");
        }

    }

    public void basicGraphMetrics() {
        System.out.println("Vertex Count: " + proteinLinksGraph.size());
        System.out.println("Edge Count: " + proteinLinksGraph.getNumberOfEdges());
        System.out.println("Average Degree: " + (long) (proteinLinksGraph.getAverageDegree() * 10) / 10.0);
        System.out.println("Reciprocity: %" + proteinLinksGraph.getReciprocity() * 100);
        System.out.print("\rDiameter is calculating... ");
        System.out.println("\rDiameter: " + proteinLinksGraph.getDiameter() + "                   ");
    }

    private void loadProteinsToAnArray() {

        try (FileReader fr = new FileReader("src/9606.protein.info.v12.0.txt")) {
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split("\t");
                proteinsInfo.put(lineSplit[0], new Protein(lineSplit[0], lineSplit[1], Integer.parseInt(lineSplit[2]), lineSplit[3]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void interactionCheck() {
        System.out.print("Please enter the ID's of the proteins you would like to check:\nProtein 1: ");
        String protein1 = scanner.nextLine();
        System.out.print("Protein 2: ");
        String protein2 = scanner.nextLine();
        System.out.println();

        if (protein1.equals(protein2)) {
            System.out.println("Protein 1 and Protein 2 are the same");
        } else if (proteinLinksGraph.containsVertex(protein1) && proteinLinksGraph.containsVertex(protein2)) {
            boolean direction1 = proteinLinksGraph.hasEdge(protein1, protein2);
            boolean direction2 = proteinLinksGraph.hasEdge(protein2, protein1);
            if (direction1 || direction2) {
                System.out.println("Given proteins have interaction! The interaction(s):");
                if (direction1) {
                    System.out.println(protein1 + " -> " + protein2 + " Weight:" + proteinLinksGraph.getWeight(protein1, protein2));
                }
                if (direction2) {
                    System.out.println(protein2 + " -> " + protein1 + " Weight:" + proteinLinksGraph.getWeight(protein2, protein1));
                }
            } else {
                System.out.println("Given proteins have no direct interaction.");
            }
        } else {
            System.out.println("Given proteins does not exist.");
        }
    }

    private void findMostConfidentPath() {
        System.out.print("Which two proteins that you want to find most confident path between?\nProtein 1: ");
        String protein1 = scanner.nextLine().trim();
        System.out.print("Protein 2: ");
        String protein2 = scanner.nextLine().trim();

        if (!proteinLinksGraph.containsVertex(protein1) || !proteinLinksGraph.containsVertex(protein2))
            System.out.println("One of the proteins does not exist");
        else {
            if (protein1.equals(protein2)) {
                System.out.println("Protein 1 and Protein 2 are the same");
            } else if (proteinLinksGraph.containsVertex(protein1) && proteinLinksGraph.containsVertex(protein2)) {
                Path path = proteinLinksGraph.getMostConfidentPath(protein1, protein2);
                Stack<String> pathStack = path.getPath();

                if(pathStack.empty()) System.out.println("There is no path between these proteins. They are in different not connected graphs.");
                else {
                    int count = 1;
                    System.out.println("The most confident path with cost " + path.getTotalWeight() + " :");
                    while (!pathStack.empty()) {
                        System.out.println(count++ + ".) " + pathStack.pop());
                    }
                }
            }
        }
    }

    private void searchByProteinID(){
        System.out.print("Which protein ID do you want to search for?\nProtein ID:");
        String proteinID = scanner.nextLine();
        if(proteinsInfo.containsKey(proteinID)){
            Protein protein = proteinsInfo.get(proteinID);
            System.out.println("Preferred name of the protein: " + protein.getName());
            System.out.println("Size of the protein: " + protein.getSize());
            System.out.println(protein.getAnnotation());
        }
        else System.out.println("Protein with given ID does not exist");
    }

    private void BFSTraversal(){
        System.out.print("Which protein ID do you want to start for BFS traverse?\nProtein ID:");
        String origin = scanner.nextLine();
        Queue<String> queue = proteinLinksGraph.getBreadthFirstTraversal(origin);
        int count = 1;
        while(!queue.isEmpty()){
            System.out.println(count++ + ")" + queue.poll());
        }
    }

    private void DFSTraversal(){
        System.out.print("Which protein ID do you want to start for DFS traverse?\nProtein ID:");
        String origin = scanner.nextLine();
        Queue<String> queue = proteinLinksGraph.getDepthFirstTraversal(origin);
        int count = 1;
        while(!queue.isEmpty()){
            System.out.println(count++ + ")" + queue.poll());
        }
    }

    private void menu() {
        boolean exit = false;
        while (!exit) {
            String choice = "";

            System.out.println();
            System.out.println("You can choose one of the following options:");
            System.out.println("1. Search for protein by ID (type '1')");
            System.out.println("2. Check interaction between two proteins (type '2')");
            System.out.println("3. Find most confident path between two proteins (type '3')");
            System.out.println("4. Show basic graph metrics (type '4')");
            System.out.println("5. Breath-First Traverse by specifying the origin protein (type '5')");
            System.out.println("6. Depth-First Traverse by specifying the origin protein (type '6')");
            System.out.println("7. Exit (type '7')");
            System.out.print("Please type your choice: ");
            while (true) {
                try {
                    choice = scanner.nextLine();
                } catch (Exception e) {
                    continue;
                }
                if (choice.equals("1") || choice.equals("2") || choice.equals("3") || choice.equals("4")
                        || choice.equals("5") || choice.equals("6") || choice.equals("7")) break;
                else {
                    System.out.println("Please enter a valid input!");
                    System.out.print("Your choice: ");
                }
            }

            switch (choice) {
                case "1":
                    searchByProteinID();
                    break;
                case "2":
                    interactionCheck();
                    break;
                case "3":
                    findMostConfidentPath();
                    break;
                case "4":
                    basicGraphMetrics();
                    break;
                case "5":
                    BFSTraversal();
                    break;
                case "6":
                    DFSTraversal();
                    break;
                default:
                    exit = true;
                    break;
            }
        }
        System.out.println("Goodbye!");
    }
}
