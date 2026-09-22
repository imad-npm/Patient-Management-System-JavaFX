package application.utils;

/*
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;


public class FilterableComboBox {

    public static void makeFilterable(ComboBox<String> comboBox) {
        ObservableList<String> originalItems = FXCollections.observableArrayList(comboBox.getItems());

        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                if (!originalItems.isEmpty()) {
                    comboBox.setItems(originalItems);
                }
                if (comboBox.isShowing()) {
                    comboBox.hide();
                }
            } else {
                ObservableList<String> filteredList = FXCollections.observableArrayList();
                for (String item : originalItems) {
                    if (item.toLowerCase().contains(newValue.toLowerCase())) {
                        filteredList.add(item);
                    }
                }
                comboBox.setItems(filteredList);
                comboBox.show();
            }
        });
        
        comboBox.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
                // Handle deletion of characters
                String filterText = comboBox.getEditor().getText();
                int caretPosition = comboBox.getEditor().getCaretPosition();

                if (event.getCode() == KeyCode.BACK_SPACE && caretPosition > 0) {
                    // Delete character to the left of the caret
                    String newText = filterText.substring(0, caretPosition - 1) + filterText.substring(caretPosition);
                    comboBox.getEditor().setText(newText); // Update the text without the deleted character
                    comboBox.getEditor().positionCaret(caretPosition - 1); // Set the caret position after deletion
                } else if (event.getCode() == KeyCode.DELETE && caretPosition < filterText.length()) {
                    // Delete character to the right of the caret
                    String newText = filterText.substring(0, caretPosition) + filterText.substring(caretPosition + 1);
                    comboBox.getEditor().setText(newText); // Update the text without the deleted character
                    comboBox.getEditor().positionCaret(caretPosition); // Set the caret position after deletion
                }

                event.consume(); // Consume the event to prevent further handling
            }
        });
        comboBox.getEditor().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                // Handle double-click event for selecting all text
                comboBox.getEditor().selectAll();
                event.consume(); // Consume the event to prevent further handling
            }
        });




     


    }
}*/

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class FilterableComboBox {
    public static void makeFilterable(ComboBox<String> comboBox) {
        ObservableList<String> items = comboBox.getItems();
        ObservableList<String> filteredItems = FXCollections.observableArrayList();

        comboBox.setEditable(true);
        TextField editor = comboBox.getEditor();

        editor.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if (keyCode == KeyCode.UP || keyCode == KeyCode.DOWN || keyCode == KeyCode.ENTER) {
                return;
            }

            if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT) {
                return;
            }

            if (keyCode == KeyCode.DELETE) {
                event.consume();
                int caretPosition = editor.getCaretPosition();
                int textLength = editor.getLength();

                if (caretPosition < textLength) {
                    editor.deleteText(caretPosition, caretPosition + 1);
                } else {
                    editor.deletePreviousChar();
                }
            }

            String searchText = editor.getText().toLowerCase();
            filteredItems.clear();

            for (String item : items) {
                if (item.toLowerCase().contains(searchText)) {
                    filteredItems.add(item);
                }
            }

            comboBox.setItems(filteredItems);
            comboBox.show();
        });
    }
}
