package edu.stedwards.matt.ui.layouts.application;

import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

public class ApplicationHomeStep extends BorderPane{

   public BorderPane buildApplicationHomeLayout(){
      BorderPane applicationHomeLayout = new BorderPane();
      // building the main page for "applications" for the app, this has the list of all applications
      // you've uploaded to the app so far, and sectiosn them in collapsable bars of this week, last week, and more.
      // congregate all the headers and container boxes together in this layout builder and return it as the layout to be shown to main

      return applicationHomeLayout;
   }

   public ApplicationHomeStep(){
      getStyleClass().add("");
      setCenter(buildApplicationHomeLayout());
   }
   
}
