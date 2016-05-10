package binding;

import com.sun.javafx.binding.StringFormatter;
import javafx.beans.property.DoubleProperty;

public class DurationStringFormatter extends StringFormatter {

    private DoubleProperty durationProperty;

    public DurationStringFormatter(DoubleProperty durationProperty) {
        super.bind(durationProperty);
        this.durationProperty = durationProperty;
    }

    @Override
    public void dispose() {
        super.unbind(durationProperty);
    }

    @Override
    protected String computeValue() {
        return String.format("%02d:%02d", durationProperty.intValue() / 60, durationProperty.intValue() % 60);
    }

}