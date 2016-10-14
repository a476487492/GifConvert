package binding;

import com.getting.util.TimeUtil;
import com.sun.javafx.binding.StringFormatter;
import javafx.beans.property.DoubleProperty;

public class VideoDurationStringFormatter extends StringFormatter {

    private final DoubleProperty durationProperty;

    public VideoDurationStringFormatter(DoubleProperty durationProperty) {
        super.bind(durationProperty);
        this.durationProperty = durationProperty;
    }

    @Override
    public void dispose() {
        super.unbind(durationProperty);
    }

    @Override
    protected String computeValue() {
        return TimeUtil.simpleFormatTime(durationProperty.intValue());
    }

}
