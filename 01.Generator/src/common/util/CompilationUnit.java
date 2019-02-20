/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package common.util;

import java.util.Collection;

public interface CompilationUnit<T> {

  public static final String SUBPACKAGE_PROPERTY = "subpackage";

  public static final String IMPORTS_PROPERTY = "imports";

  public static final String FILENAME_PROPERTY = "file_name";

  public static final String FILEEXTENSION_PROPERTY = "file_extension";

  public static final String HWC_PROPERTY = "hwc_file_name";

  public static final String EXTENDFILE_PROPERTY = "extend_file";

  public static final String EXTENDFILE_PROPERTY_VALUE = "EXTEND";

  public T subpackage(String packageName);

  public T imports(Collection<String> imports);

  public T additionalImport(String additionalImport);

}
