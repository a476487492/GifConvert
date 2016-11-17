package media;

import com.getting.util.executor.ExecuteTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GifConvertExecuteTask extends ExecuteTask {

    public static final List<String> SUPPORT_VIDEO_FORMATS = Arrays.asList("*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv");

    private final boolean reverse;

    private final String logo;

    private final File video;

    private final double outputFrameRate;

    private final double outputScale;

    private final double convertStartTime;

    private final double convertDuration;

    public GifConvertExecuteTask(File video, double outputFrameRate, double outputScale, double convertStartTime, double convertDuration, boolean reverse, String logo) {
        this.video = video;
        this.outputFrameRate = outputFrameRate;
        this.outputScale = outputScale;
        this.convertStartTime = convertStartTime;
        this.convertDuration = convertDuration;
        this.reverse = reverse;
        this.logo = logo;
    }

    public double getConvertDuration() {
        return convertDuration;
    }

    /**
     * ffmpeg [global_options] {[input_file_options] -i input_file} ... {[output_file_options] output_file} ...
     */
    @NotNull
    @Override
    public List<String> buildParameters() {
        List<String> command = new ArrayList<>();
        command.add("-y");
        command.add("-ss");
        command.add("" + convertStartTime);
        command.add("-i");
        command.add(video.getAbsolutePath());
        command.add("-i");
        command.add(new Logo(logo).create().getAbsolutePath());

        if (!reverse) {
            command.add("-t");
            command.add("" + Math.min(30, convertDuration));
        }

        command.add("-r");
        command.add("" + outputFrameRate);

        command.add("-filter_complex");
        String filter = "scale=iw*" + outputScale + ":ih*" + outputScale;
        if (reverse) {
            filter += ",trim=end=" + Math.min(30, convertDuration) + ",reverse";
        }
        filter += ",overlay=x=W-w-10:y=H-h-10";
        command.add(filter);

        command.add(getOutputFile().getAbsolutePath());
        return command;
    }

    @Override
    public File getOutputDirectory() {
        return getOutputFile().getParentFile();
    }

    @NotNull
    public File getOutputFile() {
        return new File(video.getParent(), video.getName() + ".gif");
    }

}
