package com.amica.billing.parse;

public class ParserFactory {

    /**
     * Look at a given filename and either create a CSVParser or FlatParser, based on the filename'sextension.
     */
    public static Parser createParser(String filename) {
        if (filename.endsWith(".csv")) {
            return new CSVParser();
        } else if (filename.endsWith(".flat")) {
            return new FlatParser();
        }
        return null;
    }
}
