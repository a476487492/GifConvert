package com.getting.util.ffmpeg;

import com.getting.util.executor.ParametersImp;
import com.sun.istack.internal.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoInfoParameters extends ParametersImp {

    private final File video;

    public VideoInfoParameters(@NotNull File video) {
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
