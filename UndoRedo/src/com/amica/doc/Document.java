package com.amica.doc;

import com.amica.doc.Action;
import lombok.Data;

import java.util.ArrayDeque;
import java.util.Collection;
@Data
public class Document {
    private String text;
    private ArrayDeque<Action> doneActions;
    private ArrayDeque<Action> undoneActions;

    /**
     * Constructor - creates a new Document object using an initial String that will set the text field
     * The Document's doneActions and undoneActions Collections will also be initialized
     * @param text - a String that will be used as the initial value for the text field
     */
    public Document(String text) {
        this.text = text;
        doneActions = new ArrayDeque<>(); // will be used to store Actions that have been completed
        undoneActions = new ArrayDeque<>(); // will be used to store Actions that were completed, but have since been reverted
    }

    /**
     * takeAction - facilitiates the manipulation of the Document's text field through a particular Action
     * Steps of takeAction:
     * 1.	takes a new Action (insert, replace, delete), calls its makeChange()
     * 2.	adds action to doneActions
     * 3.	clear undoneActions
     * @param action
     */
    public void takeAction(Action action) {
        // call the Action's makeChange and set the Document's text field to that value
        this.text = action.makeChange(text);

        // add the action to doneActions
        doneActions.addLast(action);

        // clear undoneActions
        undoneActions = new ArrayDeque<>();
    }

    /**
     * undo - facilitiates the manipulation of the Document's text field by reverting the last Action taken on this
     * Steps of undo:
     * 1.	Take top Action from doneActions
     * 2.	Call its revertChange()
     */
    public void undo() {
        if (doneActions.size() > 0) {
            // take top Action from doneActions
            Action lastAction = doneActions.removeLast();
            undoneActions.add(lastAction);

            // call its revertChange method
            this.text = lastAction.revertChange(text);

            System.out.println(String.format("Undo: \"%s\"", text));
        } else {
            System.out.println("There are no Actions to be undone");
        }
    }

    /**
     * redo - facilitates the manipulation of the Document's text field by reverting the last Action undone on this
     * Steps of redo:
     * 1.	takes the last Action from undoneActions
     * 2.	adds action to doneActions
     * 3.   calls its makeChange method
     */
    public void redo() {
        if (undoneActions.size() > 0) {
            // takes last Action from undoneActions
            Action lastAction = undoneActions.removeLast();

            // adds action to doneActions
            doneActions.add(lastAction);

            // calls its makeChange method
            this.text = lastAction.makeChange(text);

            // clear undoneActions
//            undoneActions = new ArrayDeque<>();

            System.out.println(String.format("Redo: \"%s\"", text));
        } else {
            System.out.println("There are no actions to be redone");
        }
    }
}
