package de.gichinsan.ttreasurysvkg;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToDoubleConverter implements Converter<String, Double> {

    @Override
    public Double convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        // Ersetzt Komma durch Punkt, um korrekt in Double zu konvertieren
        source = source.replace(",", ".");
        try {
            return Double.parseDouble(source);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for amount: " + source, e);
        }
    }
}