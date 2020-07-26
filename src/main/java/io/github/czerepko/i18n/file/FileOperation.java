package io.github.czerepko.i18n.file;

enum FileOperation {

    READ,
    READ_LINES,
    WRITE,
    CREATE;

    @Override
    public String toString() {
        return name().toLowerCase()
                     .replace('_', ' ');
    }

}
