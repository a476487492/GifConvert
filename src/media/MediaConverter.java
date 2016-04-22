package media;

import com.sun.istack.internal.NotNull;
import executor.ExecuteResult;
import executor.Executor;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaConverter extends Executor {

    private static final String CONVERTER_NAME = "ffmpeg-20160213-git-588e2e3-win64-static.exe";

    private static final Pattern PROGRESS_PATTERN = Pattern.compile("time=(?<hour>\\d{2}):(?<minute>\\d{2}):(?<second>\\d{2})\\.(?<millsecond>\\d{2})", Pattern.CASE_INSENSITIVE);

    private DoubleProperty progress = new SimpleDoubleProperty(Double.NaN);

    private ObjectProperty<MediaInfo> mediaInfoProperty = new SimpleObjectProperty<>(MediaInfo.INVALID);

    public MediaConverter() {
        super(MediaConverter.class, CONVERTER_NAME);
    }

    public void updateMediaInfo(File file) {
        updateProgressOnUIiThread(Double.NEGATIVE_INFINITY);
        ExecuteResult mediaInfoExecuteResult = execute(new MediaInfoParameters(file), true);
        updateProgressOnUIiThread(Double.NaN);
        if (mediaInfoExecuteResult.isCanceled()) {
            return;
        }
        MediaInfo mediaInfo = new MediaInfo(mediaInfoExecuteResult.getMessages());
        updateMediaInfoOnUiThread(mediaInfo);
    }

    public MediaConvertResult convert(@NotNull GifConvertParameters convertInfo) {
        updateProgressOnUIiThread(Double.NEGATIVE_INFINITY);

        ChangeListener<String> progressListener = new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null) {
                    return;
                }

                for (String split : newValue.split(" ")) {
                    Matcher matcher = PROGRESS_PATTERN.matcher(split);
                    if (matcher.matches()) {
                        final double duration = Integer.parseInt(matcher.group("hour")) * 60 * 60 + Integer.parseInt(matcher.group("minute")) * 60 + Integer.parseInt(matcher.group("second"));
                        updateProgressOnUIiThread(duration / convertInfo.getConvertDuration());
                    }
                }

            }

        };
        statusProperty().addListener(progressListener);
        ExecuteResult convertResult = execute(convertInfo, false);
        statusProperty().removeListener(progressListener);
        updateProgressOnUIiThread(Double.NaN);

        return new MediaConvertResult(convertInfo.getOutputFile(), convertResult.isSuccess(), convertResult.isCanceled(), convertResult.getCostTime());
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public ObjectProperty<MediaInfo> mediaInfoPropertyProperty() {
        return mediaInfoProperty;
    }

    private void updateProgressOnUIiThread(double progress) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                MediaConverter.this.progress.set(progress);
            }

        });
    }

    private void updateMediaInfoOnUiThread(MediaInfo mediaInfo) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                mediaInfoProperty.set(mediaInfo);
            }

        });
    }

}
