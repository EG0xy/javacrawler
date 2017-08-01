import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author holysky.zhao 2017/7/28 14:13
 * @version 1.0.0
 */
public class MergeMain {

    public static void main(String[] args) throws URISyntaxException, IOException {
        Map<String, String> brandDescMap = new HashMap<>(5000);

        try (BufferedReader readerOfSpringBrand = Files.newBufferedReader(Paths.get(GetShopSpringBrandDesc.class.getResource("/springbrandwithdesc1.txt").toURI()));
             BufferedReader readerOfGiltBrand = Files.newBufferedReader(Paths.get(GetShopSpringBrandDesc.class.getResource("/giltbrand.txt").toURI()));
             BufferedReader readerofFarfetchBrand = Files.newBufferedReader(Paths.get(GetShopSpringBrandDesc.class.getResource("/farfetchbrand.txt").toURI()));
             FileWriter fw = new FileWriter(new File("E:\\workspace\\brandcrawler\\src\\main\\resources\\resultbrand.txt"))
        ) {
            String line;
            while ((line = readerOfGiltBrand.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    brandDescMap.put(StringUtils.trim(line),"");
                }
            }
            while ((line = readerOfSpringBrand.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] split = line.split("\\|");
                    String desc = split.length > 1 ? StringUtils.trim(split[1]) : "";
                    brandDescMap.put(StringUtils.trim(split[0]), desc);
                }
            }

            while ((line = readerofFarfetchBrand.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    String[] split = line.split("\\|");
                    brandDescMap.computeIfPresent(StringUtils.trim(split[0]),(key, oldVal)->{
                        if(!"".equals(oldVal)) return oldVal+'|'+split[2];  //品牌,英文,中文
                        return "无|"+split[2];
                    });
                }
            }
            brandDescMap.keySet().stream().sorted(String::compareToIgnoreCase).forEach((it)->{
                try {
                    String val = brandDescMap.get(it);
                    fw.write(it+(StringUtils.isNotBlank(val)?"|"+val:"")+"\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
