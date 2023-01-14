package Hazem_Huffman;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Driver extends Application {
    private static int headLength = 0;
    private static int actualfileLength;
    private static double rate = 0;
    private static String headerAsString = "";
    private static int frq[];
    private static byte[] bytes_in_file;
    private static int number_different_bytes = 0;
    private static PriorityQueue<HuffmanNode> heap;
    private static HuffmanNode huffmanTreeRoot;
    private static ArrayList<Node> huffmanCodes;
    private static File inputUnCompressionFile, inputDeCompressionFile;
    private static String typeFile = "";
    private static String headerLength = "";
    private static String dataLength = "";
    public void start(Stage stage) throws IOException {
        stage.setTitle("Huffman Project");
        Label title =new Label("Compressor System");
        title.setTextFill(Color.BLACK);
        title.setFont(new Font("Cambria", 40));

        Label compressor = new Label("Compressor");
        compressor.setFont(new Font("Cambria", 20));
        compressor.setTextFill(Color.BLACK);

        Label decompressor = new Label("Decompressor");
        decompressor.setFont(new Font("Cambria", 20));
        decompressor.setTextFill(Color.BLACK);

        Button Exit = new Button("Exit");
        Exit.setTextFill(Color.RED);
        Exit.setTooltip(new Tooltip("exit from application"));
        Exit.setPrefWidth(100);
        Exit.setFont(new Font("Cambria", 15));

        Button compFilChooser = new Button("Enter to select");
        compFilChooser.setFont(new Font("Cambria", 20));
        compFilChooser.setTextFill(Color.BLACK);

        Button deCompFilChooser = new Button("Enter to select");
        deCompFilChooser.setFont(new Font("Cambria", 20));
        deCompFilChooser.setTextFill(Color.BLACK);

        TextArea data_txt =new TextArea("");
        data_txt.setPrefHeight(400);
        data_txt.setFont(new Font("Cambria", 15));


        VBox compBox = new VBox(20, compressor, compFilChooser);
        VBox deCompBox = new VBox(20, decompressor, deCompFilChooser);
        HBox hbox1 = new HBox(70, compBox, deCompBox);
        VBox primaryVBox = new VBox(65, title, hbox1, data_txt, Exit);
        primaryVBox.setBorder(new Border(
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Exit.setOnAction(e -> {
            JOptionPane.showMessageDialog(null, "Thanks to use \nGood-Luck");
            System.exit(0);
        });
        primaryVBox.setAlignment(Pos.CENTER);
        hbox1.setAlignment(Pos.CENTER);

        deCompBox.setAlignment(Pos.CENTER);

        compBox.setAlignment(Pos.CENTER);
        //crate file chooser
        FileChooser compChooser = new FileChooser();
        compChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"), new FileChooser.ExtensionFilter("Java Files", "*.java"),
                new FileChooser.ExtensionFilter("Web Files", "*.html", "*.css", "*.js", "*.php"),
                new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg"), new FileChooser.ExtensionFilter("Word files", "*.docx"),
                new FileChooser.ExtensionFilter("Pdf files", "*.pdf")

        );

        //crate Event handler to Comp file Chooser
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {

                // get the file selected
                inputUnCompressionFile = compChooser.showOpenDialog(stage);// select file to compress

                if (inputUnCompressionFile != null) {

                    comparison(stage, compBox, deCompBox, data_txt);
                }
            } // call method compression

        };
        compFilChooser.setOnAction(event);

        FileChooser decompFileChooser = new FileChooser();

        decompFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Huffman Files", "*.huff"));
        //crate Event handler to deComp file Chooser
        EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {

                // get the file selected
                inputDeCompressionFile = decompFileChooser.showOpenDialog(stage);// selected file to decompress..

                if (inputDeCompressionFile != null) {

                    data_txt.setText("Decompression \n" + inputDeCompressionFile.getAbsolutePath() + "  selected");

                    try {
                        deCompressionFile(stage, deCompBox, compBox);
                    } catch (FileNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        };

        deCompFilChooser.setOnAction(event2);

        deCompFilChooser.setOnAction(event2);
        StackPane stack_pane = new StackPane(primaryVBox);
        Scene scene = new Scene(stack_pane, 700, 680);
        stage.setScene(scene);
        stage.show();
    }
    private void deCompressionFile(Stage stage, VBox vb, VBox vb2) throws FileNotFoundException {
        if (vb.getChildren().size() > 2) {
            vb.getChildren().remove(2);
            vb2.getChildren().remove(2);
        }
        Button saveFile = new Button("Save file");
        saveFile.setFont(new Font("Cambria", 20));
        saveFile.setTextFill(Color.BLACK);
        vb.getChildren().add(saveFile);
        BinaryStreamIn binaryReader = new BinaryStreamIn(inputDeCompressionFile);
        saveFile.setOnAction(e->{
            splitHeader(binaryReader);
            FileChooser fileChooser = new FileChooser();

            // Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    typeFile + " files (*." + typeFile + ")", "*." + typeFile);
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(stage);
            if (file != null){
                FileOutputStream out = null;
                try {
                out = new FileOutputStream(file);
                BinaryStreamOut bout = new BinaryStreamOut(out);

                Long longDataLength = Long.parseLong(dataLength);

                int length = Integer.parseInt(headerLength);

                StringBuilder headerAsStringBuilder = new StringBuilder("");
                    for (int i = 0; i < length; i++) {
                        boolean b = binaryReader.readBoolean();
                        if (b)
                            headerAsStringBuilder.append("1");
                        else
                            headerAsStringBuilder.append("0");
                    }
                    headerAsString = headerAsStringBuilder.toString();
                    HuffmanNode root = decodeHeader();
                        for (int i = 0; i < longDataLength; i++) {
                            HuffmanNode node = root;
                            while (!node.isLeaf()) {
                                boolean bit = binaryReader.readBoolean();
                                if (bit)
                                    node = node.getRight();
                                else
                                    node = node.getLeft();
                            }
                            bout.write(node.getVal());
                        }
                        bout.close();

                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException ioe) {
                        System.out.println("Error while closing stream: " + ioe);
                    }

                }
            }
        });
    }
    private static String peek(int i) {
        if (i > headerAsString.length())
            return "";
        String r = headerAsString.substring(0, i);
        headerAsString = headerAsString.substring(i);
        return r;
    }
    private static HuffmanNode decodeHeader() {
        String st = peek(1);
        boolean n = st.equals("");
        if (n)
            return null;
        boolean isLeaf = st.equals("1");
        if (isLeaf) {
            byte b = (byte) ((Integer.parseInt(peek(8), 2)) - 128);
            return new HuffmanNode(b, -1, null, null, true);
        }
        return new HuffmanNode(decodeHeader(), decodeHeader());
    }
    private void splitHeader(BinaryStreamIn binaryRead) {

        boolean n = true;

        while (n) {
            char c = (char) binaryRead.readByte();
            if (c == ':')
                n = false;
            else
                typeFile += c;
        }
        n = true;
        while (n) {
            char c = (char) binaryRead.readByte();
            if (c == ':')
                n = false;
            else
                headerLength += c;
        }

        n = true;
        while (n) {
            char c = (char) binaryRead.readByte();
            if (c == ':')
                n = false;
            else
                dataLength += c;
        }

    }
    private void comparison(Stage stage, VBox vb, VBox vb2, TextArea Data_txt){
        number_different_bytes = 0;
        if (vb.getChildren().size() > 2) {
            vb.getChildren().remove(2);
            vb2.getChildren().remove(2);
        }
        Button saveFile = new Button("Save file");
        saveFile.setFont(new Font("Cambria", 20));
        saveFile.setTextFill(Color.BLACK);
        String name_of_file = inputUnCompressionFile.getName().split("\\.")[1]; //get name file
        readFile(inputUnCompressionFile); //fill array bytes,frq,number_diff_char
        vb.getChildren().addAll(saveFile);
        Data_txt.setText(" _________________________________Input_File_Info_______________________________________"
                + "\nFile Path:" + inputUnCompressionFile.getAbsolutePath() + "  selected"
                + "\nFile length :" + inputUnCompressionFile.length() + "\nNumber of Distinguished Charcter: "
                + number_different_bytes
                +  "\n _________________________________________________________________________________________");

        saveFile.setOnAction(e->{
            FileChooser fileChooser =new FileChooser();
            //set extenstion filter to just huff
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("huff files (*.huff)", "*.huff");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showSaveDialog(stage);
            if(file != null){
                FileOutputStream outputStream =null ; //write in file as stream of bytes
                try {
                    outputStream =new FileOutputStream(file); //binary and bit stream to write in file
                    BinaryStreamOut binaryStream = new BinaryStreamOut(outputStream);
                    BitOutputStream bitOutputStream = new BitOutputStream(outputStream); // write string in file
                    initializePriorityQueue();
                    buildHuffmanTree();
                    getHeaderLength(huffmanTreeRoot);
                    bitOutputStream.writeH(new StringBuilder(name_of_file + ":" + headLength + ":"));
                    buildHeader(huffmanTreeRoot,binaryStream);
                    huffmanCodes = addCode(huffmanTreeRoot);
                    writeCompressedData(bitOutputStream,binaryStream);
                    actualfileLength = (int) file.length();  // length of new file
                    rate = ((double) (bytes_in_file.length - actualfileLength) / bytes_in_file.length) * 100;
                    String ratio = rate + "";
                    if (ratio.length() > 5) {
                        ratio = ratio.substring(0, 5);
                    }

                    Data_txt.setText(
                            " ________________________________Input_File_Info_________________________________________"
                                    + "\nInput File information" + "\nFile Path:"
                                    + inputUnCompressionFile.getAbsolutePath() + "  selected" + "\nFile length :"
                                    + inputUnCompressionFile.length() + "\nNumber of Distinguished Charcter: "
                                    + number_different_bytes
                                    + "\n ___________________________Compressdd_File_Info______________________________________________"
                                    + "\nCompressed File Information " + "\nFile Head Length : " + headLength
                                    + "\n Actual Data Length : " + actualfileLength + "\nCompression Rate : " + ratio
                                    + "%"
                                    + "\n __________________________________THE TREE__________________________________________");
                    Data_txt.setText(Data_txt.getText() + "\n" + "byte  --> frequancy --> Huffman code" + "\n");
                    for (int i = 0; i < huffmanCodes.size(); i++) {
                        Data_txt.setText(Data_txt.getText() + "\n(" + (char) huffmanCodes.get(i).getVal().getVal() + ") --> "
                                + huffmanCodes.get(i).getVal().getFreq() + " --> " + huffmanCodes.get(i).gethCode()
                                + "\n");
                    }

                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException ioe) {
                        System.out.println("Error while closing stream: " + ioe);
                    }

                }
            }
        });

    }
    private void writeCompressedData(BitOutputStream bitStream, BinaryStreamOut binaryStreamOut) throws IOException {

        StringBuilder s = new StringBuilder("");

        for (int i = 0; i < bytes_in_file.length; i++) //Covert all bytes in file to the new huffman code
            for (int j = 0; j < huffmanCodes.size(); j++)
                if ((bytes_in_file[i]) == huffmanCodes.get(j).getVal().getVal()) {
                    s.append(huffmanCodes.get(j).gethCode());
                    break;
                }
        bitStream.writeH(new StringBuilder(bytes_in_file.length + ":"));

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1')
                binaryStreamOut.write(true);
            else if (s.charAt(i) == '0')
                binaryStreamOut.write(false);
        }
        // After write the file length in the compressed file, we Wrote the header.
        binaryStreamOut.close();  // print header + compress data
    }
    private static ArrayList<Node> addCode(HuffmanNode root) {
        ArrayList<Node> huffmanCodes = new ArrayList<Node>();
        String s = ("");
        addCode(root, s, huffmanCodes);
        return huffmanCodes;
    }
    private static void addCode(HuffmanNode root, String s, ArrayList<Node> huffmanCodes) {
        if (!(root.isLeaf())) {
            String sl = s + "0"; // 0 in left
            String sr = s + "1"; // 1 in right
            addCode(root.getLeft(), sl, huffmanCodes);
            addCode(root.getRight(), sr, huffmanCodes);
        } else {
            huffmanCodes.add(new Node(s.toString(), root)); // add node of list with new code
        }
    }
    private static void buildHeader(HuffmanNode huffmanTreeRoot, BinaryStreamOut bos) throws IOException {
        if (huffmanTreeRoot.isLeaf()) { //if node leaf print "1 value of node " else print "0"
            bos.write(true);
            String byteInBinaryAsString = convertByteToBitString(huffmanTreeRoot.getVal());
            for (int i = 0; i < byteInBinaryAsString.length(); i++) {
                if (byteInBinaryAsString.charAt(i) == '1')
                    bos.write(true);
                else
                    bos.write(false);
            }
            return;
        }
        else
            bos.write(false);
        buildHeader(huffmanTreeRoot.getLeft(), bos);
        buildHeader(huffmanTreeRoot.getRight(), bos);
    }
    private static String convertByteToBitString(byte b) { // convert byte to bits in string
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; --i) {
            sb.append(b >>> i & 1);
        }
        return sb.toString();
    }
    private static void getHeaderLength(HuffmanNode huffmanTreeRoot) {
        if (huffmanTreeRoot == null)
            return;
        if (huffmanTreeRoot.isLeaf()) {
            headLength += 9;
            return;
        } else {
            headLength++;
            getHeaderLength(huffmanTreeRoot.getLeft());
            getHeaderLength(huffmanTreeRoot.getRight());
        }
    }
    private static  void readFile(File file){
        bytes_in_file =new byte[(int)file.length()];
        //read file in bytes
        try(FileInputStream inputStream= new FileInputStream(file)) {
            inputStream.read(bytes_in_file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frq = new int[256];
        for (int i = 0; i < bytes_in_file.length; i++)
            frq[bytes_in_file[i] + 128]++; //byte can take is from -128 to 127.

        for (int i = 0; i < frq.length; i++)
            if (frq[i] > 0)
                number_different_bytes++;   // if freq[i] > 0 this mean a new byte in file

    }
    private static void initializePriorityQueue() { // Time = O(n*log n)
        heap = new PriorityQueue<HuffmanNode>();

        for (int i = 0; i < frq.length; i++)
            if (frq[i] > 0)
                heap.add(new HuffmanNode(frq[i], (byte) i, true));
    }
    private static void buildHuffmanTree() {  // n*log n
        while (heap.size() > 1) {
            HuffmanNode node = new HuffmanNode(0);
            HuffmanNode left = (HuffmanNode) heap.poll(); //log n
            HuffmanNode right = (HuffmanNode) heap.poll();
            node.addLift(left);
            node.addRight(right);
            node.setFreq(left.getFreq()+right.getFreq());
            heap.add(node);
        }

        huffmanTreeRoot = (HuffmanNode) heap.peek();

    }

    public static void main(String[] args) {
        launch();
    }
}