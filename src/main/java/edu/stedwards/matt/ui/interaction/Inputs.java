package edu.stedwards.matt.ui.interaction;

import javafx.scene.control.TextField;

public final class Inputs {
	private Inputs() {
	}

	public static TextField textField(String promptText, double width, String... styleClasses) {
		TextField textField = new TextField();

		textField.setPromptText(promptText);
		textField.setPrefWidth(width);
		textField.setMaxWidth(Double.MAX_VALUE);
		textField.getStyleClass().addAll(styleClasses);
      
		return textField;
	}
}
