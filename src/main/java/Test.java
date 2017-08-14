import bean.BrandBean;
import com.alibaba.fastjson.JSON;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * @author holysky.zhao 2017/8/4 14:10
 * @version 1.0.0
 */
public class Test {


    interface FindLocation {
        String getLocationIPossible(String text);
    }

    static class ZhFindLocation implements FindLocation {
        static Segment segment = HanLP.newSegment().enablePlaceRecognize(true);

        @Override
        public String getLocationIPossible(final String text) {
            if (text == null || text.length() == 0) {
                return null;
            }
            List<Term> termList = segment.seg(text);
            return termList.stream().filter((it) -> it.nature == Nature.nx || it.nature == Nature.nsf)
                           .map((it) -> it.word)
                           .findFirst().orElseGet(null);
        }
    }

    static class EnFindLocation implements FindLocation {
        final static String serializedClassifier = "E:/workspace/brandcrawler/lib/stanford-ner-2017-06-09/classifiers/english.all.3class.distsim.crf.ser.gz";
        final static AbstractSequenceClassifier<CoreLabel> classifier;

        static {
            AbstractSequenceClassifier<CoreLabel> tempClassifier;

            try {
                tempClassifier = CRFClassifier.getClassifier(serializedClassifier);
            } catch (IOException | ClassNotFoundException e) {
                tempClassifier = null;
            }
            classifier = tempClassifier;
        }


        @Override
        public String getLocationIPossible(final String text) {
            List<List<CoreLabel>> out = classifier.classify(text);
            Iterator wordIter = out.iterator();

            List sentence;
            Iterator var7;
            CoreLabel word;
            while (wordIter.hasNext()) {
                sentence = (List) wordIter.next();
                var7 = sentence.iterator();

                while (var7.hasNext()) {
                    word = (CoreLabel) var7.next();
                    if ("LOCATION".equals(word.get(CoreAnnotations.AnswerAnnotation.class))) {
                        return word.word();
                    }
                }
            }
            return null;
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException {
        String content = org.apache.commons.io.IOUtils.toString(Files.newBufferedReader(Paths.get(GetShopSpringBrandDesc.class.getResource("/result/branddata_regular.json").toURI())));
        List<BrandBean> brandBeans = JSON.parseArray(content, BrandBean.class);
        FindLocation zhFindLocation = new ZhFindLocation();
        FindLocation enFindLocation = new EnFindLocation();


        int findCount = 0;
        for (final BrandBean brandBean : brandBeans) {
            String location=null;
            if (brandBean.getDescCn() != null) {
                location = zhFindLocation.getLocationIPossible(brandBean.getDescCn());
            } else if (brandBean.getDescEn() != null) {
                location = enFindLocation.getLocationIPossible(brandBean.getDescEn());
            }

            if (location != null) {
                findCount++;
                String updateQuery = String.format("db.md_mt_brand.updateOne({\"name\":\"%s\"},{$set: {birthPlace:\"%s\"}});",
                        brandBean.getName(),
                        location);
                System.out.println(updateQuery);
            }
        }
        System.out.println("the count of brands with birthPlace:"+findCount);
    }
}