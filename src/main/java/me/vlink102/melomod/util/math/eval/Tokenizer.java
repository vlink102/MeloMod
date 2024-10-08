package me.vlink102.melomod.util.math.eval;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A String tokenizer that accepts delimiters that are greater than one character.
 * @author Jean-Marc Astesana
 * @see <a href="https://opensource.org/license/apache-2-0">License information (Apache 2)</a>
 */
public class Tokenizer {
    private Pattern pattern;
    private String tokenDelimiters;
    @Setter
    @Getter
    private boolean trimTokens;

    /** Constructor.
     * <br>By default, this tokenizer trims all the tokens.
     * @param delimiters the delimiters of the tokenizer, usually, the operators symbols, the brackets and the function argument separator are used as delimiter in the string.
     */
    public Tokenizer(List<String> delimiters) {
        if (onlyOneChar(delimiters)) {
            StringBuilder builder = new StringBuilder();
            for (String delimiter : delimiters) {
                builder.append(delimiter);
            }
            tokenDelimiters = builder.toString();
        } else {
            this.pattern = delimitersToRegexp(delimiters);
        }
        trimTokens = true;
    }

    /** Tests whether a String list contains only 1 character length elements.
     * @param delimiters The list to test
     * @return true if it contains only one char length elements (or no elements)
     */
    private boolean onlyOneChar(List<String> delimiters) {
        for (String delimiter : delimiters) {
            if (delimiter.length()!=1) {
                return false;
            }
        }
        return true;
    }

    private static Pattern delimitersToRegexp(List<String> delimiters) {
        delimiters.sort(Comparator.reverseOrder());
        // Build a string that will contain the regular expression
        StringBuilder result = new StringBuilder();
        result.append('(');
        for (String delim : delimiters) {
            // For each delimiter
            if (result.length()!=1) {
                // Add it to the union
                result.append('|');
            }
            // Quote the delimiter as it could contain some regexpr reserved characters
            result.append("\\Q").append(delim).append("\\E");
        }
        result.append(')');
        return Pattern.compile(result.toString());
    }

    private void addToTokens (List<String> tokens, String token) {
        if (trimTokens) {
            token = token.trim();
        }
        if (!token.isEmpty()) {
            tokens.add(token);
        }
    }

    /** Converts a string into tokens.
     * <br>Example: The result for the expression "<i>-1+min(10,3)</i>" evaluated for a DoubleEvaluator is an iterator on "-", "1", "+", "min", "(", "10", ",", "3", ")".
     * @param string The string to be split into tokens
     * @return The tokens
     */
    public Iterator<String> tokenize(String string) {
        if (pattern!=null) {
            List<String> res = new ArrayList<>();
            Matcher m = pattern.matcher(string);
            int pos = 0;
            while (m.find()) {
                // While there's a delimiter in the string
                if (pos != m.start()) {
                    // If there's something between the current and the previous delimiter
                    // Add to the tokens list
                    addToTokens(res, string.substring(pos, m.start()));
                }
                addToTokens(res, m.group()); // add the delimiter
                pos = m.end(); // Remember end of delimiter
            }
            if (pos != string.length()) {
                // If it remains some characters in the string after last delimiter
                addToTokens(res, string.substring(pos));
            }
            // Return the result
            return res.iterator();
        } else {
            return new StringTokenizerIterator(new StringTokenizer(string, tokenDelimiters, true));
        }
    }

    private class StringTokenizerIterator implements Iterator<String> {
        private final StringTokenizer tokens;
        /** Constructor.
         * @param tokens The Stringtokenizer on which is based this instance.
         */
        public StringTokenizerIterator(StringTokenizer tokens) {
            this.tokens = tokens;
        }
        private String nextToken = null;
        @Override
        public boolean hasNext() {
            return buildNextToken();
        }
        @Override
        public String next() {
            if (!buildNextToken()) {
                throw new NoSuchElementException();
            }
            String token = nextToken;
            nextToken = null;
            return token;
        }
        private boolean buildNextToken() {
            while ((nextToken == null) && tokens.hasMoreTokens()) {
                nextToken = tokens.nextToken();
                if (trimTokens) {
                    nextToken = nextToken.trim();
                }
                if (nextToken.isEmpty()) {
                    nextToken = null;
                }
            }
            return nextToken!=null;
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}