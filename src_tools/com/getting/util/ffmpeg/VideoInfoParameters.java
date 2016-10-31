package com.getting.util.ffmpeg;

import com.getting.util.executor.Parameters;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoInfoParameters extends Parameters {

    private final File video;

    public VideoInfoParameters(@NotNull File video) {
        this.video = video;
    }

    @NotNull
    @Override
    public List<String> build() {
        List<String> command = new ArrayList<>();
        command.add("-i");
        command.add(video.getAbsolutePath());
        return command;
    }

    @Nullable
    @Override
    public File getOutputDirectory() {
        return null;
    }

}
