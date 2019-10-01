package io.github.czerepko.i18n.translation;

import java.io.File;

class RecursiveFileResolver implements TemplateFileResolver {

    @Override
    public Iterable<File> resolve(String... filePaths) {
        throw new UnsupportedOperationException();
    }

}
