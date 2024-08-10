package me.vlink102.melomod.util.math.eval;

import lombok.Getter;

@Getter
public class BracketPair {
    /** The parentheses pair: ().*/
    public static final BracketPair PARENTHESES = new BracketPair('(', ')');
    /** The square brackets pair: [].*/
    public static final BracketPair BRACKETS = new BracketPair('[', ']');
    /** The braces pair: {}.*/
    public static final BracketPair BRACES = new BracketPair('{', '}');
    /** The angle brackets pair: &lt;&gt;.*/
    public static final BracketPair ANGLES = new BracketPair('<', '>');

    /**
     * -- GETTER --
     * Gets the open bracket character.
     *
     * @return a char
     */
    private final String open;
    /**
     * -- GETTER --
     * Gets the close bracket character.
     *
     * @return a char
     */
    private final String close;

    /** Constructor.
     * @param open The character used to open the brackets.
     * @param close The character used to close the brackets.
     */
    public BracketPair(char open, char close) {
        super();
        this.open = String.valueOf(open);
        this.close = String.valueOf(close);
    }

    @Override
    public String toString() {
        return open + close;
    }
}