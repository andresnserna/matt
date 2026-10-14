package edu.stedwards.matt.ui.interaction;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
// import edu.stedwards.matt.ui.TextStyles;

public final class Inputs {
	private Inputs() {
	}

	/**
	 * builds a styled text field with the label above it and to the left. 
	 * this function should be passed in a Label object built by TextStyles.buildFieldLabel() and a TextField object built by Inputs.inputField().
	 */
	public static VBox buildInputGroup(Label fieldLabel, TextField inputField) {
		fieldLabel.setLabelFor(inputField);

		//hbox of the label first
		HBox inputLabel = new HBox(fieldLabel);

		//hbox of the input field
		HBox input = new HBox(inputField);

		//now vbox of them together
		VBox inputGroup = new VBox();
		inputGroup.getChildren().addAll(inputLabel, input);
		inputGroup.setAlignment(Pos.TOP_LEFT); // label on top and to the left,
		
		return inputGroup;

	}

	/**
	 * builds a text field with fields for the placeholder text, and specify the width for where it will be put. 
	 * can also take in a max width so resizing of the app will only let the text field grow to a certain size.
	 */
	public static TextField inputField(String promptText, double width, double maxWidth, String... styleClasses) {
		TextField textField = new TextField();
		textField.setPromptText(promptText);
		textField.setPrefWidth(width);
		textField.setMaxWidth(maxWidth);
		textField.getStyleClass().addAll(styleClasses);
      
		return textField;
	}
}
