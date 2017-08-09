import org.apache.commons.lang3.StringUtils;
import org.json.JSONWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author holysky.zhao 2017/8/4 13:01
 * @version 1.0.0
 */
public class MergeBrandData {
    public static final Map<Character, Character> VOWEL_MAP = new HashMap<>();

    static {
        VOWEL_MAP.put('Ā','A');
        VOWEL_MAP.put('Á','A');
        VOWEL_MAP.put('Ǎ','A');
        VOWEL_MAP.put('À','A');
        VOWEL_MAP.put('À','A');
        VOWEL_MAP.put('Ō','O');
        VOWEL_MAP.put('Ó','O');
        VOWEL_MAP.put('Ǒ','O');
        VOWEL_MAP.put('Ò','O');
        VOWEL_MAP.put('Ê','E');
        VOWEL_MAP.put('Ē','E');
        VOWEL_MAP.put('É','E');
        VOWEL_MAP.put('Ě','E');
        VOWEL_MAP.put('È','E');
        VOWEL_MAP.put('Ī','I');
        VOWEL_MAP.put('Í','I');
        VOWEL_MAP.put('Ǐ','I');
        VOWEL_MAP.put('Ì','I');
        VOWEL_MAP.put('Ï', 'I');
        VOWEL_MAP.put('Ū','U');
        VOWEL_MAP.put('Ú','U');
        VOWEL_MAP.put('Ǔ','U');
        VOWEL_MAP.put('Ù','U');
        VOWEL_MAP.put('Ǖ','U');
        VOWEL_MAP.put('Ǘ','U');
        VOWEL_MAP.put('Ǚ','U');
        VOWEL_MAP.put('Ǜ','U');
    }

    public static void main(String[] args) {

        String[] fileNames = new String[]{
               "allbrand.txt"
        };

        Map<String, List<Brand>> brandGroup = Arrays.stream(fileNames).map((it) -> new BrandFile(resourcePath(it)))
                                                    .map(BrandFile::getBrandNames).flatMap((it) -> it.stream())
                                                    .sorted()
                                                    .collect(Collectors.groupingBy(Brand::getFirstWord));
        final AtomicInteger brandId = new AtomicInteger(1);
        brandGroup.keySet().stream().sorted(String::compareToIgnoreCase)
                  .forEach((key) -> {
                      final Set<Brand> val = new HashSet<>(brandGroup.get(key));
                      List<String> brands = val.stream().map(Brand::getBrandName).sorted().collect(Collectors.toList());
                      int parentBrandId = brandId.get();
                      for (int i = 0; i < brands.size(); i++) {
                          int currentBrandId = brandId.getAndIncrement();
                          StringWriter sw = new StringWriter();
                          JSONWriter writer = new JSONWriter(sw);
                          writer.object().key("brandId").value(currentBrandId).key("brandName")
                                .value(brands.get(i)).key("brandParentId").value(i == 0 ? 0 : parentBrandId).endObject();
                          System.out.println(sw.append(",").toString());
                      }
                  });
    }

    private static Path resourcePath(final String resourceName) {
        try {
            return Paths.get(MergeBrandData.class.getResource(resourceName).toURI());
        } catch (URISyntaxException e) {
            System.out.println("文件路径异常:" + e.getMessage());
            return null;
        }
    }


    public static class BrandFile {
        Path filePath;

        public BrandFile(final Path filePath) {
            this.filePath = filePath;
        }

        private Set<Brand> brandNames;

        public Set<Brand> getBrandNames() {
            if (brandNames == null) {
                brandNames = new HashSet<>();
                try (BufferedReader reader = Files.newBufferedReader(filePath);) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (StringUtils.isBlank(line)) {
                            continue;
                        }
                        String[] split = line.split("\\|");
                        if (split.length >= 1) {
                            brandNames.add(new Brand(split[0]));
                        } else {
                            System.out.println("error brandLine:" + line);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("error file" + filePath);
                    e.printStackTrace();
                }
            }
            return brandNames;

        }
    }


    public static class Brand implements Comparable {

        /**
         * 存放所有品牌的变量
         */
        public static Set<String> ALL_BRANDS=new HashSet<>(20000);

        private String firstWord;

        private String brandName;
        private String clearBrandName;  //trim,uppercase and remove vower之后的字符串


        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Brand brand = (Brand) o;
            return this.clearBrandName.equalsIgnoreCase(brand.clearBrandName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.clearBrandName);
        }

        public Brand(final String brandName) {
            this.brandName = brandName.trim();
            String brandNameUpper = this.brandName.toUpperCase();

            //处理元音
            char[] a = new char[this.brandName.length()];
            for (int i = 0; i < brandNameUpper.length(); i++) {
                a[i]  = VOWEL_MAP.getOrDefault(brandNameUpper.charAt(i), brandNameUpper.charAt(i));
            }
            this.clearBrandName = new String(a);
            ALL_BRANDS.add(this.clearBrandName);

        }

        /**
         * 返回的永远是品牌库中存在的数据
         * 试想一个品牌A B C 先检查A是否在品牌库中,如果存在则返回,否则尝试A B再尝试A B C,直到返回完整的单词(肯定存在于品牌库中)
         *
         *
         * @return
         */
        public String getFirstWord() {
            if (this.firstWord == null) {
                String[] brandPieces = this.clearBrandName  .split(" ");
                StringBuilder sb = new StringBuilder(brandPieces[0]);
                if (ALL_BRANDS.contains(sb.toString())) {
                    this.firstWord = sb.toString();
                } else {
                    for (int i = 1; i < brandPieces.length; i++) {
                        sb.append(" ").append(brandPieces[i]);
                        if (ALL_BRANDS.contains(sb.toString())) {
                            this.firstWord = sb.toString();
                            break;
                        }
                    }
                }

            }
            return this.firstWord;
        }

        public void setFirstWord(final String firstWord) {
            this.firstWord = firstWord;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(final String brandName) {
            this.brandName = brandName;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Brand{");
            sb.append("brandName='").append(brandName).append('\'');
            sb.append('}');
            return sb.toString();
        }


        @Override
        public int compareTo(final Object o) {
            String firstWord = ((Brand) o).getFirstWord();
            return this.getFirstWord().compareToIgnoreCase(firstWord);
        }
    }


}
