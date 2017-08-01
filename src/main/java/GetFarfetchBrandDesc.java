import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * 获取shopSpring 的品牌描述
 *
 * @author holysky.zhao 2017/7/27 11:06
 * @version 1.0.0
 */
public class GetFarfetchBrandDesc {


    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        System.setProperty("webdriver.chrome.driver", "E:\\workspace\\brandcrawler\\src\\main\\resources\\chromedriver.exe");
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
//        Proxy proxy = new Proxy();
//        proxy.setHttpProxy("192.168.1.146:808");
//        capabilities.setCapability("proxy", proxy);
// Add ChromeDriver-specific capabilities through ChromeOptions.
//        ChromeOptions options = new ChromeOptions();
//        options.addExtensions(new File("/path/to/extension.crx"));
//        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        WebDriver driver = new ChromeDriver(capabilities);


//        PhantomJSDriver driver = Utils.getPhantomJSDriver();
        BufferedReader reader = Files.newBufferedReader(Paths.get(GetFarfetchBrandDesc.class.getResource("/farfetchbrand.txt").toURI()));
        String line;
        while (StringUtils.isNotEmpty((line = reader.readLine()))) {
            String[] arr = line.split("\\|");
            driver.get(arr[1]);
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            WebElement element;
            String text="";
            try {
                element= driver.findElement(By.xpath("/html/body/div[1]/main/section/div[5]/div[1]/div/div/div/div/h1"));
                text += element.getText();
                element= driver.findElement(By.xpath("/html/body/div[1]/main/section/div[5]/div[1]/div/div/div/div/p"));
                text += " " + element.getText();
            } catch (Exception e) {
            }
            System.out.println(line+"|"+text);
//
//            String[] contents = element.getText().split("\n");
//            boolean found = false;
//            for (int i = 0; i < contents.length; i++) {
//                if ("About the brand".equalsIgnoreCase(contents[i])) {
//                    found = true;
//                    System.out.println(line + "     " + ((i + 1) < contents.length ? contents[i + 1] : ""));
//                }
//            }
//            if (!found) {
//                System.out.println(line); //未找到,那么在此占行输出
//            }
        }
    }

}
