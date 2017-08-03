import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 使用http://chongdata.com/ocr/ 的api来完成文字识别
 *
 * @author holysky.zhao 2017/8/1 13:56
 * @version 1.0.0
 */
public class GetBrandImageDescUsingChongData {

    public static void main(String[] args) {
        try {
            Stream<Path> walk = Files.walk(Paths.get("E:/workspace/brandcrawler/src/main/resources/brands/"));
            walk.forEach((it) -> {
                File file = it.toFile();
                String brandName = file.getName();
                String brandDesc = ChongOcr.parseUrl(brandName, file);
                System.out.println(brandName + "|" + brandDesc);
            });

            BufferedReader reader = Files.newBufferedReader(Paths.get(GetShopSpringBrandDesc.class.getResource("/品牌故事图.txt").toURI()));
            String line;
            while (StringUtils.isNotEmpty((line = reader.readLine()))) {
                String[] split = line.split(",");
                String brandName = split[0];
                String url = split[1];
                writeFile(brandName, url.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得图片的Base64编码
     *
     * @param imgFile
     * @return
     */

    public static void writeFile(String brandName, String imgFile) throws IOException {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理

        //对字节数组Base64编码
        try {
            byte[] data = org.apache.commons.io.IOUtils.toByteArray(URI.create(imgFile));
            Path filePath = Paths.get("E:/workspace/brandcrawler/src/main/resources/brands/" + brandName + ".jpg");
            if (!Files.exists(filePath))
                Files.createFile(filePath);

            OutputStream output = Files.newOutputStream(filePath);
            IOUtils.write(data, output);
            System.out.println("文件写入成功:" + imgFile);
        } catch (Exception e) {
            System.out.println("文件写入错误:" + imgFile);
        }
    }

    static class ChongOcr {
//        private static Gson gson = new Gson();
//        private static String url = "http://openapi.youdao.com/ocrapi";
//        static String appKey = "1e33df62e2fc9d1b";
//        static String appSecret = "m4bbsrz2Z5hvsPm43xghRVwB8ECeOgZ3";
//        static String detectType = "10011";
//        static String imageType = "1";
//        static String langType = "zh-en";
//        static String docType = "json";


//        curl -sS --connect-timeout 60 --max-time 60 --form file=@/home/app_log/sample10.png --form
//                username=holysky5@gmail.com --form password=zhaotianwu --insecure --form langs[0]=cn_sim --form langs[://ocr.chongdata.com/ocr/dev_api_v2/submit_job.php

        public static String parseUrl(String brandName, File imageFile) {

            List<NameValuePair> formPairs = new ArrayList<>();
            formPairs.add(new BasicNameValuePair("username", "holysky5@gmail.com"));
            formPairs.add(new BasicNameValuePair("password", "zhaotianwu"));
            formPairs.add(new BasicNameValuePair("langs[0]", "cn_sim"));
            formPairs.add(new BasicNameValuePair("langs[1]", "en"));

            try {
                Request request = Request.Post("https://ocr.chongdata.com/ocr/dev_api_v2/submit_job.php")
                                         .bodyFile(imageFile, ContentType.DEFAULT_BINARY)
                                         .bodyForm(formPairs);
                return executor.execute(request).returnContent().asString();
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        private static HttpClient client;
        private static Executor executor;
        static {
            try {
                client=HttpClients
                        .custom()
                        .setHostnameVerifier(new AllowAllHostnameVerifier())
                        .setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                                return true;
                            }
                        }).build())
                        .build();
                executor = Executor.newInstance(client);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }


    }


}

