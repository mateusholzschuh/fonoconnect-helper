/*
 * @FonoConnectHelper :: Word
 * version: b11.4.18
 */
package fonoconnecthelper;

/**
 *
 * @author Mateus
 */
public class Word {

    private Integer id;
    private String  name;

    public Word() {
    }

    public Word(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
