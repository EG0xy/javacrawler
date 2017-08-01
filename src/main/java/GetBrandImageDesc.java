import com.google.gson.Gson;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 品牌故事图的图片转换
 *
 * @author holysky.zhao 2017/8/1 13:56
 * @version 1.0.0
 */
public class GetBrandImageDesc {

    public static void main(String[] args) {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(GetShopSpringBrandDesc.class.getResource("/品牌故事图.txt").toURI()));
            String line;
            while (StringUtils.isNotEmpty((line = reader.readLine()))) {

                String[] split = line.split(",");
                String brandName = split[0];
                String url = split[1];
                String brandDesc = YoudaoOCR.parseUrl(url);
                System.out.println(new StringBuilder().append(brandName).append(",").append(url).append(",").append(brandDesc).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static class YoudaoOCR{
        private static Gson gson = new Gson();
        private static String url = "http://openapi.youdao.com/ocrapi";
        static String appKey = "1e33df62e2fc9d1b";
        static String appSecret = "m4bbsrz2Z5hvsPm43xghRVwB8ECeOgZ3";
        static String detectType = "10011";
        static String imageType = "1";
        static String langType = "zh-en";
        static String docType = "json";

        public static String parseUrl(String imageUrl) {
            try {
                Map<String, String> map = new HashMap<>();
                String salt = String.valueOf(System.currentTimeMillis());
                String img = getImageStr(imageUrl.replace("https","http").trim());
                map.put("appKey", appKey);
                map.put("img", img);
                map.put("detectType", detectType);
                map.put("imageType", imageType);
                map.put("langType", langType);
                map.put("salt", salt);
                map.put("docType", docType);
                String sign = md5(appKey + img + salt + appSecret);
                map.put("sign", sign);
                return requestOCRForHttp(url, map);
            } catch (Exception e) {
                return "";
            }
        }

        public static String requestOCRForHttp(String url, Map<String, String> requestParams) throws Exception {
            String result;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            /**HttpPost*/
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("appKey", requestParams.get("appKey")));
            params.add(new BasicNameValuePair("img", requestParams.get("img")));
            params.add(new BasicNameValuePair("detectType", requestParams.get("detectType")));
            params.add(new BasicNameValuePair("imageType", requestParams.get("imageType")));
            params.add(new BasicNameValuePair("langType", requestParams.get("langType")));
            params.add(new BasicNameValuePair("salt", requestParams.get("salt")));
            params.add(new BasicNameValuePair("sign", requestParams.get("sign")));
            params.add(new BasicNameValuePair("docType", requestParams.get("docType")));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            /**HttpResponse*/
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            try {
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity, "utf-8");
                EntityUtils.consume(httpEntity);
                OcrResult ocrResult = gson.fromJson(result, OcrResult.class);
                if (ocrResult != null && ocrResult.getErrorCode().equalsIgnoreCase("0")) {
                    StringBuilder sb = new StringBuilder();
                    for (final RegionsBean regionsBean : ocrResult.getResult().getRegions()) {
                        for (final LinesBean linesBean : regionsBean.getLines()) {
                            for (final WordsBean wordsBean : linesBean.getWords()) {
                                sb.append(wordsBean.getText()).append(" ");
                            }
                        }
                    }
                    return sb.toString();
                }
            } finally {
                try {
                    if (httpResponse != null) {
                        httpResponse.close();
                    }
                } catch (IOException e) {
                }
            }
            return "";
        }

        /**
         * 获得图片的Base64编码
         *
         * @param imgFile
         * @return
         */

        public static String getImageStr(String imgFile) throws IOException {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
            byte[] data = org.apache.commons.io.IOUtils.toByteArray(URI.create(imgFile));
            //对字节数组Base64编码
            return Base64.encode(data);//返回Base64编码过的字节数组字符串
        }

        /**
         * 生成32位MD5摘要
         *
         * @param string
         * @return
         */
        public static String md5(String string) {
            if (string == null) {
                return null;
            }
            char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'A', 'B', 'C', 'D', 'E', 'F'};
            byte[] btInput = string.getBytes();
            try {
                /** 获得MD5摘要算法的 MessageDigest 对象 */
                MessageDigest mdInst = MessageDigest.getInstance("MD5");
                /** 使用指定的字节更新摘要 */
                mdInst.update(btInput);
                /** 获得密文 */
                byte[] md = mdInst.digest();
                /** 把密文转换成十六进制的字符串形式 */
                int j = md.length;
                char str[] = new char[j * 2];
                int k = 0;
                for (byte byte0 : md) {
                    str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                    str[k++] = hexDigits[byte0 & 0xf];
                }
                return new String(str);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }
    }


    public static class OcrResult{

        /**
         * errorCode : 0
         * Result : {"orientation":"Up","regions":[{"boundingBox":"150,504,42,30","lines":[{"boundingBox":"150,504,42,30","words":[{"boundingBox":"150,504,42,30","text":"Stor"}]}]}],"textAngle":18.896469,"language":"en"}
         */

        private String errorCode;
        private ResultBean Result;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public ResultBean getResult() {
            return Result;
        }

        public void setResult(ResultBean Result) {
            this.Result = Result;
        }
    }
}

