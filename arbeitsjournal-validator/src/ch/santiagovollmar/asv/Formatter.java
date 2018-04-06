package ch.santiagovollmar.asv;

import ch.santiagovollmar.asv.wrappers.NamePair;

import java.util.*;
import java.util.stream.Collectors;

interface FormatPart {
    String getValue(final HashMap<String, Object> values);
}

class StaticFormatPart implements FormatPart {
    private String content;

    public StaticFormatPart(String content) {
        this.content = content;
    }

    @Override
    public String getValue(final HashMap<String, Object> values) {
        return content;
    }
}

class DynamicFormatPart implements FormatPart {
    private String key;
    private int size;

    public DynamicFormatPart(String key, int size) {
        this.key = key;
        this.size = size;
    }

    @Override
    public String getValue(HashMap<String, Object> values) {
        switch (key) {
            case "ca":
            case "ye":
                Integer value = (Integer) values.get(key);
                if (value != null) {
                    if (size < 0) {
                        return String.format("%0" + size + "d", value);
                    } else {
                        return String.format("%d", value);
                    }
                } else {
                    if (size < 0) {
                        return "[0-9]{" + size + "}";
                    } else {
                        return "[0-9]+";
                    }
                }

                default:
                    String svalue = (String) values.get(key);
                    if (svalue != null) {
                        return svalue;
                    } else {
                        return "[a-zA-zäëïöüâêîôûàèìòùáéíóú]+";
                    }
        }
    }
}

public class Formatter {
    private static final HashSet<String> formatSpecifiers = new HashSet<>();
    static {
        formatSpecifiers.add("fn");
        formatSpecifiers.add("sn");
        formatSpecifiers.add("fc");
        formatSpecifiers.add("sc");
        formatSpecifiers.add("ca");
        formatSpecifiers.add("ye");
    }

    private ArrayList<FormatPart> formatParts = new ArrayList<>();

    public Formatter(String format) throws IllegalArgumentException {
        StringBuilder builder = new StringBuilder();

        // analyze format
        char[] formatArray = format.toCharArray();
        try {
            for (int i = 0; i < format.length(); i++) {
                char c = formatArray[i];

                if (c == '%') { // format got escaped
                    // add previous format part to parts
                    formatParts.add(new StaticFormatPart(builder.toString()));
                    builder = new StringBuilder();

                    // collect size specifier
                    int j = 1;
                    int size = 0;
                    while (Character.isDigit(formatArray[i + j])) {
                        size += (int) Integer.parseInt(String.valueOf(formatArray[i + j])) * Math.pow(10, j - 1);
                        j++;
                    }

                    if (j == 1) { // no size was specified
                        size = -1;
                    }

                    // collect format specifier
                    String formatSpecifier = String.valueOf(formatArray[i + j]) + formatArray[i + j + 1];

                    if (!formatSpecifiers.contains(formatSpecifier)) {
                        throw new IllegalArgumentException("Illegal format specifier: " + formatSpecifier);
                    }

                    // add format part
                    formatParts.add(new DynamicFormatPart(formatSpecifier, size));

                    i += j + 1;
                } else {
                    builder.append(c);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Unfinished format specifier");
        }

        formatParts.add(new StaticFormatPart(builder.toString()));
    }

    public String format(NamePair namePair, Integer week, Integer year) {
        StringBuilder builder = new StringBuilder();

        HashMap<String, Object> values = new HashMap<>();
        values.put("fn", namePair != null ? namePair.prename: null);
        values.put("sn", namePair != null ? namePair.surname: null);
        values.put("fc", namePair != null ? namePair.prenameWithoutUmlauts: null);
        values.put("sc", namePair != null ? namePair.surnameWithoutUmlauts: null);
        values.put("ca", week);
        values.put("ye", year);

        for (FormatPart formatPart : formatParts) {
            builder.append(formatPart.getValue(values));
        }

        return builder.toString();
    }

    public int getWeek(String formatted) {
        // locate week part
        List<DynamicFormatPart> dynamicFormatParts = formatParts.stream()
                .filter(part -> part instanceof DynamicFormatPart)
                .map(part -> ((DynamicFormatPart) part))
                .collect(Collectors.toList());

        // TODO continue here

        return 0;
    }

    public int getYear(String formatted) {
        //TODO write this function
        return 0;
    }
}