package eu.goodlike.minesweeper.mvc.tile;

import eu.goodlike.minesweeper.tool.transition.AnimationChainer;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static eu.goodlike.minesweeper.MinesweeperApplication.*;

public class TileView extends StackPane {

    public TileView setText(Object o) {
        text.setText(String.valueOf(o));
        return this;
    }

    public TileView transitionTextAndColor(Object updatedText, Color updatedColor) {
        return transitionTextAndColor(updatedText, updatedColor, DEFAULT_FADE_DURATION, DEFAULT_FADE_DURATION);
    }

    public TileView transitionTextAndColor(Object updatedText, Color updatedColor, double fadeOutDuration, double fadeInDuration) {
        Animation colorTransition = transitionTo(updatedColor, fadeOutDuration + fadeInDuration);
        Animation textTransition = transitionTo(updatedText, fadeOutDuration, fadeInDuration);

        transitionMultiple(colorTransition, textTransition).play();
        return this;
    }

    // CONSTRUCTORS

    public TileView(int size) {
        background = new Rectangle(size, size, Color.WHITE);
        background.setStroke(Color.BLACK);

        text = new Text();
        text.setFont(Font.font(DEFAULT_FONT_SIZE));
        text.setOpacity(INVISIBLE);

        setAlignment(Pos.CENTER);
        getChildren().addAll(background, text);
    }

    // PRIVATE

    private final Rectangle background;
    private final Text text;

    private Animation transitionMultiple(Animation... animations) {
        return new ParallelTransition(animations);
    }

    private Animation transitionTo(Color color, double duration) {
        FillTransition fillTransition = new FillTransition(Duration.seconds(duration), background);
        fillTransition.setToValue(color);
        return fillTransition;
    }

    private Animation transitionTo(Object updatedText, double fadeOutDuration, double fadeInDuration) {
        return AnimationChainer.chain().first(fadeOut(fadeOutDuration)).afterDo(event -> setText(updatedText))
                .next(fadeIn(fadeInDuration)).get();
    }

    private Animation fadeOut(double duration) {
        return transitionTo(INVISIBLE, duration);
    }

    private Animation fadeIn(double duration) {
        return transitionTo(VISIBLE, duration);
    }

    private Animation transitionTo(double opacity, double duration) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(duration), text);
        fadeTransition.setToValue(opacity);
        return fadeTransition;
    }


}
