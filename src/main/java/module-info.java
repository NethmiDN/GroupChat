module com.example.groupchat {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.groupchat to javafx.fxml;
    exports com.example.groupchat;
}