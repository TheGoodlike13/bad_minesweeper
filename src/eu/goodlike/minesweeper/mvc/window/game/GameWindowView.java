package eu.goodlike.minesweeper.mvc.window.game;

import eu.goodlike.minesweeper.mvc.tile.TileView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static eu.goodlike.minesweeper.MinesweeperApplication.DEFAULT_FONT_SIZE;

public class GameWindowView {

    public Pane getRoot() {
        return root;
    }

    public TileView addTileViewAt(int x, int y) {
        TileView tileView = new TileView(tileSize);
        tileView.setTranslateX(tileSize * x);
        tileView.setTranslateY(tileSize * y);
        gameField.getChildren().add(tileView);
        return tileView;
    }

    public void setFlagCount(int flagCount) {
        flagsText.setText(String.valueOf(flagCount));
    }

    public void setTime(int secondsPassed) {
        timerText.setText(String.valueOf(secondsPassed));
    }

    public Button getResetButton() {
        return resetButton;
    }

    // CONSTRUCTORS

    public GameWindowView(GameWindowModel model, int tileSize) {
        this.tileSize = tileSize;

        int width = model.getWidth();
        int height = model.getHeight();

        root = new BorderPane();

        timerText = new Text("0");
        timerText.setFont(Font.font(DEFAULT_FONT_SIZE));
        flagsText = new Text(String.valueOf(model.getAmountOfBombs()));
        flagsText.setFont(Font.font(DEFAULT_FONT_SIZE));

        resetButton = new Button("RESET");
        resetButton.setPrefSize(tileSize * 3, tileSize);
        resetButton.setFont(Font.font(DEFAULT_FONT_SIZE));

        root.setTop(infoPane());

        gameField = new Pane();
        gameField.setPrefSize(width * tileSize, height * tileSize);

        root.setCenter(gameField);
    }

    // PRIVATE

    private final BorderPane root;
    private final Pane gameField;

    private final Text timerText;
    private final Text flagsText;

    private final Button resetButton;

    private final int tileSize;

    private HBox infoPane() {
        HBox infoPane = new HBox();

        Text timePaneText = new Text("Time: ");
        timePaneText.setFont(Font.font(DEFAULT_FONT_SIZE));

        Text flagPaneText = new Text("Mines: ");
        flagPaneText.setFont(Font.font(DEFAULT_FONT_SIZE));

        infoPane.setAlignment(Pos.CENTER);

        infoPane.getChildren().addAll(fillerRegion(), timePaneText, timerPane(), fillerRegion(), resetButton, fillerRegion(), flagPaneText, flagPane(), fillerRegion());
        return infoPane;
    }

    private Region fillerRegion() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private StackPane timerPane() {
        return new StackPane(genericRectangle(), timerText);
    }

    private StackPane flagPane() {
        return new StackPane(genericRectangle(), flagsText);
    }

    private Rectangle genericRectangle() {
        Rectangle rectangle = new Rectangle(tileSize << 1, tileSize, Color.WHITE);
        rectangle.setStroke(Color.BLACK);
        return rectangle;
    }

}
