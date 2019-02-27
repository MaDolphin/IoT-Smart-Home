/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package configure;

import com.google.common.collect.Sets;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides access to the aggregated configuration of a DEx instance derived
 * from (1) its command line arguments, and (2) system properties (not
 * implemented yet).
 * 
 * @author (last commit) $Author$
 */
public final class DexConfiguration implements Configuration {
  
  public static final String MODEL_EXTENSION = "cd";

  public static final String OCL_EXTENSION = "ocl";
  
  public static final String JAVA_EXTENSION = "java";
  
  public static final String FTL_EXTENSION = "ftl";

  public static final String TS_EXTENSION = "ts";
  
  public static final Set<String> DEX_EXTENSIONS = Sets.newHashSet(MODEL_EXTENSION, OCL_EXTENSION);
  
  public static final Set<String> BACKEND_HWC_EXTENSIONS = Sets.newHashSet(JAVA_EXTENSION);
  
  public static final Set<String> FTL_EXTENSIONS = Sets.newHashSet(FTL_EXTENSION);

  public static final Set<String> HWC_EXTENSIONS = Sets.newHashSet(BACKEND_HWC_EXTENSIONS);

  public static final Set<String> FRONTEND_HWC_EXTENSIONS = Sets.newHashSet(TS_EXTENSION);
  
  public static final String CONFIGURATION_PROPERTY = "_configuration";
  
  public static final String DEFAULT_OUTPUT_DIRECTORY = "out";

  public static final String DEFAULT_TEST_OUTPUT_DIRECTORY = "test";
  
  /**
   * The names of the specific DEx options used in this configuration.
   */
  public enum Options {
    
    MODEL("model"), MODEL_SHORT("m"), MODELS("models"), OCL("ocl"), CMDMODEL("cmdmodel"), MODELPATH("modelPath"), MODELPATH_SHORT("mp"),
    OUT("out"), OUT_SHORT("o"), TEST_OUT("testOut"), TEST_OUT_SHORT("tO"), HANDCODEDPATH("handcodedPath"), HANDCODEDPATH_SHORT("hcp"),
    TEMPLATEPATH("templatePath"), TEMPLATEPATH_SHORT("fp");
    
    String name;
    
    Options(String name) {
      this.name = name;
    }
    
    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return this.name;
    }
    
  }
  
  /**
   * Factory method for {@link DexConfiguration}.
   */
  public static DexConfiguration withConfiguration(Configuration configuration) {
    return new DexConfiguration(configuration);
  }
  
  private final Configuration configuration;
  
  /**
   * Constructor for {@link DexConfiguration}
   */
  private DexConfiguration(Configuration internal) {

    HWC_EXTENSIONS.addAll(FRONTEND_HWC_EXTENSIONS);
    
    this.configuration = ConfigurationContributorChainBuilder.newChain()
        .add(DelegatingConfigurationContributor.with(internal))
        .build();
    
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    return this.configuration.getAllValues();
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    return this.configuration.getAllValuesAsStrings();
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBoolean(java.lang.String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String key) {
    return this.configuration.getAsBoolean(key);
  }
  
  public Optional<Boolean> getAsBoolean(Enum<?> key) {
    return getAsBoolean(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBooleans(java.lang.String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String key) {
    return this.configuration.getAsBooleans(key);
  }
  
  public Optional<List<Boolean>> getAsBooleans(Enum<?> key) {
    return getAsBooleans(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDouble(java.lang.String)
   */
  @Override
  public Optional<Double> getAsDouble(String key) {
    return this.configuration.getAsDouble(key);
  }
  
  public Optional<Double> getAsDouble(Enum<?> key) {
    return getAsDouble(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDoubles(java.lang.String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String key) {
    return this.configuration.getAsDoubles(key);
  }
  
  public Optional<List<Double>> getAsDoubles(Enum<?> key) {
    return getAsDoubles(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsInteger(java.lang.String)
   */
  @Override
  public Optional<Integer> getAsInteger(String key) {
    return this.configuration.getAsInteger(key);
  }
  
  public Optional<Integer> getAsInteger(Enum<?> key) {
    return getAsInteger(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsIntegers(java.lang.String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String key) {
    return this.configuration.getAsIntegers(key);
  }
  
  public Optional<List<Integer>> getAsIntegers(Enum<?> key) {
    return getAsIntegers(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsString(java.lang.String)
   */
  @Override
  public Optional<String> getAsString(String key) {
    return this.configuration.getAsString(key);
  }
  
  public Optional<String> getAsString(Enum<?> key) {
    return getAsString(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsStrings(java.lang.String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String key) {
    return this.configuration.getAsStrings(key);
  }
  
  public Optional<List<String>> getAsStrings(Enum<?> key) {
    return getAsStrings(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValue(java.lang.String)
   */
  @Override
  public Optional<Object> getValue(String key) {
    return this.configuration.getValue(key);
  }
  
  public Optional<Object> getValue(Enum<?> key) {
    return getValue(key.toString());
  }
  
  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValues(java.lang.String)
   */
  @Override
  public Optional<List<Object>> getValues(String key) {
    return this.configuration.getValues(key);
  }

  @Override public boolean hasProperty(String s) {
    return this.configuration.hasProperty(s);
  }

  public Optional<List<Object>> getValues(Enum<?> key) {
    return getValues(key.toString());
  }
  
  /**
   * Getter for the model file to parse as stored in this configuration.
   * 
   * @return input model file
   */
  public File getModel() {
    Optional<String> model = getAsString(Options.MODEL);
    if (model.isPresent()) {
      return new File(model.get());
    }
    model = getAsString(Options.MODEL_SHORT);
    if (model.isPresent()) {
      return new File(model.get());
    }
    // no default; must specify model file to process
    Log.error("Please specify the model file.");
    // this return statement is bogus; hopefully fail quick is never deactivated
    // here!
    return new File("");
  }

    /**
     * Getter for the {@link IterablePath} consisting of model files stored in
     * this configuration.
     *
     * @return iterable model files
     */
    public IterablePath getModels() {
      Optional<List<String>> modelFiles = getAsStrings(Options.MODELS);
      if (modelFiles.isPresent() && checkPath(modelFiles.get())) {
        return IterablePath.from(toFileList(modelFiles.get()), MODEL_EXTENSION);
      }
      // no default; must specify model files/directories to process
      Log.warn("0xA1013 Please specify the model file(s).");
      return IterablePath.empty();
    }

  private boolean checkPath(List<String> models) {
    for (String g: models) {
      Path p = Paths.get(g);
      Log.info("model " + p, "DexScript");
      if (!Files.exists(p)) {
        Log.error("0xA1019 The requested path " + p.toString() + " does not exist.");
        return false;
      }
    }
    return true;
  }

  /**
   * Getter for the actual value of the model argument. This is not the prepared
   * {@link File} as in {@link DexConfiguration#getModel()} but the raw input
   * argument.
   * 
   * @return
   */
  public String getModelAsString() {
    Optional<String> model = getAsString(Options.MODEL);
    if (model.isPresent()) {
      return model.get();
    }
    model = getAsString(Options.MODEL_SHORT);
    if (model.isPresent()) {
      return model.get();
    }
    // no default; must specify model file to process
    Log.error("Please specify the model file.");
    return "";
  }

  public String getOCL() {
    Optional<String> model = getAsString(Options.OCL);
    if (model.isPresent()) {
      return model.get();
    }
    // no default; must specify model file to process
    //Log.error("Please specify the model file.");
    // this return statement is bogus; hopefully fail quick is never deactivated
    // here!
    return "";
  }

  public File getCmdModel() {
    Optional<String> cmdmodel = getAsString(Options.CMDMODEL);
    if (cmdmodel.isPresent()) {
      return new File(cmdmodel.get());
    }
    return new File("");
  }

  /**
   * Getter for the list of model path elements (files and directories) stored
   * in this configuration.
   * 
   * @return list of model path files
   */
  public List<File> getModelPath() {
    Optional<List<String>> modelPath = getAsStrings(Options.MODELPATH);
    if (modelPath.isPresent()) {
      return toFileList(modelPath.get());
    }
    modelPath = getAsStrings(Options.MODELPATH_SHORT);
    if (modelPath.isPresent()) {
      return toFileList(modelPath.get());
    }
    // default model path is empty
    return Collections.emptyList();
  }
  
  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "dex".
   * 
   * @return output directory file
   */
  public File getOut() {
    Optional<String> out = getAsString(Options.OUT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    out = getAsString(Options.OUT_SHORT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    // fallback default is "dex"
    return new File(DEFAULT_OUTPUT_DIRECTORY);
  }

  /**
   * Getter for the output directory for the tests stored in this configuration. A fallback
   * default is "dex".
   *
   * @return output directory file
   */
  public File getTestOut() {
    Optional<String> out = getAsString(Options.TEST_OUT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    out = getAsString(Options.TEST_OUT_SHORT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    // fallback default is "dex"
    return new File(DEFAULT_TEST_OUTPUT_DIRECTORY);
  }
  
  /**
   * Getter for the handcoded path directories stored in this configuration.
   * 
   * @return iterable handcoded files
   */
  public IterablePath getHandcodedPath() {
    Optional<List<String>> handcodedPath = getAsStrings(Options.HANDCODEDPATH);
    if (handcodedPath.isPresent()) {
      return IterablePath.from(toFileList(handcodedPath.get()), HWC_EXTENSIONS);
    }
    handcodedPath = getAsStrings(Options.HANDCODEDPATH_SHORT);
    if (handcodedPath.isPresent()) {
      return IterablePath.from(toFileList(handcodedPath.get()), HWC_EXTENSIONS);
    }
    // default handcoded path is empty
    return IterablePath.empty();
  }
  
  /**
   * Getter for the actual value of the handcoded path argument. This is not the
   * prepared {@link IterablePath} as in
   * {@link DexConfiguration#getHandcodedPath()} but the raw input arguments.
   * 
   * @return
   */
  public List<String> getHandcodedPathAsStrings() {
    Optional<List<String>> handcodedPath = getAsStrings(Options.HANDCODEDPATH);
    if (handcodedPath.isPresent()) {
      return handcodedPath.get();
    }
    handcodedPath = getAsStrings(Options.HANDCODEDPATH_SHORT);
    if (handcodedPath.isPresent()) {
      return handcodedPath.get();
    }
    // default handcoded path is empty
    return Collections.emptyList();
  }
  
  /**
   * Getter for the target path directories stored in this configuration.
   * 
   * @return iterable template files
   */
  public IterablePath getTemplatePath() {
    Optional<List<String>> templatePath = getAsStrings(Options.TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return IterablePath.from(toFileList(templatePath.get()), FTL_EXTENSIONS);
    }
    templatePath = getAsStrings(Options.TEMPLATEPATH_SHORT);
    if (templatePath.isPresent()) {
      return IterablePath.from(toFileList(templatePath.get()), FTL_EXTENSIONS);
    }
    // default template path is empty
    return IterablePath.empty();
  }
  
  /**
   * Getter for the actual value of the template path argument. This is not the
   * prepared {@link IterablePath} as in
   * {@link DexConfiguration#getTemplatePath()} but the raw input arguments.
   * 
   * @return
   */
  public List<String> getTemplatePathAsStrings() {
    Optional<List<String>> templatePath = getAsStrings(Options.TEMPLATEPATH);
    if (templatePath.isPresent()) {
      return templatePath.get();
    }
    templatePath = getAsStrings(Options.TEMPLATEPATH_SHORT);
    if (templatePath.isPresent()) {
      return templatePath.get();
    }
    // default template path is empty
    return Collections.emptyList();
  }
  
  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<File> toFileList(List<String> files) {
    return files.stream().collect(Collectors.mapping(file -> new File(file), Collectors.toList()));
  }
  
}
