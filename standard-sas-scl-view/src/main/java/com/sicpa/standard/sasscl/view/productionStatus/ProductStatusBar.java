package com.sicpa.standard.sasscl.view.productionStatus;

import com.sicpa.standard.gui.plaf.SicpaColor;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.callback.TimelineCallbackAdapter;

import javax.swing.*;

import java.awt.*;

@SuppressWarnings("serial")
public class ProductStatusBar extends JPanel {

    private float progress = 0;

    private int PROGRESS_BAR_MINIMUM_VALUE = 0;

    private int PROGRESS_BAR_MAXIMUM_VALUE = 100;

    private Timeline timeline;


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBackground(g);
        paintBar(g);
    }

    private void paintBar(Graphics g) {
        int w = (int) (getWidth() * progress / PROGRESS_BAR_MAXIMUM_VALUE);
        g.setColor(SicpaColor.BLUE_MEDIUM);
        g.fillRect(0, 0, w, getHeight());
    }

    private void paintBackground(Graphics g) {
        g.setColor(SicpaColor.BLUE_DARK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    public void startProgressBarAnimation() {
        if (!isProgressBarStarted()) {
            getTimeLine().play();
        }
    }

    private boolean isProgressBarStarted() {
        return progress != 0;
    }

    private Timeline getTimeLine() {
        if (timeline == null) {
            initTimeLine();
        }
        return timeline;
    }

    private void initTimeLine() {
        timeline = new Timeline();
        timeline.setDuration(400);
        timeline.addCallback(new TimelineCallbackAdapter() {

            @Override
            public void onTimelinePulse(float durationFraction, float timelinePosition) {
                setProgress((int) (timelinePosition * PROGRESS_BAR_MAXIMUM_VALUE));
            }

            public void onTimelineStateChanged(Timeline.TimelineState oldState, Timeline.TimelineState newState, float durationFraction, float timelinePosition) {
                if (isTimeLineFinish(timelinePosition)) {
                    setProgress(PROGRESS_BAR_MINIMUM_VALUE);
                }
            }

        });
    }

    private boolean isTimeLineFinish(float timelinePosition) {
        return timelinePosition == 1;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        repaint();
    }
}
