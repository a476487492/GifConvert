package binding;

import com.getting.util.TimeUtil;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;

public class VideoDurationLabelFormatter extends StringConverter<Number> {

    @Override
    public String toString(@NotNull Number object) {
        return TimeUtil.simpleFormatTime(object.intValue());
    }

    @Override
    public Number fromString(@NotNull String string) {
        return Double.parseDouble(string);
    }

}
