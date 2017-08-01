import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author holysky.zhao 2017/7/26 13:40
 * @version 1.0.0
 */
public class App {

    static ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);


    /**
     * 解析页面,返回页面
     *
     * @param driver
     * @param prefix           品牌前缀a~z
     * @param pageNo,页号,从第一页开始
     * @return 本页面包含的品牌集合, 如果pageNo是1, 返回最大页数, 否则永远返回1
     */
    public static Pair<Set<String>, Integer> resovePage(PhantomJSDriver driver, char prefix, Integer pageNo) throws InterruptedException {
        driver.get("https://www.gilt.com/brands/" + prefix);
        sleep(2);
        WebElement element = driver.findElement(By.tagName("html"));
        List<WebElement> allLink = element.findElements(By.className("directory-item-link"));
        System.out.println("读取品牌前缀:" + prefix + ",第" + pageNo + "页,读取了品牌" + allLink.size());
        if (pageNo == 1) {
            resloveBrandName(allLink);
            String lastPageNoStr = element.findElements(By.className("listing")).get(1).findElement(By.xpath("a[last()]")).getText();
            Integer lastPageNo = Integer.valueOf(lastPageNoStr);
            return Pair.of(resloveBrandName(allLink), lastPageNo);
        }
        return Pair.of(resloveBrandName(allLink), 1);  //这里是写死的值,因为用不上
    }


    public static void main(String[] args) {
        PhantomJSDriver driver = Utils.getPhantomJSDriver();

        try (FileWriter fw = new FileWriter(new File("D:\\brands.txt"))) {
            // 让浏览器访问空间主页
            for (int i = 1; i < 26; i++) {
                Set<String> brandNames = new HashSet<>();
                try {
                    char prefix = (char) ('a' + i);
                    Pair<Set<String>, Integer> resultPair = resovePage(driver, (char) prefix, 1);
                    brandNames.addAll(resultPair.getKey());
                    if (resultPair.getValue() >= 2) {
                        for (int j = 2; j <= resultPair.getValue(); j++) {
                            Pair<Set<String>, Integer> resultPair1 = resovePage(driver, prefix, j);
                            brandNames.addAll(resultPair1.getKey());
                        }
                    }
                    System.out.println(prefix+"brand list:");
                    brandNames.stream().sorted().forEach((brandName)->{
                        try {
                            fw.write(brandName + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//            es.shutdown();
//            es.awaitTermination(1, TimeUnit.DAYS);
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            Thread.sleep(1000L);
//            WebElement pwdLoginbutton = driver.findElement(By.id("bottom_qlogin")).findElement(By.id("switcher_plogin"));
//            pwdLoginbutton.click();
//            //获取账号密码输入框的节点
//            WebElement userNameElement = driver.findElement(By.id("u"));
//            WebElement pwdElement = driver.findElement(By.id("p"));
//            userNameElement.sendKeys("2437801435");
//            pwdElement.sendKeys("lyt123456");
//
//            //获取登录按钮
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            WebElement loginButton = driver.findElement(By.id("login_button"));
//            loginButton.click();
//            //设置线程休眠时间等待页面加载完成
//            Thread.sleep(1000L);
//
//            //获取新页面窗口句柄并跳转，模拟登陆完成
//            String windowHandle = driver.getWindowHandle();
//            driver.switchTo().window(windowHandle);
//
//            //设置说说详情数据页面的加载时间并跳转
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            driver.get("http://ic2.s21.qzone.qq.com/cgi-bin/feeds/feeds_html_module?i_uin=564227332&i_login_uin=2437801435&mode=4&previewV8=1&style=25&version=8&needDelOpr=true&transparence=true&hideExtend=false&showcount=5&MORE_FEEDS_CGI=http%3A%2F%2Fic2.s21.qzone.qq.com%2Fcgi-bin%2Ffeeds%2Ffeeds_html_act_all&refer=2&paramstring=os-winxp|100");
//
//            //获取要抓取的元素,并设置等待时间,超出抛异常
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            //设置设置线程休眠时间等待页面加载完成
//            Thread.sleep(1000L);
//            WebElement firstTalk = driver.findElement(By.ByXPath.xpath("/html/body/div[1]/div[1]/ul/li[1]/div[2]/div/div[1]"));
//            WebElement talkTime = driver.findElement(By.ByXPath.xpath("/html/body/div[1]/div[1]/ul/li[1]/div[1]/div[2]/div[2]/span[1]"));
//            String content = firstTalk.getText();
//            String time = talkTime.getText();
//            System.out.println("content="+content+"========="+"time="+time);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            //关闭并退出浏览器
            driver.close();
            driver.quit();

        }

    }



    private static Set<String> resloveBrandName(final List<WebElement> allLink) {
        Set<String> result = new HashSet<>();
        for (final WebElement el : allLink) {
            if (!"Back to brands directory".equalsIgnoreCase(el.getText())) {
                result.add(el.getText());
            }
        }
        return result;

    }

    private static void sleep(final int i) throws InterruptedException {
        TimeUnit.SECONDS.sleep((long) i);
    }
}
