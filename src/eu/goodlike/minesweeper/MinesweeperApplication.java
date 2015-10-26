package eu.goodlike.minesweeper;

import eu.goodlike.minesweeper.mvc.window.game.GameWindowModel;
import eu.goodlike.minesweeper.mvc.window.game.GameWindowView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperApplication extends Application {

    public static final double DEFAULT_FONT_SIZE = 30;
    public static final double INVISIBLE = 0;
    public static final double VISIBLE = 1;
    public static final double DEFAULT_FADE_DURATION = 0.13;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        newGameWindow(primaryStage);
    }

    @Override
    public void stop() {
        interruptAllLooseThreads();
    }

    public void reset() {
        reset(null);
    }

    public void reset(Object eventOrSimilar) {
        primaryStage.close();
        interruptAllLooseThreads();
        newGameWindow(primaryStage);
    }

    public synchronized void registerThreadForInterruption(Thread thread) {
        threads.add(thread);
    }

    public synchronized void interruptAllLooseThreads() {
        threads.forEach(Thread::interrupt);
        threads.clear();
    }

    // PRIVATE

    private Stage primaryStage;
    private final List<Thread> threads = new ArrayList<>();

    private void newGameWindow(Stage primaryStage) {
        GameWindowModel gameWindowModel = new GameWindowModel(30, 16, 99);
        GameWindowView gameWindowView = gameWindowModel.createViewAndController(50, this);

        primaryStage.setScene(new Scene(gameWindowView.getRoot()));
        primaryStage.setTitle("Shitty minesweeper!");
        primaryStage.show();
    }

    // LAUNCH
    public static void main(String[] args) {
        launch(args);
    }
}
