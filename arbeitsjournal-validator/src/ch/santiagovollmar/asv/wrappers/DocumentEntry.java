package ch.santiagovollmar.asv.wrappers;

import java.io.File;

public class DocumentEntry {
    private File file;
    private int week;
    private int year;
    private NamePair owner;

    public DocumentEntry(NamePair owner, File file, int week, int year) {
        this.file = file;
        this.week = week;
        this.year = year;
        this.owner = owner;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public NamePair getOwner() {
        return owner;
    }

    public void setOwner(NamePair owner) {
        this.owner = owner;
    }
}
