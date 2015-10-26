package eu.goodlike.minesweeper.mvc.tile;

import eu.goodlike.minesweeper.mvc.window.game.GameWindowController;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class TileController {

    public TileModel getTile() {
        return tile;
    }

    public void setUpUsTheBomb() {
        tile.setBomb();
        gameWindowController.getSurroundingTileControllers(this)
                .map(TileController::getTile)
                .filter(TileModel::isSafe)
                .forEach(TileModel::incrementBombCount);
    }

    public void disableControls() {
        tileView.setOnMouseClicked(null);
    }

    public void enableControls() {
        tileView.setOnMouseClicked(this::handleMouseClick);
    }

    public void showBomb() {
        if (tile.isBomb()) {
            tile.reveal();
            transitionBasedOnTileMark()
                    .noMarkText("X").noMarkColor(Color.DARKRED)
                    .flagText("FX").flagColor(Color.GREEN)
                    .questionText("?X").questionColor(Color.LIGHTBLUE);
        }
    }

    public void revealTile() {
        if (tile.isHidden()) {
            int bombsAround = tile.reveal();
            switch (bombsAround) {
                case TileModel.BOMB:
                    tileView.transitionTextAndColor("X", Color.RED);
                    gameWindowController.handleLoss(this);
                    break;
                case 0:
                    handleReveal();
                    break;
                default:
                    handleReveal(bombsAround);
            }
        }
    }

    // CONSTRUCTORS

    public TileController(GameWindowController gameWindowController, TileModel tile, TileView tileView) {
        this.gameWindowController = gameWindowController;

        this.tile = tile;
        this.tileView = tileView;

        tileView.setOnMouseClicked(this::handleInitialMouseClick);
    }

    // PRIVATE

    private final GameWindowController gameWindowController;

    private final TileModel tile;
    private final TileView tileView;

    private void handleInitialMouseClick(MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY:
                handleInitialLeftMouseClick();
                break;
            case SECONDARY:
                handleRightMouseClick();
            default:
                break;
        }
    }

    private void handleInitialLeftMouseClick() {
        if (!tile.isFlagged()) {
            gameWindowController.seedBombs(this);
            gameWindowController.getTileControllers().forEach(TileController::enableControls);
            revealTile();
            gameWindowController.checkForVictory();
        }
    }

    private void handleMouseClick(MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY:
                handleLeftMouseClick();
                break;
            case SECONDARY:
                handleRightMouseClick();
            default:
                break;
        }
    }

    private void handleLeftMouseClick() {
        if (tile.isHidden() && !tile.isFlagged()) {
            revealTile();
            gameWindowController.checkForVictory();
        }
    }

    private synchronized void handleRightMouseClick() {
        if (tile.isHidden()) {
            if (tile.isFlagged())
                gameWindowController.decrementFlagCount();

            tile.incrementMark();
            transitionBasedOnTileMark()
                    .noMarkText("").noMarkColor(Color.WHITE)
                    .flagText("F").flagColor(Color.BROWN)
                    .questionText("?").questionColor(Color.YELLOW);

            if (tile.isFlagged())
                gameWindowController.incrementFlagCount();
        }
    }

    private void handleReveal() {
        tileView.transitionTextAndColor("", Color.GRAY);
        gameWindowController.getSurroundingTileControllers(this).forEach(TileController::revealTile);
    }

    private void handleReveal(int bombsAround) {
        tileView.transitionTextAndColor(bombsAround, Color.LIGHTGRAY);
    }

    private NoMarkTextStep transitionBasedOnTileMark() {
        return new MarkBasedTransition();
    }

    // HELPERS

    private interface NoMarkTextStep {
        NoMarkColorStep noMarkText(Object noMarkText);
    }

    private interface NoMarkColorStep {
        FlagTextStep noMarkColor(Color noMarkColor);
    }

    private interface FlagTextStep {
        FlagColorStep flagText(Object flagText);
    }

    private interface FlagColorStep {
        QuestionTextStep flagColor(Color flagColor);
    }

    private interface QuestionTextStep {
        QuestionColorStep questionText(Object questionText);
    }

    private interface QuestionColorStep {
        void questionColor(Color questionColor);
    }

    private class MarkBasedTransition implements NoMarkTextStep, NoMarkColorStep, FlagTextStep, FlagColorStep, QuestionTextStep, QuestionColorStep {
        @Override
        public NoMarkColorStep noMarkText(Object noMarkText) {
            this.noMarkText = noMarkText;
            return this;
        }

        @Override
        public FlagTextStep noMarkColor(Color noMarkColor) {
            this.noMarkColor = noMarkColor;
            return this;
        }

        @Override
        public FlagColorStep flagText(Object flagText) {
            this.flagText = flagText;
            return this;
        }

        @Override
        public QuestionTextStep flagColor(Color flagColor) {
            this.flagColor = flagColor;
            return this;
        }

        @Override
        public QuestionColorStep questionText(Object questionText) {
            this.questionText = questionText;
            return this;
        }

        @Override
        public void questionColor(Color questionColor) {
            transitionBasedOnTileMark(noMarkText, noMarkColor, flagText, flagColor, questionText, questionColor);
        }

        // PRIVATE

        private Object noMarkText;
        private Color noMarkColor;

        private Object flagText;
        private Color flagColor;

        private Object questionText;
    }

    private void transitionBasedOnTileMark(Object noMarkText, Color noMarkColor, Object flagText, Color flagColor, Object questionText, Color questionColor) {
        if (tile.isFlagged()) {
            tileView.transitionTextAndColor(flagText, flagColor);
            return;
        }

        if (tile.isQuestioned()) {
            tileView.transitionTextAndColor(questionText, questionColor);
            return;
        }

        tileView.transitionTextAndColor(noMarkText, noMarkColor);
    }

}
