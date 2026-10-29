package edu.stedwards.matt.ui.layouts.template;

import java.util.LinkedHashSet;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import edu.stedwards.matt.controllers.TemplateEditorController;

/** Template authoring screen. Bracketed text in the editor is surfaced as selectable fields. */
public class TemplatesEditor extends BorderPane {
   private final TextField nameField = new TextField("TemplateName");
   private final HTMLEditor editor = new HTMLEditor();
   private final FlowPane fields = new FlowPane(10, 10);
   private final Set<String> selectedFields = new LinkedHashSet<>();
   private final TemplateEditorController controller;

   public TemplatesEditor(TemplateEditorController controller) {
      this.controller = controller;
      getStyleClass().add("template-editor-screen");
      setPadding(new Insets(34, 42, 30, 42));

      VBox content = new VBox(20);
      content.getStyleClass().add("template-editor-content");
      content.setFillWidth(true);

      HBox title = new HBox(12, new Label("Edit"), nameField);
      title.setAlignment(Pos.CENTER_LEFT);
      title.getStyleClass().add("template-editor-title");
      nameField.setPromptText("Template name");
      nameField.getStyleClass().add("template-name-input");
      HBox.setHgrow(nameField, Priority.ALWAYS);
      content.getChildren().add(title);

      editor.setHtmlText("");
      editor.setMinHeight(300);
      editor.setPrefHeight(470);
      editor.setMaxHeight(Double.MAX_VALUE);
      editor.getStyleClass().add("template-html-editor");
      VBox.setVgrow(editor, Priority.ALWAYS);
      content.getChildren().add(editor);

      Label heading = new Label("Dynamic Fields");
      heading.getStyleClass().add("template-fields-heading");
      fields.setMaxWidth(760);
      fields.setAlignment(Pos.TOP_LEFT);
      ScrollPane fieldScroll = new ScrollPane(fields);
      fieldScroll.setFitToWidth(true);
      fieldScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
      fieldScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
      fieldScroll.setMaxHeight(180);
      fieldScroll.getStyleClass().add("template-fields-scroll");

      HBox footer = new HBox(12);
      footer.setAlignment(Pos.BOTTOM_RIGHT);
      Button save = new Button("Save");
      save.getStyleClass().add("template-save-button");
      Button finish = new Button("Finish  ›");
      finish.getStyleClass().add("template-finish-button");
      footer.getChildren().addAll(save, finish);

      VBox fieldSection = new VBox(12, heading, fieldScroll, footer);
      fieldSection.getStyleClass().add("template-fields-section");
      content.getChildren().add(fieldSection);
      setCenter(content);

      editor.addEventHandler(javafx.scene.input.KeyEvent.KEY_RELEASED, event -> refreshFields());
      editor.focusedProperty().addListener((obs, oldValue, focused) -> {
         if (!focused) refreshFields();
      });
      refreshFields();
   }

   private void refreshFields() {
      String html = editor.getHtmlText();
      Set<String> current = controller.extractDynamicFields(html);
      selectedFields.retainAll(current);
      fields.getChildren().clear();
      for (String field : current) {
         Button chip = new Button(field);
         chip.setMaxWidth(250);
         chip.setEllipsisString("…");
         chip.setTooltip(new javafx.scene.control.Tooltip(field));
         chip.getStyleClass().add("template-field-chip");
         if (selectedFields.contains(field)) chip.getStyleClass().add("selected");
         chip.setOnAction(event -> {
            if (!selectedFields.add(field)) selectedFields.remove(field);
            chip.getStyleClass().remove("selected");
            if (selectedFields.contains(field)) chip.getStyleClass().add("selected");
            refreshHighlights();
         });
         fields.getChildren().add(chip);
      }
   }

   /** Applies a translucent highlight to matching placeholder text in the editor's WebView. */
   private void refreshHighlights() {
      editor.lookupAll(".web-view").stream().findFirst().ifPresent(node -> {
         if (node instanceof javafx.scene.web.WebView webView && webView.getEngine().getDocument() != null) {
            String fieldsArray = selectedFields.stream()
                  .map(this::toJavaScriptString)
                  .collect(java.util.stream.Collectors.joining(",", "[", "]"));
            String script = "(function(fields){var d=document;"
                  + "d.querySelectorAll('.template-field-highlight').forEach(function(s){"
                  + "s.parentNode.replaceChild(d.createTextNode(s.textContent),s);});d.body.normalize();"
                  + "var walker=d.createTreeWalker(d.body,NodeFilter.SHOW_TEXT),nodes=[],n;"
                  + "while(n=walker.nextNode())nodes.push(n);"
                  + "nodes.forEach(function(node){var text=node.nodeValue,matches=[];"
                  + "fields.forEach(function(field){var needle='['+field+']',from=0,at;"
                  + "while((at=text.indexOf(needle,from))!==-1){matches.push({start:at,end:at+needle.length});from=at+needle.length;}});"
                  + "matches.sort(function(a,b){return b.start-a.start;});matches.forEach(function(m){"
                  + "var range=d.createRange();range.setStart(node,m.start);range.setEnd(node,m.end);"
                  + "var mark=d.createElement('span');mark.className='template-field-highlight';"
                  + "mark.style.background='rgba(226,149,120,.35)';try{range.surroundContents(mark);}catch(e){}});"
                  + "});})(" + fieldsArray + ");";
            webView.getEngine().executeScript(script);
         }
      });
   }

   private String toJavaScriptString(String value) {
      return "'" + value.replace("\\", "\\\\").replace("'", "\\'")
            .replace("\r", "\\r").replace("\n", "\\n")
            .replace("\u2028", "\\u2028").replace("\u2029", "\\u2029") + "'";
   }
}
