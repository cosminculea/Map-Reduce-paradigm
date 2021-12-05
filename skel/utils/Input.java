package utils;

public class Input {
    private final String fileName;
    private final long offset;
    private final int dim;

    public Input(String fileName, long offset, int dim) {
        this.fileName = fileName;
        this.offset = offset;
        this.dim = dim;
    }

    public String getFileName() {
        return fileName;
    }

    public long getOffset() {
        return offset;
    }

    public int getDim() {
        return dim;
    }
}
