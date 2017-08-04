package trie;

/**
 * prefix 能力树   https://www.toptal.com/java/the-trie-a-neglected-data-structure
 *
 * @author holysky.zhao 2017/8/4 17:53
 * @version 1.0.0
 */
public interface Trie {
    boolean add(String word);
    boolean isPrefix(String prefix);
    boolean contains(String word);
}
