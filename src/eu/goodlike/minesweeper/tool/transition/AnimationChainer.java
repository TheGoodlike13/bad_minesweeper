package eu.goodlike.minesweeper.tool.transition;

import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.Optional;

public class AnimationChainer implements FirstAnimationStep, OnFinishStep {

    @Override
    public OnFinishStep first(Animation animation) {
        firstAnimation = animation;
        lastAnimation = animation;
        return this;
    }

    @Override
    public NextAnimationStep afterDo(EventHandler<ActionEvent> eventHandler) {
        lastEventHandler = Optional.of(eventHandler);
        return this;
    }

    @Override
    public OnFinishStep next(final Animation animation) {
        if (lastEventHandler.isPresent()) {
            final EventHandler<ActionEvent> eventHandler = lastEventHandler.get();
            lastAnimation.setOnFinished(event -> {
                eventHandler.handle(event);
                animation.play();
            });
            lastEventHandler = Optional.empty();
        } else
            lastAnimation.setOnFinished(event -> animation.play());

        lastAnimation = animation;
        return this;
    }

    @Override
    public void play() {
        firstAnimation.play();
    }

    @Override
    public Animation get() {
        return firstAnimation;
    }

    // CONSTRUCTORS

    public static FirstAnimationStep chain() {
        return new AnimationChainer();
    }

    private AnimationChainer() {
        lastEventHandler = Optional.empty();
    }

    // PRIVATE

    private Animation firstAnimation;
    private Animation lastAnimation;
    private Optional<EventHandler<ActionEvent>> lastEventHandler;


}
