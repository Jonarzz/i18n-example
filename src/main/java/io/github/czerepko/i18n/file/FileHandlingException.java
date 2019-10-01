package io.github.czerepko.i18n.file;

import java.io.IOException;

class FileHandlingException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "An error occurred while performing a %s operation on a file";

    FileHandlingException(IOException ioException, FileOperation fileOperation) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, fileOperation), ioException);
    }

}
