package com.example.proj_1;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        firstPage(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    public void viewGamePage(Stage stage, int[] Coins, int[] sequenceChosenIndex, int[][] chosenTable,int [][] dpTable) {
        Group root = new Group();
        VBox buttons = new VBox();
        buttons.setSpacing(100);
        buttons.setTranslateX(5);
        buttons.setTranslateY(50);
        Scene scene = new Scene(root, 1000, 600, Color.LIGHTBLUE);
        Button Play = new Button("Play");
        Button Table = new Button(" Table");
        Button Relation = new Button("Relation");
        buttonDesign(Play);
        buttonDesign(Table);
        buttonDesign(Relation);
        Play.setOnAction((ActionEvent e) -> {
            Group root_1 = new Group();
            Stage stage1 = new Stage();
            Scene scene1 = new Scene(root_1, 1250, 690, Color.LIGHTBLUE);
            HBox Coin_box = new HBox();
            Coin_box.setSpacing(20);
            Coin_box.setAlignment(Pos.CENTER);
            Coin_box.setTranslateX(200);
            Coin_box.setTranslateY(110);
            Label user = new Label("ME:");
            user.setFont(new Font("Cambria", 30));
            user.setTranslateX(10);
            user.setTranslateY(50);
            Label opponent = new Label("Opponent:");
            opponent.setFont(new Font("Cambria", 30));
            opponent.setTranslateX(10);
            opponent.setTranslateY(200);
            Circle[] Circles = new Circle[Coins.length];
            StackPane[] Coin_stackPane = new StackPane[Coins.length];
            Label[] arr_lable = new Label[Coins.length];
            for (int i = 0; i < Coins.length; i++) {
                Coin_stackPane[i] = new StackPane();
                Circles[i] = new Circle();
                arr_lable[i] = new Label(String.valueOf(Coins[i]));
                Circles[i].setRadius(40);
                Circles[i].setFill(Color.GOLD);
                arr_lable[i].setFont(new Font("Cambria", 30));
                arr_lable[i].setTextFill(Color.BLACK);
                Coin_stackPane[i].getChildren().addAll(Circles[i], arr_lable[i]);
            }
            for (int i = 0; i < Coin_stackPane.length; i++)
                Coin_box.getChildren().addAll(Coin_stackPane[i]);
            TranslateTransition[] translate = new TranslateTransition[sequenceChosenIndex.length];
            PauseTransition pt = new PauseTransition(Duration.seconds(1));// pause Between transitions
            SequentialTransition sq = new SequentialTransition();
            sq.getChildren().add(pt);
            int opponent_resuilt = 0;
            for (int i = 0; i < sequenceChosenIndex.length; i++) {
                translate[i] = new TranslateTransition();
                translate[i].setDuration(Duration.seconds(1));
                translate[i].setNode(Coin_stackPane[sequenceChosenIndex[i]]);
                if (i % 2 == 0)
                    translate[i].setByY(-70);
                else {
                    translate[i].setByY(+70);
                    opponent_resuilt = opponent_resuilt + Coins[sequenceChosenIndex[i]];
                }
                sq.getChildren().add(translate[i]);
            }
            int finalOpponent_resuilt = opponent_resuilt;
            sq.setOnFinished((ActionEvent l) -> {
                Button play_again = new Button("Play again");
                buttonDesign(play_again);
                HBox Result_box = new HBox();
                Text res_me = new Text("MY RESULT : " + String.valueOf(getMaxResult(dpAlgorithm(Coins))));
                Text res_opponent = new Text("OPPONENT RESUILT : " + finalOpponent_resuilt);
                res_me.setFont(new Font(35));
                res_opponent.setFont(new Font(35));
                res_me.setFill(Color.GREEN);
                res_opponent.setFill(Color.RED);
                Result_box.setTranslateX(40);
                Result_box.setTranslateY(500);
                Result_box.setSpacing(170);
                play_again.setOnAction((ActionEvent again) -> {
                    stage1.close();
                    firstPage(stage);
                });
                Result_box.getChildren().addAll(res_me, res_opponent, play_again);
                root_1.getChildren().add(Result_box);
            });
            sq.play();
            root_1.getChildren().addAll(Coin_box, user, opponent);
            stage1.setScene(scene1);
            stage1.show();
        });
        Table.setOnAction((ActionEvent e) -> {
            printDpTable(dpAlgorithm(Coins), Coins, chosenTable);
        });
        Relation.setOnAction((ActionEvent e) -> {
            Label label = new Label("DP[i][j]= 0               If j <  i\n" + "DP[i][j] = Coin[i]             If j == i\n" +
                    "DP[i][j] = max(Coin[i], Coin[j])        If j == i + 1\n" +

                    "DP[i][j] = Max(Coin[i] + min(DP[i+2][j], DP[i+1][j-1] ), " +
                    "Coin[j] + min(DP[i+1][j-1], DP[i][j-2] ))");
            label.setTranslateX(20);
            label.setTranslateY(450);
            label.setFont(new Font("Arial", 24));
            label.setTextFill(Color.BLACK);
            label.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            label.setPadding(new Insets(10));
            root.getChildren().add(label);
        });
        buttons.getChildren().addAll(Play, Table, Relation);
        root.getChildren().addAll(buttons);
        stage.setScene(scene);
        stage.show();
    }

    public int[][] dpAlgorithm(int[] Coins) {  // Time = O(n^2)   space = O(n^2)
        int[][] dpTable = new int[Coins.length][Coins.length];
        for (int i = 0; i < Coins.length; i++) {
            dpTable[i][i] = Coins[i];   //dpTable[i][j]= coin[i] or coin [j] when i=j
        }
        for (int gap = 2; gap <= Coins.length; gap++) {
            for (int i = 0; i < Coins.length - gap + 1; ++i) // when gap = 2 we -> j = i + 1
            {
                int j = i + gap - 1;
                if (j == i + 1)
                    dpTable[i][j] = Math.max(Coins[i], Coins[j]); //dpTable[i][j] = max(coin[i],coin[j]) when j =i + 1
                else
                    dpTable[i][j] = Math.max((Coins[i] + Math.min(dpTable[i + 2][j], dpTable[i + 1][j - 1])),
                            (Coins[j] + Math.min(dpTable[i + 1][j - 1], dpTable[i][j - 2])));
            }
        }
        return dpTable;
    }

    public int[][] getChosenTable(int[] Coins, int[][] dpTable) {
        int[][] chossenTable = new int[Coins.length][Coins.length];
        for (int i = 0; i < Coins.length; i++) {
            chossenTable[i][i] = i;
        }
        for (int gap = 2; gap <= Coins.length; gap++) {
            for (int i = 0; i < Coins.length - gap + 1; ++i) {
                int j = i + gap - 1;
                if (j == i + 1) {
                    if (Coins[i] > Coins[j])
                        chossenTable[i][j] = i;
                    else
                        chossenTable[i][j] = j;
                } else {
                    if ((Coins[i] + Math.min(dpTable[i + 2][j], dpTable[i + 1][j - 1])) >= (Coins[j] + Math.min(dpTable[i + 1][j - 1], dpTable[i][j - 2])))
                        chossenTable[i][j] = i;
                    else
                        chossenTable[i][j] = j;
                }
            }
        }
        return chossenTable;
    }

    public int getMaxResult(int[][] dpTable) {
        int max = dpTable[0][dpTable.length - 1];
        return max;
    }

    public void printDpTable(int[][] dpTable, int[] Coins, int[][] chosenTable) {
        Group root = new Group();
        Scene scene = new Scene(root, 600, 300, Color.LIGHTBLUE);
        Stage stage = new Stage();
        Line lineX = new Line(0, 26, 1000, 26);
        Line lineY = new Line(35, 0, 35, 600);
        for (int i = 0; i < Coins.length; i++) {
            Label coinX = new Label(String.valueOf(Coins[i]));
            coinX.setFont(new Font("Cambria", 20));
            coinX.setTextFill(Color.BLACK);
            coinX.setTranslateY(2);
            coinX.setTranslateX(60 + (i * 70));
            Label coinY = new Label(String.valueOf(Coins[i]));
            coinY.setFont(new Font("Cambria", 20));
            coinY.setTextFill(Color.BLACK);
            coinY.setTranslateY(30 + (i * 40));
            coinY.setTranslateX(2);
            root.getChildren().addAll(coinX, coinY);
        }
        for (int i = 0; i < dpTable.length; i++) {
            for (int j = 0; j < dpTable.length; j++) {
                Label index = new Label(String.valueOf(dpTable[j][i]) + "|" + String.valueOf(chosenTable[j][i]));
                index.setTranslateX(50 + (i * 70));
                index.setTranslateY(30 + (j * 40));
                index.setFont(new Font("Cambria", 20));
                index.setTextFill(Color.BLACK);
                root.getChildren().add(index);
            }
        }
        root.getChildren().addAll(lineX, lineY);
        stage.setScene(scene);
        stage.show();
    }

    public int[] getSequenceChosenIndex(int[][] chosenTable, int[] Coins) {
        int[] indx_arr = new int[(Coins.length)];
        int i = 0;
        int j = Coins.length - 1;
        int contuer = 0;
        while (contuer <= Coins.length - 1) {
            if (chosenTable[i][j] == i) {
                indx_arr[contuer] = i;
                i++;  //if we choose from right we increment i
            } else {
                indx_arr[contuer] = j;
                j--; //if we choose from left we decrement j
            }
            contuer++;
        }
        return indx_arr;
    }

    // interFace method
    public void buttonDesign(Button button) {
        button.setFont(new Font("Cambria", 30));
        button.setTextFill(Color.CYAN);
        button.setPrefWidth(200);
        button.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), Insets.EMPTY)));
        DropShadow shadow = new DropShadow(10, Color.RED);
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            button.setEffect(shadow);
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            button.setEffect(null);
        });
    }

    public static void warningMessage(String x) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setContentText(x);
        alert.show();
    }

    public void firstPage(Stage stage) {
        TextField inputField = new TextField();
        inputField.setPrefWidth(1000);
        inputField.setPromptText("Enter the Coins(The Coins number must be even,separated by space)");
        inputField.setFocusTraversable(false);
        inputField.setFont(new Font("Cambria", 30));
        Button submitButton = new Button("Submit");
        buttonDesign(submitButton);
        submitButton.setOnAction(event -> {
            String[] stringArray = inputField.getText().split(" ");
            int[] coinArray = new int[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
                coinArray[i] = Integer.parseInt(stringArray[i]);
            }
            if (coinArray.length % 2 == 1) {
                warningMessage("The number of Coins odd try Again");
            } else {
                int[][] dpTable= dpAlgorithm(coinArray);
                int[][] chosenTable = getChosenTable(coinArray, dpTable);
                int[] sequenceChosenIndex = getSequenceChosenIndex(chosenTable, coinArray);
                stage.close();
                viewGamePage(stage, coinArray, sequenceChosenIndex, chosenTable,dpTable);
            }
            stage.show();
        });
        HBox root = new HBox(inputField, submitButton);
        root.setSpacing(50);
        Scene scene = new Scene(root, 1200, 120, Color.LIGHTBLUE);
        stage.setScene(scene);
        stage.show();
    }
}




