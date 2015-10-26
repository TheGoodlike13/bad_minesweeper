package eu.goodlike.minesweeper.tool.transition;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface OnFinishStep extends NextAnimationStep {

    NextAnimationStep afterDo(EventHandler<ActionEvent> eventHandler);

}
