<!-- (c) https://github.com/MontiCore/monticore -->
# MontiGEM

## Requirements
- maven
- settings.xml (contact your supervisor)
- java 8 (>= 8 Update 141) [Download Here](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html). Newer Versions of Java (e.g. Java 13) will lead to errors.
- [Docker](https://www.docker.com/products/docker-desktop)
    - After installing, leave the default to "Linux-Containers", **not** "Windows-Containers"
    - Windows: expose daemon: "Docker-Icon" -> "Settings" -> "Expose damon on TCP [...]"
    - Mac: See below `socat`
- node 8 (manageable with [nvm-windows](https://github.com/coreybutler/nvm-windows/releases) or [nvm](https://github.com/nvm-sh/nvm))
    - other versions of node (e.g. 12) lead to errors
    - `nvm install 8`
    - `nvm use 8.17.0` (Version Number is in the output of the previous command)

## First-time build
- in parent folder: 
    - `mvn clean install -DskipTests -Pprod -s settings.xml`
    - (in pom set monticonnect.src.dir absolute path to monticonnect generated montigem models directory.)
- in 04.BackendRTE: 
    - (`cd 04.BackendRTE`)
    - `mvn -s ../settings.xml clean docker:stop install -DskipTests docker:start tomee:run`
    - command will not terminate, instead open a new console
- in 03.FrontendRTE (other console):
    - (`cd 03.FrontendRTE`)
    - `npm install`
    - (in pom set monticonnect.src.dir absolute path to monticonnect generated montigem models directory.)
    - `mvn -s ../settings.xml generate-sources -U`
    - `npm run start:jit`
- open Browser http://localhost:4200
    - first visit triggers build of the Angular-Frontend
    - second visit displays page
    - username: "admin", pswd: "passwort"

## Backend build and start
- in 04.BackendRTE: `mvn -s ../settings.xml clean docker:stop install -DskipTests docker:start tomee:run`

### production mode
- in 04.BackendRTE pom set tomeeHost ip.

## Frontend
### regenerate
- in 03.FrontendRTE: `mvn -s ../settings.xml clean generate-sources -U`

### start
- in 03.FrontendRTE: `npm run start:jit`

### production mode
- in 03.FrontendRTE angular.json set host and publicHost
- in 03.FrontendRTE: `npm run start:prod`

## Website
- Browser `localhost:4200`
- user `admin`, pw: `passwort`

# Troubleshooting

## Backend
### Backend doesn't start on a Mac
- Install [socat](https://formulae.brew.sh/formula/socat) on Mac
- in 03.FrontendRTE: `npm start socat-fix`
- start backend as usual

## Debug Backend
### Create Intellij remote task (only first time)
- `Run` -> `Edit Configurations`
- *+* -> `Remote`
- `use Module classpath:` => `backend`

### Run the backend in debug mode
- `mvn clean docker:stop install -DskipTests docker:start tomee:debug`
- as soon as the backend tries to connect to the debugger (`Listening for transport dt_socket at address: 5005
[] CONNECT ATTEMPT -2147483585 on port: 8080
`): debug with Intellij (with selected remote configuration)

### Have a look at the logfiles
- find them in `04.BackendRTE/target/apache-tomee/logs/`

# Introduction

## Models
- contains all the used models
- the important one is `Domain.cd` (`00.Models/src/main/models/de/montigem/be/domain/Domain.cd`), which is the base model for all data classes

## Generator
- based on the [MontiCore](https://www.se-rwth.de/publications/MontiCore-5-Language-Workbench-Edition-2017.pdf) generator environment
- extended to generate most parts of a enterprise application

### Groovy
- controlled with `groovy` scripts (`01.Generator/src/backend/scripts/domain.groovy`, similar in frontend)
- entry point for the generator
- receives models (by path)
- transforms the parsed *AST*
- writes files, based on the changed *AST* to `.../target/generated-sources`
- if there exists a handwritten file (same package and same name) for a generated class, then the generator adds `TOP` to the name, the developer should then extend the `<Classname>TOP` class in the handwritten one  

## OCL
- contains the adapted *OCL/P* language
- usually not necessary to change

## Frontend
- written with angular
- each page is a *component* and can contain *subcomponent*s
- typescript files contain the logic of a component

### GuiDSL
- uses gui models in `00.Models/src/main/models/de/montigem/gui/*`
- generates *typescript* and *html* files

## Backend
- JavaEE, REST

### Database
- the database is running in a docker container

Container
- `db`: contains the systems data
- `datasource`: has the mapping, where to find different instances
