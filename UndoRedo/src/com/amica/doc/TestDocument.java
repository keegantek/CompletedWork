package com.amica.doc;

public class TestDocument {

    public static void main(String[] args) {
        System.out.println("Creating a test Document");

        Document doc = new Document("Every good boy does fine.");

        System.out.println(String.format("Start with the string \"%s\"", doc.getText()));
        doc.takeAction(new InsertAction("d", 13));
        doc.takeAction(new DeleteAction(6, 5));
        doc.takeAction(new ChangeAction(4, "well", 15));
        doc.undo();
        doc.undo();
        doc.undo();
        doc.redo();
        doc.redo();
        doc.takeAction(new ChangeAction(7, "? F", 9));
        doc.redo();
        doc.undo();
        doc.undo();
        doc.redo();
    }
}