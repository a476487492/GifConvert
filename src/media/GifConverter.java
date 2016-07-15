package media;

import com.sun.istack.internal.NotNull;
import com.getting.util.executor.ExecuteResult;
import com.getting.util.executor.Executor;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GifConverter extends Executor {

    private static final String CONVERTER_NAME = "ffmpeg-20160213-git-588e2e3-win64-static.exe";

    private static final Pattern CONVERT_PROGRESS_PATTERN = Pattern.compile("time=(?<hour>\\d{2}):(?<minute>\\d{2}):(?<second>\\d{2})\\.(?<millsecond>\\d{2})", Pattern.CASE_INSENSITIVE);

    private DoubleProperty convertProgress = new SimpleDoubleProperty(Double.NaN);

    private ObjectProperty<VideoInfo> videoInfo = new SimpleObjectProperty<>();

    public GifConverter() {
        super(GifConverter.class, CONVERTER_NAME);
    }

    public void updateVideo(File file) {
        updateProgressOnUIiThread(Double.NEGATIVE_INFINITY);
        ExecuteResult result = execute(new VideoInfoParameters(file), true);
        updateProgressOnUIiThread(Double.NaN);
        if (result.isCanceled()) {
            return;
        }
        VideoInfo videoInfo = new VideoInfo(result.getMessages());
        updateMediaInfoOnUiThread(videoInfo);
    }

    public GifConvertResult convert(@NotNull GifConvertParameters parameters) {
        updateProgressOnUIiThread(Double.NEGATIVE_INFINITY);

        ChangeListener<String> progressListener = new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null) {
                    return;
                }

                for (String split : newValue.split(" ")) {
                    Matcher matcher = CONVERT_PROGRESS_PATTERN.matcher(split);
                    if (matcher.matches()) {
                        final double duration = Integer.parseInt(matcher.group("hour")) * 60 * 60 + Integer.parseInt(matcher.group("minute")) * 60 + Integer.parseInt(matcher.group("second"));
                        updateProgressOnUIiThread(duration / parameters.getConvertDuration());
                    }
                }
            }

        };
        executorOutputMessage.addListener(progressListener);
        ExecuteResult convertResult = execute(parameters, false);
        executorOutputMessage.removeListener(progressListener);
        updateProgressOnUIiThread(Double.NaN);

        return new GifConvertResult(parameters.getOutputFile(), convertResult.isSuccess(), convertResult.isCanceled(), convertResult.getCostTime());
    }

    public DoubleProperty convertProgressProperty() {
        return convertProgress;
    }

    public ObjectProperty<VideoInfo> videoInfoProperty() {
        return videoInfo;
    }

    private void updateProgressOnUIiThread(double progress) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                GifConverter.this.convertProgress.set(progress);
            }

        });
    }

    private void updateMediaInfoOnUiThread(VideoInfo videoInfo) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                GifConverter.this.videoInfo.set(videoInfo);
            }

        });
    }

}
