package com.amica.doc;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DeleteAction implements Action {
    @NonNull private int numberOfCharsToDelete;
    @NonNull private int offset;
    private Action revertAction;

    @Override
    public String makeChange(String text) {
        // set revertedChange
        this.revertAction = new InsertAction(text.substring(offset, offset + numberOfCharsToDelete), offset);

        // make change
        StringBuffer string = new StringBuffer(text);
        string.delete(offset, offset + numberOfCharsToDelete);

        System.out.println(String.format("Delete %s characters at offset %s: \"%s\"", numberOfCharsToDelete, offset, string.toString()));

        return string.toString();
    }

    @Override
    public String revertChange(String text) {
        return this.revertAction.makeChange(text);
    }
}
