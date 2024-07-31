package me.vlink102.melomod.util.math.eval;

public interface AbstractVariableSet<T> {
    /** Gets the value of a variable.
     * @param variableName The name of a variable
     * @return the variable's value or null if the variable is unknown
     */
    public T get(String variableName);
}