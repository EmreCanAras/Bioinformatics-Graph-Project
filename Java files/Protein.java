public class Protein {
    private String proteinId;
    private String name;
    private int size;
    private String annotation;

    public Protein(String id, String proteinName, int proteinSize, String annotation) {
        this.proteinId = id;
        this.name = proteinName;
        this.size = proteinSize;
        this.annotation = annotation;
    }

    public String getId() {return proteinId;}
    public String getName() {return name;}
    public int getSize() {return size;}
    public String getAnnotation() {return annotation;}
    public void setAnnotation(String annotation) {this.annotation = annotation;}
    public void setName(String name) {this.name = name;}
    public void setSize(int size) {this.size = size;}
    public void setId(String id) {this.proteinId = id;}
}
