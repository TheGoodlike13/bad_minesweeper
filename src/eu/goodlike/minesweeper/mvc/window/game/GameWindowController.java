package eu.goodlike.minesweeper.mvc.window.game;

import eu.goodlike.minesweeper.MinesweeperApplication;
import eu.goodlike.minesweeper.mvc.tile.TileController;
import eu.goodlike.minesweeper.mvc.tile.TileModel;
import eu.goodlike.minesweeper.mvc.tile.TileView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GameWindowController {

    public Stream<TileController> getTileControllers() {
        return tileControllers.stream();
    }

    public void seedBombs(TileController source) {
        disableControls();
        List<TileController> safeTiles = getSurroundingTileControllers(source).collect(Collectors.toList());
        safeTiles.add(source);
        List<TileController> copyOfTiles = getTileControllers().filter(tile -> !safeTiles.contains(tile)).collect(Collectors.toList());
        Collections.shuffle(copyOfTiles);

        IntStream.range(0, gameWindowModel.getAmountOfBombs())
                .mapToObj(copyOfTiles::get)
                .forEach(TileController::setUpUsTheBomb);

        startTimer();
    }

    public void handleLoss(TileController cause) {
        endTimer();
        disableControls();
        showAllBombs(cause);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("LOSS");
        alert.setHeaderText("Unfortunate! Please, try again!");
        alert.setContentText("YOU LOSE! :(");
        //alert.setOnCloseRequest(app::reset);
        alert.show();
    }

    public void checkForVictory() {
        if (flaggedAllAndOnlyBombs() || allSafeTilesAreRevealed())
            handleVictory();
    }

    public Stream<TileController> getSurroundingTileControllers(TileController center) {
        return getTilesAroundBasedOn(center, (i, j) -> i != 0 || j != 0);
    }

    public Stream<TileController> getAdjacentTileControllers(TileController center) {
        return getTilesAroundBasedOn(center, (i, j) -> (i == 0 && j != 0) || (i != 0 && j == 0));
    }

    public void incrementFlagCount() {
        int remainingFlagCount = gameWindowModel.incrementFlagCount();
        gameWindowView.setFlagCount(remainingFlagCount);

        if (remainingFlagCount == 0)
            checkForVictory();
    }

    public void decrementFlagCount() {
        int remainingFlagCount = gameWindowModel.decrementFlagCount();
        gameWindowView.setFlagCount(remainingFlagCount);

        if (remainingFlagCount == 0)
            checkForVictory();
    }

    // CONSTRUCTORS

    public GameWindowController(MinesweeperApplication app, GameWindowModel gameWindowModel, GameWindowView gameWindowView) {
        this.app = app;

        this.gameWindowModel = gameWindowModel;
        this.gameWindowView = gameWindowView;

        tileControllers = IntStream.range(0, gameWindowModel.getHeight())
                .mapToObj(y -> IntStream.range(0, gameWindowModel.getWidth()).mapToObj(x -> createTileController(x, y)))
                .flatMap(Function.identity())
                .collect(Collectors.toList());

        gameWindowView.getResetButton().setOnMouseClicked(this::resetConfirmDialog);

        timerThread = new Thread(() -> {
            int secondsPassed = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                gameWindowView.setTime(++secondsPassed);
            }
        });
        app.registerThreadForInterruption(timerThread);
    }

    // PRIVATE

    private final MinesweeperApplication app;

    private final GameWindowModel gameWindowModel;
    private final GameWindowView gameWindowView;

    private final List<TileController> tileControllers;

    private final Thread timerThread;

    private TileController createTileController(int x, int y) {
        TileModel tile = new TileModel();
        TileView tileView = gameWindowView.addTileViewAt(x, y);
        return new TileController(this, tile, tileView);
    }

    private void resetConfirmDialog(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset confirmation");
        alert.setHeaderText("You are about to reset the game!");
        alert.setContentText("Are you sure?");
        alert.showAndWait().filter(button -> button == ButtonType.OK).ifPresent(app::reset);
    }

    private void handleVictory() {
        endTimer();
        disableControls();
        showAllFields();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("VICTORY");
        alert.setHeaderText("Congratulations! Nice job!");
        alert.setContentText("YOU WIN! :)");
        //alert.setOnCloseRequest(app::reset);
        alert.show();
    }

    private void disableControls() {
        getTileControllers().forEach(TileController::disableControls);
    }

    private void showAllFields() {
        showAllBombs();
        getTileControllers().forEach(TileController::revealTile);
    }

    private boolean flaggedAllAndOnlyBombs() {
        return everyFlaggedFieldIsBomb() && everyBombFieldIsFlagged();
    }

    private boolean everyFlaggedFieldIsBomb() {
        return getTileModels().filter(TileModel::isFlagged).allMatch(TileModel::isBomb);
    }

    private boolean everyBombFieldIsFlagged() {
        return getTileModels().filter(TileModel::isBomb).allMatch(TileModel::isFlagged);
    }

    private boolean allSafeTilesAreRevealed() {
        return getTileModels().filter(TileModel::isSafe).allMatch(TileModel::isRevealed);
    }

    private Stream<TileController> getControllersForBombs() {
        return getTileControllers().filter(controller -> controller.getTile().isBomb());
    }

    private void showAllBombs() {
        getControllersForBombs().forEach(TileController::showBomb);
    }

    private void showAllBombs(TileController cause) {
        getControllersForBombs().filter(controller -> controller != cause).forEach(TileController::showBomb);
    }

    private int getX(int index) {
        return index / gameWindowModel.getWidth();
    }

    private int getY(int index) {
        return index % gameWindowModel.getWidth();
    }

    private int getIndex(int x, int y) {
        return x * gameWindowModel.getWidth() + y;
    }

    private Optional<TileController> getTileAt(int x, int y) {
        if (!withinBoundsX(x) || !withinBoundsY(y))
            return Optional.empty();

        return Optional.of(tileControllers.get(getIndex(x, y)));
    }

    private boolean withinBoundsX(int coordinate) {
        return coordinate >= 0 && coordinate < gameWindowModel.getHeight();
    }

    private boolean withinBoundsY(int coordinate) {
        return coordinate >= 0 && coordinate < gameWindowModel.getWidth();
    }

    private Stream<TileController> getTilesAroundBasedOn(TileController center, BiFunction<Integer, Integer, Boolean> coordinateCondition) {
        int index = tileControllers.indexOf(center);
        int x = getX(index);
        int y = getY(index);

        return tileOffsets()
                .mapToObj(i -> tileOffsets()
                        .filter(j -> coordinateCondition.apply(i, j))
                        .mapToObj(j -> getTileAt(x + i, y + j))
                        .filter(Optional::isPresent)
                        .map(Optional::get))
                .flatMap(Function.identity());
    }

    private Stream<TileModel> getTileModels() {
        return getTileControllers().map(TileController::getTile);
    }

    private IntStream tileOffsets() {
        return IntStream.rangeClosed(-1, 1);
    }

    public void startTimer() {
        timerThread.start();
    }

    public void endTimer() {
        timerThread.interrupt();
    }

}
