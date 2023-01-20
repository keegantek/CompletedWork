package com.amica.doc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InsertAction implements Action {
    @NonNull private String textToChange;
    @NonNull private int offset;
    private Action revertAction;

    public String makeChange(String text) {
        // make revertedChange
        this.revertAction = new DeleteAction(textToChange.length(), offset);

        // make change
        StringBuffer string = new StringBuffer(text);
        string.insert(this.offset, this.textToChange);

        System.out.println(String.format("Insert \"%s\" at offset %s: \"%s\"", textToChange, offset, string.toString()));

        return string.toString();
    }

    public String revertChange(String text) {
        return this.revertAction.makeChange(text);
    }
}
