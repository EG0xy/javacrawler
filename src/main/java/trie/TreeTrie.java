package trie;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author holysky.zhao 2017/8/4 18:00
 * @version 1.0.0
 */
public class TreeTrie extends TreeSet<String> implements Trie {

    public TreeTrie(Collection<String> c) {
        super(c);
    }

    public boolean isPrefix(String prefix) {
        String nextWord = ceiling(prefix);
        if (nextWord == null) {
            return false;
        }
        if (nextWord.equals(prefix)) {
            Set<String> tail = tailSet(nextWord, false);
            if (tail.isEmpty()) {
                return false;
            }
            nextWord = tail.iterator().next();
        }

        return nextWord.startsWith(prefix);
    }

    /**
     * There is a mismatch between the parameter types of vocabulary and TreeSet, so
     * force call to the upper-class method
     */
    public boolean contains(String word) {
        return super.contains(word);
    }

    public static void main(String[] args) {
        TreeTrie treeTrie = new TreeTrie(ImmutableSet.of("1","123","a","abc"));
        boolean prefix = treeTrie.isPrefix("1");
        System.out.println("1 is prefix:"+prefix);
        System.out.println("123 is prefix:"+treeTrie.isPrefix("123"));

    }


}
