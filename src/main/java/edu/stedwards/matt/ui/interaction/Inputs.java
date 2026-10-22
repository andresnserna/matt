package edu.stedwards.matt.ui.interaction;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
// import edu.stedwards.matt.ui.TextStyles;

public final class Inputs {
	private Inputs() {
	}

	/** 
	 * Builds a password field that masks typed characters, with an icon to its left. 
	 */
	public static HBox buildPasswordFieldWIcon(String promptText, FontAwesomeSolid iconType, double width, double maxWidth, String... styleClasses) {
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText(promptText);
		passwordField.setPrefWidth(width);
		passwordField.setMaxWidth(maxWidth);
		passwordField.getStyleClass().add("input-field-text");
		return wrapInputFieldWithIcon(passwordField, iconType, styleClasses);
	}

	public static HBox buildInputFieldWIcon(String promptText, FontAwesomeSolid iconType, double width, double maxWidth, String... styleClasses) {
		TextField textField = inputField(promptText, width, maxWidth);
		return wrapInputFieldWithIcon(textField, iconType, styleClasses);
	}

	/**
	 * 
	 */
	private static HBox wrapInputFieldWithIcon(TextField textField, FontAwesomeSolid iconType, String... styleClasses) {
		FontIcon icon = new FontIcon(iconType);
		icon.getStyleClass().add("input-field-icon");
		HBox.setHgrow(textField, Priority.ALWAYS);

		HBox input = new HBox(12, icon, textField);
		input.setAlignment(Pos.CENTER_LEFT);
		// input.getStyleClass().add("input-field-with-icon");
		input.getStyleClass().addAll(styleClasses);
		return input;
	}

	/**
	 * Builds a labeled input group with the label above and to the left of its control.
	 */
	public static VBox buildInputGroup(Label fieldLabel, Node inputControl) {
		fieldLabel.setLabelFor(inputControl);

		//hbox of the label first
		HBox inputLabel = new HBox(fieldLabel);

		//hbox of the input field
		HBox input = new HBox(inputControl);

		//now vbox of them together
		VBox inputGroup = new VBox();
		inputGroup.getChildren().addAll(inputLabel, input);
		inputGroup.setAlignment(Pos.TOP_LEFT); // label on top and to the left,
		inputGroup.getStyleClass().add("input-field-spacing");
		
		return inputGroup;

	}

	/**
	 * Converts a nullable model value into text suitable for an editable input control.
	 */
	public static String nullToEmpty(String value) {
		return value == null ? "" : value;
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
		textField.getStyleClass().add("input-field-text");
		textField.getStyleClass().addAll(styleClasses);
      
		return textField;
	}

	/**
	 *
	 */
	public static TextArea textArea(String promptText, double width, double maxWidth, double height, double maxHeight, String... styleClasses) {
		TextArea myTextArea = new TextArea();

		myTextArea.setPromptText(promptText);
		myTextArea.setPrefWidth(width);
		myTextArea.setMaxWidth(maxWidth);
		myTextArea.setPrefHeight(height);
		myTextArea.setMaxHeight(maxHeight);
		myTextArea.getStyleClass().add("input-field-text");
		myTextArea.getStyleClass().addAll(styleClasses);

		return myTextArea;
	}

	/**
	 * this is the start date end date picker in the experiance onboarding screen,
	 * can be an all inclusive builder, no need to seperate the components
	 */
	public static void buildDatePicker() {

	}

	/**
	 * Field with unit suffix: "$124,000.00 / yr" the suffix is yr, and the / is
	 * added in this func.
	 */
	private static void wrapInputFieldWithSuffix(TextField textField, String suffix, String... styleClasses) {

	}




}
