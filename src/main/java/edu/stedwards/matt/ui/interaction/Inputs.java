package edu.stedwards.matt.ui.interaction;

import javafx.scene.control.TextField;

public final class Inputs {
	private Inputs() {
	}

	public static TextField textField(String promptText, String... styleClasses) {
		TextField textField = new TextField();
		textField.setPromptText(promptText);

		if (styleClasses != null) {
			for (String styleClass : styleClasses) {
				if (styleClass != null && !styleClass.isBlank()) {
					textField.getStyleClass().add(styleClass);
				}
			}
		}
      
		return textField;
	}
}
