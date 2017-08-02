import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * 获取overstock的category中包含的brand
 * @author holysky.zhao 2017/8/1 20:01
 * @version 1.0.0
 */
public class GetOverstockCategoryBrand {


    public static void main(String[] args) throws URISyntaxException {
        WebDriver driver = Utils.getChromeDriver();
        Path readerPath = Paths.get(GetShopSpringBrandDesc.class.getResource("/overstock_categories.txt").toURI());
        Path writerPath = Paths.get(GetShopSpringBrandDesc.class.getResource("/overstock_brand_result.txt").toURI());

        try (BufferedReader reader = Files.newBufferedReader(readerPath);
             BufferedWriter writer = Files.newBufferedWriter(writerPath);
        ) {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String[] split = line.split("\\|");
                if (split.length == 1) {
                    continue;
                }
                String category = split[0];
                String url = split[1];
                try {
                    driver.get(url);
                    Thread.sleep(2000l);
                    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                    WebElement brandsEl = driver.findElement(By.id("brands"));
                    String text = brandsEl.getText();
                    if (StringUtils.isNotBlank(text)) {
                        for (final String brand : text.split("\n")) {
                            writer.write(brand+"|"+category+"\n");
                        }
                    }
                } catch (Exception e) {
                    System.out.println(line+"未找到原因:"+e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
