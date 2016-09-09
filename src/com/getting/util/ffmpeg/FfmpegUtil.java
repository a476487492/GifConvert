package com.getting.util.ffmpeg;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FfmpegUtil {

    private static final Pattern DURATION_PATTERN = Pattern.compile("Duration: (?<duration>(?<hour>\\d{2}):(?<minute>\\d{2}):(?<second>\\d{2})\\.(\\d+))", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIDEO_SIZE_PATTERN = Pattern.compile("(?<width>\\d{2,4})x(?<height>\\d{2,4})", Pattern.CASE_INSENSITIVE);

    private static final Pattern VIDEO_FRAME_RATE_PATTERN = Pattern.compile("(?<frame>[0-9.]+) fps", Pattern.CASE_INSENSITIVE);

    private static final Pattern CONVERT_PROGRESS_PATTERN = Pattern.compile("time=(?<hour>\\d{2}):(?<minute>\\d{2}):(?<second>\\d{2})\\.(?<millsecond>\\d{2})", Pattern.CASE_INSENSITIVE);

    /**
     * @param messages Output of "ffmpeg -i file"
     */
    @Nullable
    public static Point parseVideoSize(@NotNull List<String> messages) {
        for (String message : messages) {
            for (String token : message.split(",")) {
                Matcher videoSizeMatcher = VIDEO_SIZE_PATTERN.matcher(token);
                if (videoSizeMatcher.find()) {
                    return new Point(Integer.parseInt(videoSizeMatcher.group("width")), Integer.parseInt(videoSizeMatcher.group("height")));
                }
            }
        }

        return null;
    }

    /**
     * @param messages Output of "ffmpeg -i file"
     */
    @Nullable
    public static double parseFrameRate(@NotNull List<String> messages) {
        for (String message : messages) {
            for (String token : message.split(",")) {
                Matcher frameRateMatcher = VIDEO_FRAME_RATE_PATTERN.matcher(token);
                if (frameRateMatcher.find()) {
                    return Double.parseDouble(frameRateMatcher.group("frame"));
                }
            }
        }

        return -1;
    }

    /**
     * @param messages Output of "ffmpeg -i file"
     */
    @Nullable
    public static double parseDuration(@NotNull List<String> messages) {
        for (String message : messages) {
            for (String token : message.split(",")) {
                Matcher videoDurationMatcher = DURATION_PATTERN.matcher(token);
                if (videoDurationMatcher.find()) {
                    return Integer.parseInt(videoDurationMatcher.group("hour")) * 60 * 60 + Integer.parseInt(videoDurationMatcher.group("minute")) * 60 + Integer.parseInt(videoDurationMatcher.group("second"));
                }
            }
        }

        return -1;
    }

    /**
     * @param messages Output of "ffmpeg -i file"
     */
    @Nullable
    public static String parseDurationDescription(@NotNull List<String> messages) {
        for (String message : messages) {
            for (String token : message.split(",")) {
                Matcher videoDurationMatcher = DURATION_PATTERN.matcher(token);
                if (videoDurationMatcher.find()) {
                    return videoDurationMatcher.group("duration");
                }
            }
        }

        return null;
    }

    /**
     * @param messages Output of "ffmpeg -i input ouput"
     */
    @Nullable
    public static double getConvertDuration(@NotNull String messages) {
        for (String split : messages.split(" ")) {
            Matcher matcher = CONVERT_PROGRESS_PATTERN.matcher(split);
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group("hour")) * 60 * 60 + Integer.parseInt(matcher.group("minute")) * 60 + Integer.parseInt(matcher.group("second"));
            }
        }

        return -1;
    }

}
