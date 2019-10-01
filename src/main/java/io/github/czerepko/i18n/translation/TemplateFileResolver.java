package io.github.czerepko.i18n.translation;

import java.io.File;

interface TemplateFileResolver {

    Iterable<File> resolve(String... filePaths);

}
