package models;

import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import models.Conversation;

public class ConversationListCell extends ListCell<Conversation> {
    private VBox content;
    private Label titleLabel;
    private Label statusLabel;

    public ConversationListCell() {
        content = new VBox(5);
        titleLabel = new Label();
        statusLabel = new Label();
        
        content.getChildren().addAll(titleLabel, statusLabel);
        content.setPadding(new Insets(5, 10, 5, 10));
    }

    @Override
    protected void updateItem(Conversation conversation, boolean empty) {
        super.updateItem(conversation, empty);

        if (empty || conversation == null) {
            setGraphic(null);
        } else {
            titleLabel.setText("Conversation #" + conversation.getConversationId());
            statusLabel.setText(conversation.getIsResolved() ? "Resolved" : "Active");
            
            // Style based on status
            if (conversation.getIsResolved()) {
                statusLabel.setStyle("-fx-text-fill: #28a745;"); // Green for resolved
            } else {
                statusLabel.setStyle("-fx-text-fill: #007bff;"); // Blue for active
            }
            
            setGraphic(content);
        }
    }
}