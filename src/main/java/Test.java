import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author holysky.zhao 2017/8/4 14:10
 * @version 1.0.0
 */
public class Test {

    public static void main(String[] args) throws IOException, ClassNotFoundException {

//        String[] testCase = new String[]{
//                "武胜县新学乡政府大楼门前锣鼓喧天",
//                "蓝翔给宁夏固原市彭阳县红河镇黑牛沟村捐赠了挖掘机",
//        };
//        Segment segment = HanLP.newSegment().enablePlaceRecognize(true);
//        for (String sentence : testCase)
//        {
//            List<Term> termList = segment.seg(sentence);
//            System.out.println(termList);
//        }


        String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
        if(args.length > 0) {
            serializedClassifier = args[0];
        }

        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
        if(args.length > 1) {
            String fileContents = IOUtils.slurpFile(args[1]);
            List<List<CoreLabel>> out = classifier.classify(fileContents);
            Iterator var5 = out.iterator();

            List sentence;
            Iterator var7;
            CoreLabel word;
            while(var5.hasNext()) {
                sentence = (List)var5.next();
                var7 = sentence.iterator();

                while(var7.hasNext()) {
                    word = (CoreLabel)var7.next();
                    System.out.print(word.word() + '/' + (String)word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
                }

                System.out.println();
            }

            System.out.println("---");
            out = classifier.classifyFile(args[1]);
            var5 = out.iterator();

            while(var5.hasNext()) {
                sentence = (List)var5.next();
                var7 = sentence.iterator();

                while(var7.hasNext()) {
                    word = (CoreLabel)var7.next();
                    System.out.print(word.word() + '/' + (String)word.get(CoreAnnotations.AnswerAnnotation.class) + ' ');
                }

                System.out.println();
            }

            System.out.println("---");
            List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(fileContents);
            Iterator var18 = list.iterator();

            while(var18.hasNext()) {
                Triple<String, Integer, Integer> item = (Triple)var18.next();
                System.out.println((String)item.first() + ": " + fileContents.substring(((Integer)item.second()).intValue(), ((Integer)item.third()).intValue()));
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
            for(var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.println(classifier.classifyToString(str));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for(var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.print(classifier.classifyToString(str, "slashTags", false));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for(var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.print(classifier.classifyToString(str, "tabbedEntities", false));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for(var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.println(classifier.classifyWithInlineXML(str));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for(var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.println(classifier.classifyToString(str, "xml", true));
            }

            System.out.println("---");
            var15 = example;
            i = example.length;

            for(var21 = 0; var21 < i; ++var21) {
                str = var15[var21];
                System.out.print(classifier.classifyToString(str, "tsv", false));
            }

            System.out.println("---");
            int j = 0;
            String[] var24 = example;
            var21 = example.length;

            Iterator var10;
            int var28;
            for(var28 = 0; var28 < var21; ++var28) {
                str = var24[var28];
                ++j;
                List<Triple<String, Integer, Integer>> triples = classifier.classifyToCharacterOffsets(str);
                var10 = triples.iterator();

                while(var10.hasNext()) {
                    Triple<String, Integer, Integer> trip = (Triple)var10.next();
                    System.out.printf("%s over character offsets [%d, %d) in sentence %d.%n", new Object[]{trip.first(), trip.second(), trip.third, Integer.valueOf(j)});
                }
            }

            System.out.println("---");
            i = 0;
            String[] var30 = example;
            var28 = example.length;

            for(int var26 = 0; var26 < var28; ++var26) {
                str = var30[var26];
                var10 = classifier.classify(str).iterator();

                while(var10.hasNext()) {
                    List<CoreLabel> lcl = (List)var10.next();
                    Iterator var12 = lcl.iterator();

                    while(var12.hasNext()) {
                        CoreLabel cl = (CoreLabel)var12.next();
                        System.out.print(i++ + ": ");
                        System.out.println(cl.toShorterString(new String[0]));
                    }
                }
            }

            System.out.println("---");
        }

    }
}
