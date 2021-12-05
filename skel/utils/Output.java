package utils;

public class Output {
    private final double rang;
    private final int maxLength;
    private final int numberAppearances;
    private final String fileName;

    public Output(double rang, int maxLength, int numberAppearances,  String fileName) {
        this.rang = rang;
        this.maxLength = maxLength;
        this.numberAppearances = numberAppearances;
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        String[] tokens = fileName.split("[/]");
        String file = tokens[tokens.length - 1];
        return file + ","
                + String.format("%.2f", rang) + ","
                + maxLength + ","
                + numberAppearances;
    }

    public double getRang() {
        return rang;
    }
}
