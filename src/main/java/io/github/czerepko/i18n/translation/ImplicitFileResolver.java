package io.github.czerepko.i18n.translation;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Arrays;

class ImplicitFileResolver implements TemplateFileResolver {

    @Override
    public Iterable<File> resolve(String... filePaths) {
        return Arrays.stream(filePaths)
                     .map(File::new)
                     .collect(toList());
    }

}
