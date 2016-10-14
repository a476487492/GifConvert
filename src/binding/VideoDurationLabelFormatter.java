package binding;

import com.getting.util.TimeUtil;
import javafx.util.StringConverter;

public class VideoDurationLabelFormatter extends StringConverter<Number> {

    @Override
    public String toString(Number object) {
        return TimeUtil.simpleFormatTime(object.intValue());
    }

    @Override
    public Number fromString(String string) {
        return Double.parseDouble(string);
    }

}
