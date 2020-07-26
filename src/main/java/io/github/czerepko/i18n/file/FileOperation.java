package io.github.czerepko.i18n.file;

enum FileOperation {

    READ,
    WRITE,
    CREATE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
