package me.vlink102.melomod.util.math;

public class Constant {
    private String name;

    /** Constructor
     * @param name The mnemonic of the constant.
     * <br>The name is used in expressions to identified the constants.
     */
    public Constant(String name) {
        this.name = name;
    }

    /** Gets the mnemonic of the constant.
     * @return the id
     */
    public String getName() {
        return name;
    }
}
