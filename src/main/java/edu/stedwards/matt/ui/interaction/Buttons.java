package edu.stedwards.matt.ui.interaction;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public final class Buttons {
	private Buttons() {
	}

	public static Button button(String text, EventHandler<ActionEvent> onAction, String... styleClasses) {
		Button button = new Button(text);

		if (onAction != null) {
			button.setOnAction(onAction);
		}

		addStyleClasses(button, styleClasses);
		return button;
	}

	public static HBox horizontalGroup(String styleClass, double spacing, Node... children) {
		HBox group = new HBox(spacing, children);

		if (styleClass != null && !styleClass.isBlank()) {
			group.getStyleClass().add(styleClass);
		}

		return group;
	}

	private static void addStyleClasses(Node node, String... styleClasses) {
		if (styleClasses == null) {
			return;
		}
      
		for (String styleClass : styleClasses) {
			if (styleClass != null && !styleClass.isBlank()) {
				node.getStyleClass().add(styleClass);
			}
		}
	}
}
