package media;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Logo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Logo.class);

    private final String logo;

    public Logo(String logo) {
        this.logo = logo;
    }

    @NotNull
    public File create() {
        File logoFile = new File(System.getProperty("java.io.tmpdir"), "ffmpeg-logo.png");
        Font font = new Font("微软雅黑", Font.PLAIN, 15);
        FontMetrics fontMetrics = new Canvas().getFontMetrics(font);
        BufferedImage bufferedImage = new BufferedImage(fontMetrics.stringWidth(logo), fontMetrics.getAscent() + fontMetrics.getDescent(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        graphics.drawString(logo, 0, fontMetrics.getAscent());
        try {
            ImageIO.write(bufferedImage, "png", logoFile);
        } catch (IOException e) {
            LOGGER.error("create", e);
        }
        return logoFile;
    }

}
