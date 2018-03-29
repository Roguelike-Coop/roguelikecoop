package io.github.roguelikecoop.roguelikecoop;

public class DataFileException extends RuntimeException {
    private String fileName;
    private int lineNum;
    private int columnNum;
    private String detail;
    private Throwable cause;

    DataFileException (String fileName,
                       String detail) {

        this.fileName = fileName;
        this.detail = detail;
    }

    DataFileException (String fileName,
                       Throwable cause) {

        this.fileName = fileName;
        this.detail = cause.toString();
        this.cause = cause;
    }

    DataFileException (String fileName,
                       int lineNum,
                       String detail) {

        this.fileName = fileName;
        this.lineNum = lineNum;
        this.detail = detail;
    }

    DataFileException (String fileName,
                       int lineNum,
                       Throwable cause) {

        this.fileName = fileName;
        this.lineNum = lineNum;
        this.detail = cause.toString();
        this.cause = cause;
    }

    DataFileException (String fileName,
                       int lineNum,
                       int columnNum,
                       String detail) {

        this.fileName = fileName;
        this.lineNum = lineNum;
        this.columnNum = columnNum;
        this.detail = detail;
    }

    DataFileException (String fileName,
                       int lineNum,
                       int columnNum,
                       Throwable cause) {

        this.fileName = fileName;
        this.lineNum = lineNum;
        this.columnNum = columnNum;
        this.detail = cause.toString();
        this.cause = cause;
    }

    @Override
    public Throwable getCause () {
        return cause;
    }

    @Override
    public String toString () {
        StringBuilder b = new StringBuilder();

        if (fileName != null) {
            b.append("In `");
            b.append(fileName);
            b.append('\'');
        }

        if (lineNum != 0) {
            if (b.length() == 0) {
                b.append("At line ");
            } else {
                b.append(" at line ");
            }

            b.append(lineNum);

            if (columnNum != 0) {
                b.append(", column ");
                b.append(columnNum);
            }
        }

        if (b.length() != 0) {
            b.append(": ");
        }

        if (detail == null) {
            b.append("<null>");
        } else {
            b.append(detail);
        }

        return b.toString();
    }
}
