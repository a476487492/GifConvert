package com.getting.util.ffmpeg;

import java.awt.*;
import java.util.List;

public class VideoInfo {

    private final Point videoSize;
    private final double frameRate;
    private final String durationDescription;
    private final double duration;

    /**
     * @param messages Output of "ffmpeg -i file"
     */
    public VideoInfo(List<String> messages) {
        videoSize = FfmpegUtil.parseVideoSize(messages);
        frameRate = FfmpegUtil.parseFrameRate(messages);
        duration = FfmpegUtil.parseDuration(messages);
        durationDescription = FfmpegUtil.parseDurationDescription(messages);
    }

    public String getDurationDescription() {
        return durationDescription;
    }

    /**
     * @return second
     */
    public double getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        if (videoSize == null || frameRate <= 0 || durationDescription == null) {
            return "";
        }

        return "" + videoSize.x + "x" + videoSize.y + ", " + frameRate + "fps, " + durationDescription;
    }

}
