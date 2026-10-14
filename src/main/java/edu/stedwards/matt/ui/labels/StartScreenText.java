/**
 * Architecture layer: view
 * Centralized text resources used by the JavaFX screens.
 */
package edu.stedwards.matt.ui.labels;
import javafx.scene.text.TextAlignment;

/**
 * TODO: (LONG description of this class)
 */
public final class StartScreenText {

    public static final String APP_TITLE = "Matt";
    public static final TextAlignment APP_TITLE_ALIGNMENT = TextAlignment.CENTER;

    public static final String SUBTITLE_LOADING = "Loading ";
    public static final TextAlignment SUBTITLE_LOADING_ALIGNMENT = TextAlignment.CENTER;

    // public static final String SUBTITLE_LOADING_ITEM_1 = "Resources";
    // public static final String SUBTITLE_LOADING_ITEM_2 = "AI Models";
    // public static final String SUBTITLE_LOADING_ITEM_3 = "Materials";
    public static final String CREATED_BY = "Created by Andres Serna & Javier Zavala Copyright 2026";
    public static final TextAlignment CREATED_BY_ALIGNMENT = TextAlignment.CENTER;



    private StartScreenText() {
    }
}
