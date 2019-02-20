/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package generation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import configure.DexScript;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.cli.CLIArguments;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.ConfigurationPropertiesMapContributor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import dex.junit.categories.StandardTests;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Category(StandardTests.class)
public abstract class GeneratorTestBase {

  private static final String OUTPUT_FOLDER = "testoutput";

  static final String LOG = "DexGeneratorTest";

  /**
   * Base generator arguments
   */
  private List<String> generatorArguments = Lists.newArrayList(
      getConfigProperty("modelPath"), "src/test/resources",
      getConfigProperty("out"), OUTPUT_FOLDER,
      getConfigProperty("targetPath"), "test/source",
      getConfigProperty("templatePath"), "test/source"
  );

  @BeforeClass
  public static void initLog() {
    Slf4jLog.init();
    Log.enableFailQuick(false);
  }

  protected Optional<Configuration> getConfiguration(Multimap<String, String> properties) {

    ConfigurationPropertiesMapContributor propertiesContributor =
        ConfigurationPropertiesMapContributor.fromSplitMap(properties);
    Configuration configuration = ConfigurationContributorChainBuilder.newChain()
        .add(propertiesContributor).build();
    return Optional.of(configuration);
  }

  protected String[] getCLIArguments(String model) {
    List<String> args = Lists.newArrayList(generatorArguments);
    args.add(getConfigProperty("model"));
    args.add(model);
    return args.toArray(new String[0]);
  }

  @BeforeClass
  public static void cleanOutputFolder() {
    // remove output folder:
    File outputFolder = new File(OUTPUT_FOLDER);
    if (outputFolder.exists()) {
      FileUtils.deleteQuietly(outputFolder);
    }
  }

  protected void doGenerate(String model) {
    Log.info("Runs test for the model " + model, LOG);
    Multimap<String, String> m = ArrayListMultimap.create();
    for (Map.Entry<String, Iterable<String>> s : CLIArguments.forArguments(getCLIArguments(model)).asMap().entrySet()) {
      m.putAll(s.getKey(), s.getValue());
    }
    new DexScript().run(getConfiguration(m));
  }

  /**
   * Compiles the files in the given directory, printing errors to the console
   * if any occur.
   *
   * @param model          - the model to test
   * @param sourceCodePath the source directory to be compiled
   * @return true if the compilation succeeded
   */
  protected boolean doCompile(String model, Path sourceCodePath) {
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    boolean compilationSuccess = false;
    Optional<CompilationTask> task = setupCompilation(sourceCodePath, diagnostics);
    if (!task.isPresent()) {
      Log.error("CompilationTask can't be created.");
      return false;
    }
    compilationSuccess = task.get().call();
    if (!compilationSuccess) {
      for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
        Log.error(diagnostic.toString());
      }
    }
    return compilationSuccess;
  }

  protected boolean compile(Path sourceCodePath) {
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    boolean compilationSuccess = false;
    Optional<CompilationTask> task = setupCompilation(sourceCodePath, diagnostics);
    if (!task.isPresent()) {
      return compilationSuccess;
    }
    compilationSuccess = task.get().call();
    if (!compilationSuccess) {
      for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
        Log.error(diagnostic.toString());
      }
    }
    return compilationSuccess;
  }

  /**
   * Instantiates all the parameters required for a CompilationTask and returns
   * the finished task.
   *
   * @param sourceCodePath the source directory to be compiled
   * @param diagnostics    a bin for any error messages that may be generated by
   *                       the compiler
   * @return the compilationtask that will compile any entries in the given
   * directory
   */
  protected Optional<CompilationTask> setupCompilation(Path sourceCodePath,
      DiagnosticCollector<JavaFileObject> diagnostics) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler != null) {
      StandardJavaFileManager fileManager = compiler
          .getStandardFileManager(diagnostics, null, null);
      // set compiler's classpath to be same as the runtime's
      List<String> optionList = Lists.newArrayList("-classpath",
          System.getProperty("java.class.path"));
      Iterable<? extends JavaFileObject> javaFileObjects = getJavaFileObjects(sourceCodePath,
          fileManager);
      return Optional.of(compiler.getTask(null, fileManager, diagnostics, optionList, null,
          javaFileObjects));
    }
    return Optional.empty();
  }

  /**
   * Creates an Iterable (horrible return type, but blame the Java standard
   * library) over all the JavaFileObjects contained in a given directory.
   *
   * @param sourceCodePath the directory from which JavaFileObjects are to be
   *                       retrieved
   * @param fileManager    the StandardJavaFileManager to be used for the
   *                       JavaFileObject creation
   * @return the JavaFileObjects contained in the given directory
   */
  protected Iterable<? extends JavaFileObject> getJavaFileObjects(Path sourceCodePath,
      StandardJavaFileManager fileManager) {
    Collection<File> files = FileUtils.listFiles(sourceCodePath.toFile(), new String[] { "java" },
        true);
    return fileManager.getJavaFileObjects(files.toArray(new File[files.size()]));
  }

  protected Path getPathToGeneratedCode(String model) {
    return Paths.get(OUTPUT_FOLDER,
        Names.getPathFromPackage(Names.getQualifier(model)).toLowerCase());
  }

  protected String getConfigProperty(String property) {
    return new StringBuilder("-").append(property).toString();
  }

  protected void testCorrect(String filename) {
    Log.info("Runs generator test for correct model " + filename, LOG);
    try {
      doGenerate(filename);
    }
    catch (Exception e) {
      Log.error("There are generation errors for model: " + filename, e);
      fail("There are generation errors for model: " + filename);
      return;
    }

    // switch of Reporting for compilation step
    boolean reportingOn = Reporting.isEnabled();
    if (reportingOn) {
      Reporting.off();
    }
    Path sourceCodePath = getPathToGeneratedCode(filename);
    if (!sourceCodePath.toFile().isDirectory()) {
      sourceCodePath = new File(OUTPUT_FOLDER + File.separator + sourceCodePath.getName(
          sourceCodePath.getNameCount() - 3).toFile().getName() + File.separator +
          sourceCodePath.getName(sourceCodePath.getNameCount() - 1).toFile().getName()).toPath();
    }
    if (!sourceCodePath.toFile().isDirectory()) {
      fail("Generation failed for model: " + filename);
    }
    boolean success = doCompile(filename, sourceCodePath);
    if (reportingOn) {
      //Reporting.on();
    }

    // clear output folder to prevent follow up tests to fail if the current
    // test failed
    try {
      FileUtils.deleteDirectory(sourceCodePath.toAbsolutePath().toFile());
    }
    catch (IOException e) {
      fail("Could not clear the output folder");
    }

    assertTrue("There are compile errors in generated code for model: " + filename, success);
  }

  protected void testFalse(String filename) {
    Log.info("Runs generator test for incorrect model " + filename
        + " (generation has to be canceled)", LOG);
    // remove output folder for this model
    File outputPath = new File(OUTPUT_FOLDER);
    if (outputPath.exists()) {
      FileUtils.deleteQuietly(outputPath);
    }
    // TODO AR, GV: check expected errors
    try {
      doGenerate(filename);
    }
    catch (Exception e) {
      Log.info("There are generation errors for model: " + filename, LOG);
      // TODO: Logs
    }
  }

}
