package media;

import com.getting.util.executor.ExecuteResult;
import com.getting.util.executor.Executor;
import com.getting.util.ffmpeg.FfmpegUtil;
import com.getting.util.ffmpeg.VideoInfoParameters;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GifConverter extends Executor {

    private static final String CONVERTER_NAME = "ffmpeg-20160915-6f062eb-win64-static.exe";

    private final ObjectProperty<FfmpegUtil.VideoInfo> videoInfo = new SimpleObjectProperty<>();
    private final DoubleProperty executeProgress = new SimpleDoubleProperty(Double.NaN);

    public GifConverter() {
        super(GifConverter.class, CONVERTER_NAME);
    }

    public void updateVideoInfo(File file) {
        updateProgressOnUiThread(Double.NEGATIVE_INFINITY);
        ExecuteResult result = execute(new VideoInfoParameters(file), true);
        updateProgressOnUiThread(Double.NaN);
        if (result == null) {
            return;
        }
        if (result.getStatus() == ExecuteResult.Status.CANCELED) {
            return;
        }

        updateVideoInfoOnUiThread(FfmpegUtil.getVideoInfo(result.getMessages()));
    }

    public ExecuteResult convert(@NotNull GifConvertParameters parameters) {
        updateProgressOnUiThread(Double.NEGATIVE_INFINITY);

        ChangeListener<String> progressListener = (observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }

            final FfmpegUtil.Duration duration = FfmpegUtil.getConvertDuration(newValue);
            if (duration != null) {
                updateProgressOnUiThread(duration.duration / parameters.getConvertDuration());
            }
        };
        executorOutputMessage.addListener(progressListener);
        ExecuteResult convertResult = execute(parameters, false);
        executorOutputMessage.removeListener(progressListener);
        updateProgressOnUiThread(Double.NaN);

        return convertResult;
    }

    @NotNull
    public ObjectProperty<FfmpegUtil.VideoInfo> videoInfoProperty() {
        return videoInfo;
    }

    private void updateProgressOnUiThread(double progress) {
        Platform.runLater(() -> GifConverter.this.executeProgressProperty().set(progress));
    }

    private void updateVideoInfoOnUiThread(FfmpegUtil.VideoInfo videoInfo) {
        Platform.runLater(() -> GifConverter.this.videoInfo.set(videoInfo));
    }

    public double getExecuteProgress() {
        return executeProgress.get();
    }

    @NotNull
    public DoubleProperty executeProgressProperty() {
        return executeProgress;
    }

}
