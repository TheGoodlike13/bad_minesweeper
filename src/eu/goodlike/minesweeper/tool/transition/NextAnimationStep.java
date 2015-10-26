package eu.goodlike.minesweeper.tool.transition;

import javafx.animation.Animation;

public interface NextAnimationStep extends PlayStep {

    OnFinishStep next(Animation animation);

}
