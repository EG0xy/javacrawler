import java.util.List;

/**
 * @author holysky.zhao 2017/8/1 14:22
 * @version 1.0.0
 */
public class RegionsBean {
    /**
     * boundingBox : 150,504,42,30
     * lines : [{"boundingBox":"150,504,42,30","words":[{"boundingBox":"150,504,42,30","text":"Stor"}]}]
     */

    private String boundingBox;
    private java.util.List<LinesBean> lines;

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public List<LinesBean> getLines() {
        return lines;
    }

    public void setLines(List<LinesBean> lines) {
        this.lines = lines;
    }
}
