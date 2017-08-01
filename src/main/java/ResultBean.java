import java.util.List;

/**
 * @author holysky.zhao 2017/8/1 14:22
 * @version 1.0.0
 */
public class ResultBean {
    /**
     * orientation : Up
     * regions : [{"boundingBox":"150,504,42,30","lines":[{"boundingBox":"150,504,42,30","words":[{"boundingBox":"150,504,42,30","text":"Stor"}]}]}]
     * textAngle : 18.896469
     * language : en
     */

    private String orientation;
    private double textAngle;
    private String language;
    private java.util.List<RegionsBean> regions;

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public double getTextAngle() {
        return textAngle;
    }

    public void setTextAngle(double textAngle) {
        this.textAngle = textAngle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<RegionsBean> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionsBean> regions) {
        this.regions = regions;
    }
}
