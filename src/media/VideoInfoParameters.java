package media;

import com.getting.util.executor.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoInfoParameters implements Parameters {

    private final File video;

    public VideoInfoParameters(File video) {
        this.video = video;
    }

    @Override
    public List<String> build() {
        List<String> command = new ArrayList<>();
        command.add("-i");
        command.add(video.getAbsolutePath());
        return command;
    }

    @Override
    public File getOutputDirectory() {
        return null;
    }

}
