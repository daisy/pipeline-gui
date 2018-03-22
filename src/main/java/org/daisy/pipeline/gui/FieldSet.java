package org.daisy.pipeline.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class FieldSet extends GridPane {
    
    private Label titleLbl;
    private int lastRow, lastRealRow; // subtitle and row of controls counts as one row to user
    private List<Integer> cols; // column indexes of last node in each row
    private int totalCols;
    
/*----SETUP--------------------------------------------------------------*/
    
    /**
     * Constructs an empty FieldSet with an empty title.
     */
    public FieldSet() {
        super();
        init(null);
    }
    
    /**
     * Constructs an empty FieldSet with the specified title.
     * 
     * @param title - the title label text
     */
    public FieldSet(String title) {
        super();
        init(title);
    }
    
    private void init(String title) {
        if (title != null)
            addTitle(title);
        getStyleClass().add("fieldset");
        cols = new ArrayList<Integer>();
        cols.add(1); // title col
        cols.add(0);
        lastRow = -1;
        lastRealRow = 1;
        totalCols = 1;
    }
    
/*----PUBLIC--------------------------------------------------------------*/
    
    /**
     * Sets the title label text for this FieldSet (if it wasn't set with the constructor).
     * 
     * @param title - the title label text
     */
    public void setTitle(String title) {
        if (titleLbl != null)
            titleLbl.setText(title);
        else
            addTitle(title);
    }
    
    // Helper
    private void addTitle(String title) {
        getChildren().remove(titleLbl);
        titleLbl = new Label(title);
        titleLbl.getStyleClass().add("subtitle");
        add(titleLbl, 0, 0, 1, 1);
    }
    
    /**
     * Returns the title for this fieldset if it was added.
     *
     * @return the title text String for this FieldSet, empty ("") if not added
     */
    public String getTitle() {
        return (titleLbl != null)? titleLbl.getText(): "";
    }
    
    /**
     * Returns a label in this FieldSet by text.
     * @param text - the text to match
     * @return - the label with text that is {@link Object#equals(Object)} the given text
     */
    public Label getLabel(String text) {
        for (Node child: getChildren())
            if (child.getClass().isAssignableFrom(Label.class) && ((Label)child).getText().equals(text))
                return (Label)child;
        return null;
    }
    
    /**
     * Sets the GridPane Hgrow property for the given node in this FieldSet.
     * 
     * Use this instead of {@link GridPane#setHgrow(Node, Priority)}.
     * 
     * @param node - the node to set the Hgrow property for
     * @param priority -  the horizontal grow priority for the node
     */
    public void setHGrow(Node node, Priority priority) {
        setHgrow(node, priority);
    }
    
    /**
     * Sets the GridPane Hgap property for this FieldSet.
     * 
     * Use this instead of {@link GridPane#setHgap(double)}.
     * 
     * @param priority -  the horizontal grow priority for this FieldSet
     */
    public void setHGap(double hgap) {
        setHgap(hgap);
    }
    
    /**
     * Sets the GridPane Hgap property for this FieldSet.
     * 
     * Use this instead of {@link GridPane#setVgap(double)}.
     * 
     * @param priority -  the vertical grow priority for this FieldSet
     */
    public void setVGap(double hgap) {
        setVgap(hgap);
    }
    
    /**
     * Returns the row index for the given node if it was added.
     * 
     * Use this instead of {@link GridPane#getRowIndex(Node)}.
     * 
     * @return null if it wasn't added
     */
    public int getRow(Node node) {
        return convertToRow(getRowIndex(node));
    }
    
    /**
     * Returns the column index for the given node if it was added.
     * 
     * Use this instead of {@link GridPane#getColumnIndex(Node)}.
     * 
     * @return null if it wasn't added
     */
    public int getColumn(Node node) {
        return getColumnIndex(node);
    }
    
    /**
     * Adds a new empty row to this FieldSet with an empty subtitle.
     */
    public void newRow() {
        newRow(null);
    }
    
    /**
     * Adds a new empty row to this FieldSet with the given subtitle.
     * 
     * @param subtitle - the subtitle label text
     */
    public void newRow(String subtitle) {
        if (subtitle != null) {
            add(new Label(subtitle), cols.get(lastRealRow), lastRealRow, 1, 1);
            newRow(true);
        } else {
            add(new Label(""), cols.get(lastRealRow), lastRealRow, 1, 1);
            newRow(false);
        }
    }
    
    // Helper
    private void newRow(boolean hasSubtitle) {
        lastRow++;
        lastRealRow+=2;
        if (hasSubtitle) cols.add(1);
        else cols.add(0);
        cols.add(0);
    }
    
    // Helper - check if totalCols has increased, if so find titles and respan them
    private void respanTitles(int row) {
        if (cols.get(row) > totalCols) {
            totalCols = cols.get(row);
            for (int i = -1; i < lastRealRow; i+=2)
                for (Node child: getChildren())
                    if (getRowIndex(child) == 0 || // title label
                            getRowIndex(child) == i)
                        GridPane.setColumnSpan(child, totalCols);
        }
    }
    
    /**
     * Adds a node to the last row.
     * 
     * Default colSpan and rowSpan is 1.
     * 
     * @param node - the node to add
     */
    public void addNode(Node node) {
        addNode(node, lastRow, 1, 1);
    }
    
    /**
     * Adds a node to the end of the specified row.
     * 
     * Default colSpan and rowSpan is 1.
     * 
     * @param node - the node to add
     * @param row - the row to add the control to
     */
    public void addNode(Node node, int row) {
        addNode(node, row, 1, 1);
    }
    
    /**
     * Adds a node to the end of the specified row with the given colSpan and rowSpan.
     * 
     * @param node -  the node to add
     * @param row - the row to add the node to
     * @param colSpan - determines how many columns the node spans
     * @param rowSpan - determines how many rows the node spans
     */
    public void addNode(Node node, int row, int colSpan, int rowSpan) {
        if (node == null)
            throw new NullPointerException("argument control cannot be null");
        if (row < 0)
            throw new IllegalArgumentException("argument row cannot be negative: " + row);
        if (colSpan <= 0 || rowSpan <= 0)
            throw new IllegalArgumentException("arguments colSpan and rowSpan cannot be less than 1: " + colSpan + ", " + rowSpan);
        if (lastRow == -1) newRow();
        
        int realRow = convertToRealRow(row);
        add(node, cols.get(realRow), realRow, colSpan, rowSpan);
        incrementCols(realRow);
        // make room for rowSpan
        if (rowSpan > 1)
            for (int i = 0; i < rowSpan; i++)
                newRow();
    }
    
    private void incrementCols(int row) {
        cols.set(row, cols.get(row)+1);
        respanTitles(row);
    }
    
    /**
     * Inserts a node into the specified row and column, shifting all cells in that row to make room.
     * @param node - the node to insert
     * @param row - the row to insert into
     * @param col - the col to insert into
     */
    public void insertNode(Node node, int row, int col) {
        insertNode(node, row, col, 1);
    }
    
    /**
     * Inserts a node into the specified row and column, shifting any cells in that row after the specified position to the right.
     * 
     * Doesn't currently support rowSpan.
     * 
     * @param node -  the node to insert
     * @param row - the row to insert into
     * @param col - the col to insert into
     * @param colSpan - determines how many columns the node spans
     */
    public void insertNode(Node node, int row, int col, int colSpan) {
        if (node == null)
            throw new NullPointerException("argument node cannot be null");
        if (row < 0)
            throw new IllegalArgumentException("argument row cannot be negative: " + row);
        if (colSpan <= 0)
            throw new IllegalArgumentException("argument colSpan cannot be less than 1: " + colSpan);
        
        int realRow = convertToRealRow(row);
        shiftCells(realRow, col, colSpan);
        add(node, col, realRow, colSpan, 1);
    }
    
    /**
     * Returns the node in this FieldSet at the specified row and column.
     * 
     * @return the desired node, null if it does not exist
     */
    public Node getNode(int row, int col) {
        if (row < 0 || col < 0)
            throw new IllegalArgumentException("arguments row and col cannot be negative: " + row + ", " + col);
        for (Node child: getChildren())
            if (getRowIndex(child) == convertToRealRow(row) && getColumnIndex(child) == col)
                return child;
        return null;
    }
    
    /**
     * Returns the first node in this FieldSet with the specified userData.
     * 
     * @param userData - the userData to search with
     * @return the desired node, null if not found
     */
    public Node getNode(Object userData) {
        for (Node child: getChildren())
            if (child.getUserData() != null && child.getUserData().equals(userData))
                return child;
        return null;
    }
    
    // Helper
    private void shiftCells(int row, int col, int colSpan) {
        for (Node child: getChildren())
            if (getRowIndex(child) == row && getColumnIndex(child) >= col) {
                GridPane.setColumnIndex(child, col+colSpan);
                respanTitles(row);
            }
    }
    
    // Helper
    private int convertToRealRow(int row) {
        return row*2+2;
    }
    
    // Helper
    private int convertToRow(int realRow) {
        return (realRow-2)/2;
    }
    
    /**
     * Disables all nodes in the specified row, excluding the optional exceptions.
     * 
     * Convenience method.
     * 
     * @param row - the row to disable
     * @param disabled - whether to disable the nodes
     * @param exceptions - these nodes in the FieldSet will not be affected (provided they {@link Object#equals()} the given nodes)
     */
    public void setRowDisabled(int row, boolean disabled, Node... exceptions) {
        List<Node> exceptionsList = Arrays.asList(exceptions);
        for (Node child: getChildren())
            if (getRowIndex(child) == convertToRealRow(row) && !exceptionsList.contains(child))
                child.setDisable(disabled);
    }
    
    /**
     * Disables all nodes in the specified row, excluding the optional exceptions.
     * 
     * Convenience method.
     * 
     * @param row - the row to disable
     * @param disabled - whether to disable the nodes
     * @param userDataExceptions - these nodes in the FieldSet will not be affected (provided their userData {@link Object#equals()} the given userData)
     */
    public void setRowDisabled(int row, boolean disabled, Object... userDataExceptions) {
        List<Object> exceptionsList = Arrays.asList(userDataExceptions);
        for (Node child: getChildren())
            if (getRowIndex(child) == row && !exceptionsList.contains(child.getUserData()))
                child.setDisable(disabled);
    }
    
}
