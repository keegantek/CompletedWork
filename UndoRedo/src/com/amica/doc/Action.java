package com.amica.doc;

public interface Action {
    /**
     * makeChange - perform the requested change on the provided Text field (presumably passed from a particular Document object)
     * @param text - the text to apply the change to
     * @return a String of the modified text
     */
    public String makeChange(String text);

    /**
     * revertChange - perform the opposite of the change on the provided text field (presumably passed from a particular Document object)
     * @param text - the text to apply the change to
     * @return a String of the modified text
     */
    public String revertChange(String text);
}
