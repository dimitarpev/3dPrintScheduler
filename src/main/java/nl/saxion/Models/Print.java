package nl.saxion.Models;

import java.util.ArrayList;
import java.util.List;

public class Print {
    private String name;
    private int height;
    private int width;
    private int length;
    private List<Double> filamentLength;
    private int printTime;

    public Print(String name, int height, int width, int length, List<Double> filamentLength, int printTime) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
        this.filamentLength = filamentLength;
        this.printTime = printTime;
    }

    @Override
    public String toString() {
        return "--------" + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Height: " + height + System.lineSeparator() +
                "- Width: " + width + System.lineSeparator() +
                "- Length: " + length + System.lineSeparator() +
                "- FilamentLength: " + filamentLength + System.lineSeparator() +
                "- Print Time: " + printTime + System.lineSeparator() +
                "--------";
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getPrintTime() {
        return printTime;
    }

    public List<Double> getFilamentLength() {
        return filamentLength;
    }
}
