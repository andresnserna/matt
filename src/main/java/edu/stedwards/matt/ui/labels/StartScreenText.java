/**
 * Architecture layer: view
 * Centralized text resources used by the JavaFX screens.
 */
package edu.stedwards.matt.ui.labels;

public final class StartScreenText {
    // Naming convention: Tier of Label, text clue
    // public static final String HEADER_SALUTATION_MORNING = "Good Morning, "
    // public static final String HEADER_SALUTATION_AFTERNOON = "Good Morning, "
    // public static final String HEADER_SALUTATION_EVENING = "Good Morning, " - screen, tier, clue
    // public static final String USER_FIRST_NAME = "Andres" - scope, text clue
    // now build an example
    // HOME_HEADER_SALUTATION_MORNING + USERNAME + "."
    //                                                  Good Morning, Andres.
    public static final String APP_TITLE = "Matt";
    public static final String SUBTITLE_LOADING = "Loading ";
    // public static final String SUBTITLE_LOADING_ITEM_1 = "Resources";
    // public static final String SUBTITLE_LOADING_ITEM_2 = "AI Models";
    // public static final String SUBTITLE_LOADING_ITEM_3 = "Materials";
    public static final String CREATED_BY = "Created by Andres Serna & Javier Zavala Copyright 2026";



    private StartScreenText() {
    }
}
