package trie;

/**
 * 包含26个字母的trie树实现
 * @author holysky.zhao 2017/8/4 18:22
 * @version 1.0.0
 */

import java.util.Collection;


public class ArrayTrie implements Trie {

    private boolean isWord = false;
    // The number of possible children is the number of letters in the alphabet
    private ArrayTrie[] children = new ArrayTrie[26];
    // This is the number of actual children
    private int numChildren = 0;

    public ArrayTrie() {
    }

    public ArrayTrie(Collection<String> words) {
        for (String w:words) {
            add(w);
        }
    }

    public static int getIndex(char i){
        return i - 'a';
    }

    public boolean add(String s) {
        char first = s.charAt(0);
        int index = getIndex(first);
        if (index < 0) {
            System.out.println("uf");
        }
        ArrayTrie child = children[index];
        if (child == null) {
            child = new ArrayTrie();
            children[index] = child;
            numChildren++;
        }
        if (s.length() == 1) {
            if (child.isWord) {
                // The word is already in the trie
                return false;
            }
            child.isWord = true;
            return true;
        } else {
            // Recurse into sub-trie
            return child.add(s.substring(1));
        }
    }

    /**
     * Searches for a string in this trie
     * @param s
     * @return
     */
    public boolean contains(String s) {
        ArrayTrie n = getNode(s);
        return n != null && n.isWord;
    }

    /**
     * Searches for a string prefix in this trie
     * @param s
     * @return
     */
    public boolean isPrefix(String s) {
        ArrayTrie n = getNode(s);
        return n != null && n.numChildren > 0;
    }

    /**
     * Returns the node corresponding to the string
     * @param s
     * @return
     */
    public ArrayTrie getNode(String s) {
        ArrayTrie node = this;
        for (int i = 0; i < s.length(); i++) {
            int index = getIndex(s.charAt(i));
            ArrayTrie child = node.children[index];
            if (child == null) {
                // There is no such word
                return null;
            }
            node = child;
        }
        return node;
    }

    /**
     * Searches for a string represented as indices in this trie,
     * @return
     */
    public boolean contains(byte[] indices, int offset, int len) {
        ArrayTrie n = getNode(indices, offset, len);
        return n != null && n.isWord;
    }

    public boolean contains(byte[] indices, int offset) {
        ArrayTrie n = getNode(indices, offset, indices.length-offset);
        return n != null && n.isWord;
    }

    /**
     * Searches for a string prefix represented as indices in this trie
     * @return
     */
    public boolean isPrefix(byte[] indices, int offset, int len) {
        ArrayTrie n = getNode(indices, offset, len);
        return n != null && n.numChildren > 0;
    }

    public boolean isPrefix(byte[] indices, int offset) {
        ArrayTrie n = getNode(indices, offset, indices.length-offset);
        return n != null && n.numChildren > 0;
    }

    /**
     * Returns the node corresponding to the string represented as indices
     * @return
     */
    public ArrayTrie getNode(byte[] indices, int offset, int len) {
        ArrayTrie node = this;
        for (int i = 0; i < len; i++) {
            int index = indices[offset+i];
            ArrayTrie child = node.children[index];
            if (child == null) {
                // There is no such word
                return null;
            }
            node=child;
        }
        return node;
    }

    public String getName() {
        return getClass().getName();
    }

    public boolean isWord() {
        return isWord;
    }

    public boolean hasChildren() {
        return numChildren > 0;
    }
}