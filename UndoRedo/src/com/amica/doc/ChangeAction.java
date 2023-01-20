package com.amica.doc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChangeAction implements Action {
    @NonNull private int numberOfCharsToChange;
    @NonNull private String textToChange;
    @NonNull private int offset;
    private Action revertAction;

    @Override
    public String makeChange(String text) {
        // set revertedChange
        this.revertAction = new ChangeAction(textToChange.length(), text.substring(offset, offset + numberOfCharsToChange), offset);

        // make change
        StringBuffer string = new StringBuffer(text);
        string.replace(offset, offset + numberOfCharsToChange, textToChange);

        System.out.println(String.format("Change the %s characters at offset %s to \"%s\":\"%s\"", numberOfCharsToChange, offset, textToChange, string.toString()));

        return string.toString();
    }

    @Override
    public String revertChange(String text) {
        return this.revertAction.makeChange(text);
    }
}
