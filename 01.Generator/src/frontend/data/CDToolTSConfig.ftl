${tc.signature( "importMap")}
{
  "compilerOptions": {
    "paths": {
      // static part:
        "@shared/*": ["src/app/shared/*"],
        "@targetgui/*" : ["target/generated-sources/gui/*"],
        "@targetdtos/*" : ["target/generated-sources/dtos/*"],
        "@srcdtos/*" : ["src/app/shared/architecture/data/dtos/*"],
        "@targetrte/*": ["target/generated-sources/rte/*"],
        "@components/*": ["src/app/shared/components/guidsl/*"],
        "@utils/*": ["src/app/utils/*"],

        // Test
        "@testutils/*": ["src/tests/utils/*"],

        // Services
        "@services/*": ["src/app/shared/architecture/services/*"],
        "@jsonapiservice/*": ["src/app/shared/architecture/services/*"],
        "@loggerservice/*": ["src/app/shared/architecture/services/*"],

        // Commands
        "@commands/*": ["target/generated-sources/commands/*"],

      // based on input-models:
      <#list importMap?keys as key>
        "@${key?lower_case}/*": ["${importMap[key]}"],
      </#list>
    }
  }
}
