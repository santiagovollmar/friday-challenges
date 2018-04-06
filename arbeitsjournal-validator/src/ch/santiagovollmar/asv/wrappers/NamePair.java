package ch.santiagovollmar.asv.wrappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.TreeSet;

public class NamePair {
    public final String prename;
    public final String surname;

    public final String prenameWithoutUmlauts;
    public final String surnameWithoutUmlauts;

    public NamePair(String prename, String surname) {
        this.prename = prename.toLowerCase();
        this.surname = surname.toLowerCase();

        // remove umlauts
        prenameWithoutUmlauts = removeUmlaute(this.prename);
        surnameWithoutUmlauts = removeUmlaute(this.surname);
    }

    private String removeUmlaute(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            builder.append(isUmlaut(c) ? getUmlautMapping(c): c);
        }
        return builder.toString();
    }

    private String getUmlautMapping(char c) {
        //@formatter:off
        switch (c) {
            case 'ä':
                return "ae";
            case 'ë':
                return "e";
            case 'ï':
                return "i";
            case 'ö':
                return "oe";
            case 'ü':
                return "ue";
            case 'â':
                return "a";
            case 'ê':
                return "e";
            case 'î':
                return "i";
            case 'ô':
                return "o";
            case 'û':
                return "u";
            case 'à':
                return "a";
            case 'è':
                return "e";
            case 'ì':
                return "i";
            case 'ò':
                return "o";
            case 'ù':
                return "u";
            case 'á':
                return "a";
            case 'é':
                return "e";
            case 'í':
                return "i";
            case 'ó':
                return "o";
            case 'ú':
                return "u";
            case 'ã':
                return "a";
            case 'õ':
                return "o";
            case 'ñ':
                return "n";
        }

        return Character.toString(c);
        //@formatter:on
    }

    private boolean isUmlaut(char c) {
        return "äëïöüâêîôûàèìòùáéíóúãõñ".indexOf(Character.toLowerCase(c)) != -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamePair namePair = (NamePair) o;

        if (!prename.equals(namePair.prename)) return false;
        return surname.equals(namePair.surname);
    }

    @Override
    public int hashCode() {
        int result = prename.hashCode();
        result = 31 * result + surname.hashCode();
        return result;
    }
}
