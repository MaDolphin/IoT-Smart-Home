<!-- (c) https://github.com/MontiCore/monticore -->
# MontiGEM

## Requirements
- maven
- java 8 (>= 8 Update 141)
- docker (https://www.docker.com/products/docker-desktop)
- node 8 (manageable with https://github.com/coreybutler/nvm-windows/releases)
- settings.xml (contact your supervisor)

## First-time build
- in parent folder: `mvn clean install -DskipTests -Pprod`
- in 04.Backend: `mvn clean docker:stop install -DskipTests docker:start tomee:run`
- in 03.Frontend (other console):
    - `npm install`
    - `mvn generate-sources -U`
    - `npm run start:jit`

## Backend build and start
- in 04.Backend: `mvn clean docker:stop install -DskipTests docker:start tomee:run`

## Frontend
### regenerate
- in 03.Frontend: `mvn generate-sources -U`

### start
- in 03.Frontend: `npm run start:jit`

## Website
- Browser `localhost:4200`
- user `admin`, pw: `passwort`

# Troubleshooting

## Debug Backend
### create Intellij remote task (only first time)
- `Run` -> `Edit Configurations`
- *+* -> `Remote`
- `use Module classpath:` => `backend`

### run the backend in debug mode
- `mvn clean docker:stop install -DskipTests docker:start tomee:debug`
- as soon as the backend tries to connect to the debugger (`Listening for transport dt_socket at address: 5005
[] CONNECT ATTEMPT -2147483585 on port: 8080
`): debug with Intellij (with selected remote configuration)

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