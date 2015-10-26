package eu.goodlike.minesweeper.mvc.window.game;

import eu.goodlike.minesweeper.MinesweeperApplication;

public class GameWindowModel {

    public GameWindowView createView(int tileSize) {
        return new GameWindowView(this, tileSize);
    }

    public GameWindowController createController(GameWindowView view, MinesweeperApplication app) {
        return new GameWindowController(app, this, view);
    }

    public GameWindowView createViewAndController(int tileSize, MinesweeperApplication app) {
        GameWindowView view = createView(tileSize);
        createController(view, app);
        return view;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getAmountOfBombs() {
        return amountOfBombs;
    }

    public synchronized int incrementFlagCount() {
        return amountOfBombs - ++flagCount;
    }

    public synchronized int decrementFlagCount() {
        return amountOfBombs - --flagCount;
    }

    // CONSTRUCTORS

    public GameWindowModel(int width, int height, int amountOfBombs) {
        if (amountOfBombs < 1 || amountOfBombs > width * height - 9)
            throw new IllegalArgumentException("You can't have " + amountOfBombs + " in a square of " + width + "x" + height);

        this.width = width;
        this.height = height;
        this.amountOfBombs = amountOfBombs;
    }

    // PRIVATE

    private final int width;
    private final int height;
    private final int amountOfBombs;

    private volatile int flagCount;

}
