module com.example.hufmman_hazem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens Hazem_Huffman to javafx.fxml;
    exports Hazem_Huffman;
}