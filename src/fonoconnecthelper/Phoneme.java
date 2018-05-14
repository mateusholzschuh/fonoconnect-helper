/*
 * @FonoConnectHelper :: Phoneme
 * version: b11.4.18
 */
package fonoconnecthelper;

/**
 *
 * @author Mateus
 */
public class Phoneme {

    private Integer id;
    private String  name;
    private String  example;

    public Phoneme() {
    }

    public Phoneme(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Phoneme(Integer id, String name, String example) {
        this.id = id;
        this.name = name;
        this.example = example;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

}
