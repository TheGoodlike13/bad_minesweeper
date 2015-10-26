package eu.goodlike.minesweeper.mvc.tile;

import java.util.Arrays;
import java.util.List;

public class TileModel {

    public static final int BOMB = -1;

    public boolean isBomb() {
        return bombsAround == BOMB;
    }

    public boolean isSafe() {
        return !isBomb();
    }

    public boolean isHidden() {
        return !isRevealed();
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isFlagged() {
        return tileMark == TileMark.FLAG;
    }

    public boolean isQuestioned() {
        return tileMark == TileMark.QUESTION;
    }

    public void setBomb() {
        bombsAround = BOMB;
    }

    public synchronized void incrementBombCount() {
        if (bombsAround == MAX_VALUE)
            throw new IllegalStateException("Cannot increment surrounding bomb count beyond " + MAX_VALUE);

        bombsAround++;
    }

    public TileMark incrementMark() {
        tileMark = tileMark.next();
        return tileMark;
    }

    public int reveal() {
        isRevealed = true;
        return bombsAround;
    }

    @Override
    public String toString() {
        return "TileModel{" +
                "isRevealed: " + isRevealed + ", " +
                "tileMark: " + tileMark + ", " +
                "bombsAround: " + bombsAround + "}";
    }

    // CONSTRUCTORS

    public TileModel() {
        isRevealed = false;
        tileMark = TileMark.defaultMark();
        bombsAround = 0;
    }

    // PRIVATE

    private boolean isRevealed;
    private TileMark tileMark;
    private volatile int bombsAround;

    private static final int MAX_VALUE = 8;

    // INNER

    public enum TileMark {

        NONE, FLAG, QUESTION;

        public static TileMark defaultMark() {
            return NONE;
        }

        public TileMark next() {
            List<TileMark> values = Arrays.asList(TileMark.values());
            int currentIndex = values.indexOf(this);
            int nextIndex = (currentIndex + 1) % values.size();
            return values.get(nextIndex);
        }

    }

}
