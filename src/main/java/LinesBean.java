import java.util.List;

/**
 * @author holysky.zhao 2017/8/1 14:22
 * @version 1.0.0
 */
public class LinesBean {
    /**
     * boundingBox : 150,504,42,30
     * words : [{"boundingBox":"150,504,42,30","text":"Stor"}]
     */

    private String boundingBox;
    private java.util.List<WordsBean> words;

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public List<WordsBean> getWords() {
        return words;
    }

    public void setWords(List<WordsBean> words) {
        this.words = words;
    }
}
