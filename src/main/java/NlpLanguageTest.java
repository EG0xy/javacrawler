import com.baidu.aip.nlp.AipNlp;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author holysky.zhao 2017/8/3 19:49
 * @version 1.0.0
 */
public class NlpLanguageTest {
    //设置APPID/AK/SK
    public static final String APP_ID = "9967231";
    public static final String API_KEY = "Lpvm4Go2P3hp1oOyksgi4gtV";
    public static final String SECRET_KEY = "yMWraD3SAI1KY7n3q3DEHcATM7IyhP28";

    public static void main(String[] args) {
        // 初始化一个NLPClient
        AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);


        // 获取两个词的相似度结果
        JSONObject response = client.simnet("LOUIS VUITTON", "test",new HashMap<>());
        System.out.println(response.toString());
    }


}
