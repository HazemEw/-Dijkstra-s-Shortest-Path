module com.example.proj_1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.proj_1 to javafx.fxml;
    exports com.example.proj_1;
}