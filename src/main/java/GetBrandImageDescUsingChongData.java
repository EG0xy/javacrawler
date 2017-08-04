import com.baidu.aip.ocr.AipOcr;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * 使用http://chongdata.com/ocr/ 的api来完成文字识别
 *
 * @author holysky.zhao 2017/8/1 13:56
 * @version 1.0.0
 */
public class GetBrandImageDescUsingChongData {

    public static void main(String[] args) {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(GetShopSpringBrandDesc.class.getResource("/品牌故事图.txt").toURI()));
            String line;
            while (StringUtils.isNotEmpty((line = reader.readLine()))) {
                String[] split = line.split(",");
                String brandName = split[0];
                String url = split[1];
                String result = ChongOcr.parseUrl(brandName, url.trim());
                System.out.println(brandName+"|"+result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    static class ChongOcr {
        public static final String APP_ID = "9966857";
        public static final String API_KEY = "I10COEdBPsp0oTWizKsY9Pdt";
        public static final String SECRET_KEY = "IhpyzT0DPQdiLUZ11VAF7KRnzxxLt8KR";

        public static AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        static{
            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
        }

//        curl -sS --connect-timeout 60 --max-time 60 --form file=@/home/app_log/sample10.png --form
//                username=holysky5@gmail.com --form password=zhaotianwu --insecure --form langs[0]=cn_sim --form langs[://ocr.chongdata.com/ocr/dev_api_v2/submit_job.php

        public static String parseUrl(String brandName, String imageUrl) {

            JSONObject response3 = client.basicGeneralUrl(imageUrl, new HashMap<String, String>());
            try {
                JSONArray wordsResult = response3.getJSONArray("words_result");
                StringBuilder sb = new StringBuilder();
                wordsResult.forEach((it)-> sb.append(((JSONObject) it).getString("words")));
                return sb.toString();
            } catch (JSONException e) {
                return "";
            }

        }


    }


}

