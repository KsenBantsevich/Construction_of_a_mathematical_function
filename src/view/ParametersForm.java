package view;

import controller.LinearFunction;
import controller.SortFunction;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.concurrent.ConcurrentLinkedQueue;


public class ParametersForm {

    private final Group group = new Group();

    private Integer x = 0;
    private Integer limit;
    private Integer amountOfArrays;
    private Integer n = 1;

    private final ConcurrentLinkedQueue<Integer> numberQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Integer> arrQueue = new ConcurrentLinkedQueue<>();

    private Thread numberCalculationThread = new Thread();
    private Thread arrCalculationThread = new Thread();


    public ParametersForm(GraphicGroup graphicGroup, Table table) {

        setNumberTempLine(graphicGroup);
        setArrTempLine(graphicGroup);

        Label inputRangeXLabel = new Label("                                   x: ");
        TextField inputLimit = new TextField();
        HBox rightThresholdBox = new HBox(inputRangeXLabel, inputLimit);

        Label inputKLabel = new Label("Количество массивов: ");
        TextField inputAmountOfArrays = new TextField();
        HBox KThresholdBox = new HBox(inputKLabel, inputAmountOfArrays);

        Button startBuildButton = new Button("    Рисовать   ");
        Button stopBuildButton = new Button("    Остановить    ");
        Label spaceLabel = new Label("                          ");
        HBox startStopButtonBox = new HBox(spaceLabel, startBuildButton,new Separator(), stopBuildButton);
        startStopButtonBox.setSpacing(15);

        VBox buttonsGroup = new VBox( rightThresholdBox,KThresholdBox, startStopButtonBox);
        buttonsGroup.setSpacing(5);


        startBuildButton.setOnAction(actionEvent -> {
            if (!numberCalculationThread.isAlive()) {
                if (integerCheck(inputAmountOfArrays.getText())) {

                    amountOfArrays = Integer.parseInt(inputAmountOfArrays.getText());

                    if (integerCheck(inputLimit.getText())) {
                        if (Integer.parseInt(inputLimit.getText()) >= x) {
                            limit = Integer.parseInt(inputLimit.getText());
                            numberQueue.clear();
                            table.clearTable();

                            graphicGroup.createNewNumberSeries("2 * x", x, limit);

                            numberCalculationThread = new Thread(new LinearFunction(x, limit, numberQueue));

                            numberCalculationThread.start();
                        }
                        else {
                            errorAlert();
                        }
                    }

                }

                if (!arrCalculationThread.isAlive()) {
                    arrQueue.clear();

                    graphicGroup.createNewWordSeries(limit);
                    arrCalculationThread = new Thread(new SortFunction(limit, amountOfArrays, arrQueue, table));
                    arrCalculationThread.start();
                }
            }
        });

        stopBuildButton.setOnAction(actionEvent -> {
            if (!numberCalculationThread.isInterrupted()) {

                numberCalculationThread.interrupt();
                arrCalculationThread.interrupt();

                numberQueue.clear();
                arrQueue.clear();

                table.clearTable();

                inputLimit.clear();
                inputAmountOfArrays.clear();
            }
        });

        group.getChildren().addAll(buttonsGroup);
    }

    public Group getGroup() {
        return group;
    }

    private void errorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("Impossible to draw a graphic");
        alert.setContentText("Enter the correct data");

        alert.showAndWait();
    }

    private boolean integerCheck(String text) {

        boolean isInteger = false;

        String numberMatcher = "^-?[0-9]*$";
        if (!text.isEmpty()) {
            if (!text.matches(numberMatcher)) {
                errorAlert();
            } else {
                isInteger = true;
            }
        }

        return isInteger;
    }

    private void setNumberTempLine(GraphicGroup chartGroup) {
        Timeline numberTempLine = new Timeline();
        numberTempLine.setCycleCount(Timeline.INDEFINITE);
        numberTempLine.getKeyFrames().add(new KeyFrame(Duration.millis(100),
                actionEvent -> {
                    if (!numberQueue.isEmpty()) {
                        chartGroup.updateNumberSeriesList(x, numberQueue.poll());
                        x += 1;
                    }
                }));
        numberTempLine.play();
    }

    private void setArrTempLine(GraphicGroup chartGroup) {
        Timeline wordTempLine = new Timeline();
        wordTempLine.setCycleCount(Timeline.INDEFINITE);
        wordTempLine.getKeyFrames().add(new KeyFrame(Duration.millis(100),
                actionEvent -> {
                    if (!arrQueue.isEmpty()) {
                        chartGroup.updateWordSeriesList(n, arrQueue.poll());
                        n += 1;
                    }
                }));
        wordTempLine.play();
    }
}
