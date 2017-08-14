package bean;

/**
 * @author holysky.zhao 2017/8/14 12:55
 * @version 1.0.0
 */
public class PlatformsBean {
    /**
     * TM : {}
     * JUMEI : {}
     * JD : {}
     */

    private TMBean TM;
    private JUMEIBean JUMEI;
    private JDBean JD;

    public TMBean getTM() {
        return TM;
    }

    public void setTM(TMBean TM) {
        this.TM = TM;
    }

    public JUMEIBean getJUMEI() {
        return JUMEI;
    }

    public void setJUMEI(JUMEIBean JUMEI) {
        this.JUMEI = JUMEI;
    }

    public JDBean getJD() {
        return JD;
    }

    public void setJD(JDBean JD) {
        this.JD = JD;
    }
}
