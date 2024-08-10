package me.vlink102.melomod.util.math.eval;

import lombok.Getter;

@Getter
public class Constant {
    /**
     * -- GETTER --
     * Gets the mnemonic of the constant.
     *
     * @return the id
     */
    private final String name;

    /** Constructor
     * @param name The mnemonic of the constant.
     * <br>The name is used in expressions to identified the constants.
     */
    public Constant(String name) {
        this.name = name;
    }

}
