module com.puproject.cgprojectdenislavlyubomir {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.puproject.cgprojectdenislavlyubomir to javafx.fxml;
    exports com.puproject.cgprojectdenislavlyubomir;
}