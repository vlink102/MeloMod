package me.vlink102.melomod.util.math.eval;

public class BracketPair {
    /** The parentheses pair: ().*/
    public static final BracketPair PARENTHESES = new BracketPair('(', ')');
    /** The square brackets pair: [].*/
    public static final BracketPair BRACKETS = new BracketPair('[', ']');
    /** The braces pair: {}.*/
    public static final BracketPair BRACES = new BracketPair('{', '}');
    /** The angle brackets pair: &lt;&gt;.*/
    public static final BracketPair ANGLES = new BracketPair('<', '>');

    private String open;
    private String close;

    /** Constructor.
     * @param open The character used to open the brackets.
     * @param close The character used to close the brackets.
     */
    public BracketPair(char open, char close) {
        super();
        this.open = String.valueOf(open);
        this.close = String.valueOf(close);
    }

    /** Gets the open bracket character.
     * @return a char
     */
    public String getOpen() {
        return open;
    }

    /** Gets the close bracket character.
     * @return a char
     */
    public String getClose() {
        return close;
    }

    @Override
    public String toString() {
        return open + close;
    }
}