package media;

import com.getting.util.executor.ExecuteResult;
import com.getting.util.executor.Executor;
import com.getting.util.ffmpeg.FfmpegUtil;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.io.File;

public class GifConverter extends Executor {

    private static final String CONVERTER_NAME = "ffmpeg-20160213-git-588e2e3-win64-static.exe";

    private final ObjectProperty<VideoInfo> videoInfo = new SimpleObjectProperty<>();

    public GifConverter() {
        super(GifConverter.class, CONVERTER_NAME);
    }

    public void updateVideoInfo(File file) {
        updateProgressOnUiThread(Double.NEGATIVE_INFINITY);
        ExecuteResult result = execute(new VideoInfoParameters(file), true);
        updateProgressOnUiThread(Double.NaN);
        if (result.isCanceled()) {
            return;
        }
        VideoInfo videoInfo = new VideoInfo(result.getMessages());
        updateMediaInfoOnUiThread(videoInfo);
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

    public ObjectProperty<VideoInfo> videoInfoProperty() {
        return videoInfo;
    }

    private void updateProgressOnUiThread(double progress) {
        Platform.runLater(() -> GifConverter.this.executeProgressProperty().set(progress));
    }

    private void updateMediaInfoOnUiThread(VideoInfo videoInfo) {
        Platform.runLater(() -> GifConverter.this.videoInfo.set(videoInfo));
    }

}
