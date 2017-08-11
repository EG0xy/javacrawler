import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author holysky.zhao 2017/8/4 14:10
 * @version 1.0.0
 */
public class Test {

    static Segment segment = HanLP.newSegment().enablePlaceRecognize(true);

    public static List<String> getCountry(String text) {
        if (text == null || text.length() == 0) {
            return new ArrayList<>();
        }
        List<Term> termList = segment.seg(text);
        return termList.stream().filter((it) -> it.nature == Nature.nx || it.nature == Nature.nsf)
                       .map((it) -> it.word)
                       .collect(Collectors.toList());

    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {


        String text = "当Italo Fontana无意间发现他的爷爷大约在1942年专为意大利海军设计的手表草图的时候，他便意识到自己发现了什么特别的东西。那些设计要满足海军的高品质标准并需要遵守特殊的技术要求，确保在不同情况下最大的可见度和可靠性。由于当时的政治环境所限，这些设计从没被真正地投入生产。但是穿越过时间，他们成了U-Boat品牌背后的灵感来源。";

        List<String> country = getCountry(text);
        System.out.println(country);


        String serializedClassifier = "E:/workspace/brandcrawler/lib/stanford-ner-2017-06-09/classifiers/english.all.3class.distsim.crf.ser.gz";
        if (args.length > 0) {
            serializedClassifier = args[0];
        }

        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
        if (args.length > 1) {
            String fileContents = IOUtils.slurpFile(args[1]);
            List<List<CoreLabel>> out = classifier.classify(fileContents);
            Iterator var5 = out.iterator();

            List sentence;
            Iterator var7;
            CoreLabel word;
            while (var5.hasNext()) {
                sentence = (List) var5.next();
                var7 = sentence.iterator();

                while (var7.hasNext()) {
                    word = (CoreLabel) var7.next();
                    System.out.print(word.word() + '/' + (String) word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
                }

                System.out.println();
            }

            System.out.println("---");
            out = classifier.classifyFile(args[1]);
            var5 = out.iterator();

            while (var5.hasNext()) {
                sentence = (List) var5.next();
                var7 = sentence.iterator();

                while (var7.hasNext()) {
                    word = (CoreLabel) var7.next();
                    System.out.print(word.word() + '/' + (String) word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
                }

                System.out.println();
            }

            System.out.println("---");
            List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(fileContents);
            Iterator var18 = list.iterator();

            while (var18.hasNext()) {
                Triple<String, Integer, Integer> item = (Triple) var18.next();
                System.out.println((String) item.first() + ": " + fileContents.substring(((Integer) item.second()).intValue(), ((Integer) item.third()).intValue()));
            }

            System.out.println("---");
            System.out.println("Ten best entity labelings");
            DocumentReaderAndWriter<CoreLabel> readerAndWriter = classifier.makePlainTextReaderAndWriter();
            classifier.classifyAndWriteAnswersKBest(args[1], 10, readerAndWriter);
            System.out.println("---");
            System.out.println("Per-token marginalized probabilities");
            classifier.printProbs(args[1], readerAndWriter);
        } else {
            String[] example = new String[]{"Good afternoon Rajat Raina, how are you today?", "I go to school at Stanford University, which is located in California."};
            String[] var15 = example;
            int i = example.length;

            int var21;
            String str;
            for (var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.println(classifier.classifyToString(str));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for (var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.print(classifier.classifyToString(str, "slashTags", false));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for (var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.print(classifier.classifyToString(str, "tabbedEntities", false));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for (var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.println(classifier.classifyWithInlineXML(str));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for (var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.println(classifier.classifyToString(str, "xml", true));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for (var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.print(classifier.classifyToString(str, "tsv", false));
            }

            System.out.println("---");
            int j = 0;
            String[] var24 = example;
            var21 = example.length;

            Iterator var10;
            int var28;
            for (var28 = 0; var28 < var21; ++var28) {
                str = var24[var28];
                ++j;
                List<Triple<String, Integer, Integer>> triples = classifier.classifyToCharacterOffsets(str);
                var10 = triples.iterator();

                while (var10.hasNext()) {
                    Triple<String, Integer, Integer> trip = (Triple) var10.next();
                    System.out.printf("%s over character offsets [%d, %d) in sentence %d.%n", new Object[]{trip.first(), trip.second(), trip.third, Integer.valueOf(j)});
                }
            }

            System.out.println("---");
            i = 0;
            String[] var30 = example;
            var28 = example.length;

            for (int var26 = 0; var26 < var28; ++var26) {
                str = var30[var26];
                var10 = classifier.classify(str).iterator();

                while (var10.hasNext()) {
                    List<CoreLabel> lcl = (List) var10.next();
                    Iterator var12 = lcl.iterator();
                    while (var12.hasNext()) {
                        CoreLabel cl = (CoreLabel) var12.next();
                        System.out.print(i++ + ": ");
                        System.out.println(cl.toShorterString(new String[0]));
                    }
                }
            }
            System.out.println("---");
        }

    }
}
