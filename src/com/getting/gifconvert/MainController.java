package com.getting.gifconvert;

import binding.VideoDurationLabelFormatter;
import binding.VideoDurationStringFormatter;
import com.getting.util.*;
import com.getting.util.annotation.UiThread;
import com.getting.util.binding.NullableObjectStringFormatter;
import com.getting.util.executor.ExecuteResult;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import media.GifConvertExecuteTask;
import media.GifConverter;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.PlusMinusSlider;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.StatusBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private static final Object MSG_CONVERT_VIDEO = new Object();

    private final GifConverter gifConverter = new GifConverter();
    private final Looper convertLoop = new Looper();
    private final Looper uiLoop = new Looper();

    private final Image loadingImage = new Image(MainController.class.getResource("loading.gif").toExternalForm(), true);
    private final PathRecord lastVisitPathRecord = new PathRecord(MainController.class, "last visit directory");
    @FXML
    private ImageView gifPreviewView;
    @FXML
    private Slider gifFrameRateView;
    @FXML
    private Slider gifScaleView;
    @FXML
    private RangeSlider inputVideoDurationView;
    @FXML
    private Label inputVideoStartTimeView;
    @FXML
    private Label inputVideoEndTimeView;
    @FXML
    private Pane inputVideoDurationPane;
    @FXML
    private CheckMenuItem reverseGifView;
    @FXML
    private CheckMenuItem addLogoView;
    @FXML
    private Label videoInfoView;
    @FXML
    private NotificationPane notificationPane;
    @FXML
    private StatusBar statusBar;
    @NotNull
    private ObjectProperty<File> inputVideo = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showLoadingImage();

        statusBar.progressProperty().bind(gifConverter.executeProgressProperty());
        videoInfoView.textProperty().bind(new NullableObjectStringFormatter<>(gifConverter.videoInfoProperty()));
        inputVideoStartTimeView.textProperty().bind(new VideoDurationStringFormatter(inputVideoDurationView.lowValueProperty()));
        inputVideoEndTimeView.textProperty().bind(new VideoDurationStringFormatter(inputVideoDurationView.highValueProperty()));
        inputVideoDurationView.setLabelFormatter(new VideoDurationLabelFormatter());

        {
            final ChangeListener<Number> convertParameterChangeListener = (observable, oldValue, newValue) -> reloadGifConvert(1000);

            inputVideoDurationView.lowValueProperty().addListener(convertParameterChangeListener);
            inputVideoDurationView.highValueProperty().addListener(convertParameterChangeListener);
            gifScaleView.valueProperty().addListener(convertParameterChangeListener);
            gifFrameRateView.valueProperty().addListener(convertParameterChangeListener);
        }

        {
            final ChangeListener<Boolean> convertParameterChangeListener = (observable, oldValue, newValue) -> reloadGifConvert(0);

            reverseGifView.selectedProperty().addListener(convertParameterChangeListener);
            addLogoView.selectedProperty().addListener(convertParameterChangeListener);
        }

        inputVideo.addListener((observable, oldValue, newValue) -> convertLoop.postTask(new ReloadVideoInfoTask()));

        gifConverter.videoInfoProperty().addListener((observable, oldValue, newValue) -> {
            final double duration = newValue.getDuration();
            inputVideoDurationView.setMin(0);
            inputVideoDurationView.setMax(duration);
            inputVideoDurationView.setMajorTickUnit(duration / 10);
            inputVideoDurationPane.setVisible(true);
        });

        gifPreviewView.setOnDragOver(event -> event.acceptTransferModes(TransferMode.LINK));
        gifPreviewView.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            if (!files.isEmpty()) {
                inputVideo.set(files.get(0));
            }
        });
    }

    @FXML
    private void onChooseVideo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("视频文件", GifConvertExecuteTask.SUPPORT_VIDEO_FORMATS));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("所有文件", "*.*"));
        if (lastVisitPathRecord.getPath().isDirectory()) {
            fileChooser.setInitialDirectory(lastVisitPathRecord.getPath());
        }

        File chooseFile = fileChooser.showOpenDialog(gifPreviewView.getScene().getWindow());
        if (chooseFile != null) {
            inputVideo.set(chooseFile);
            lastVisitPathRecord.set(chooseFile.getParentFile());
        }

        gifPreviewView.getScene().getWindow().setOnCloseRequest(event -> {
            convertLoop.removeAllTasks();
            uiLoop.removeAllTasks();
        });
    }

    @FXML
    private void onOpenSaveDirectory() {
        if (inputVideo.get() == null) {
            return;
        }

        if (!inputVideo.get().exists()) {
            return;
        }

        try {
            java.awt.Desktop.getDesktop().open(inputVideo.get().getParentFile());
        } catch (IOException e) {
            LOGGER.error("onOpenSaveDirectory", e);
        }
    }

    @UiThread
    private void reloadGifConvert(long delay) {
        convertLoop.removeTask(MSG_CONVERT_VIDEO);

        notificationPane.hide();

        if (inputVideo.get() == null) {
            return;
        }

        if (!inputVideo.get().exists() || !inputVideo.get().isFile()) {
            notificationPane.show("所选择的文件已被删除，请重新选择文件");
            return;
        }

        if (inputVideoDurationView.getHighValue() - inputVideoDurationView.getLowValue() > 30) {
            notificationPane.show("转换时间长度过长");
            return;
        }

        if (inputVideoDurationView.getHighValue() - inputVideoDurationView.getLowValue() < 1) {
            return;
        }

        convertLoop.postTask(new GifConvertTask(delay));
    }

    @UiThread
    private void showLoadingImage() {
        gifPreviewView.setImage(loadingImage);
    }

    @UiThread
    private void showNotificationForAWhile(String message) {
        notificationPane.show(message);

        uiLoop.postTask(new HideNotificationTask());
    }

    @FXML
    public void onLowAdjust(@NotNull PlusMinusSlider.PlusMinusEvent plusMinusEvent) {
        inputVideoDurationView.setLowValue(inputVideoDurationView.getLowValue() + Math.copySign(0.1, plusMinusEvent.getValue()));
    }

    @FXML
    public void onHighAdjust(@NotNull PlusMinusSlider.PlusMinusEvent plusMinusEvent) {
        inputVideoDurationView.setHighValue(inputVideoDurationView.getHighValue() + Math.copySign(0.1, plusMinusEvent.getValue()));
    }

    private class HideNotificationTask extends AsyncTask<Void> {

        public HideNotificationTask() {
            super(null, 3000);
        }

        @Override
        public void preTaskOnUi() {
            notificationPane.hide();
        }

        @Nullable
        @Override
        public Void runTask() {
            return null;
        }

    }

    private class ReloadVideoInfoTask extends AsyncTask<Void> {

        public ReloadVideoInfoTask() {
            super(null, 0);
        }

        @Nullable
        @Override
        public Void runTask() {
            gifConverter.updateVideoInfo(inputVideo.get());
            return null;
        }

        @Override
        public void cancel() {
            gifConverter.cancel();
        }

    }

    private class GifConvertTask extends AsyncTask<ExecuteResult> {

        @NotNull
        private final GifConvertExecuteTask parameters;

        public GifConvertTask(long delay) {
            super(MSG_CONVERT_VIDEO, delay);
            String logo = addLogoView.isSelected() ? new SimpleDateFormat().format(new Date()) : " ";
            parameters = new GifConvertExecuteTask(inputVideo.get(),
                    gifFrameRateView.getValue(),
                    gifScaleView.getValue(),
                    inputVideoDurationView.getLowValue(),
                    inputVideoDurationView.getHighValue() - inputVideoDurationView.getLowValue(),
                    reverseGifView.isSelected(),
                    logo);
        }

        @Override
        public void preTaskOnUi() {
            showLoadingImage();
        }

        @Override
        public ExecuteResult runTask() {
            return gifConverter.convert(parameters);
        }

        @Override
        public void postTaskOnUi(@Nullable ExecuteResult result) {
            if (result == null) {
                return;
            }

            try {
                gifPreviewView.setImage(new Image(parameters.getOutputFile().toURI().toURL().toExternalForm(), true));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (result.getStatus() == ExecuteResult.Status.CANCELED) {
                return;
            }

            showNotificationForAWhile(result.getStatus() == ExecuteResult.Status.SUCCESS ? "转换时间：" + TimeUtil.formatTime(result.getCostTime()) + "，转换后大小：" + MyFileUtil.formatFileSize(parameters.getOutputFile()) : "转换失败！！是否选择了有效的视频文件？");
        }

        @Override
        public void cancel() {
            gifConverter.cancel();
        }

    }

}
