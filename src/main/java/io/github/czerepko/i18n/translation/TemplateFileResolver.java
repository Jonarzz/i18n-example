package io.github.czerepko.i18n.translation;

import java.io.File;
import java.util.List;

interface TemplateFileResolver {

    List<File> resolve(String... filePaths);

}
