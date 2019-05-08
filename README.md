# MontiGEM

## Requirements
- maven
- java 8 >= 8 Update 141
- docker (https://www.docker.com/products/docker-desktop)
- node 8 (manageable with https://github.com/coreybutler/nvm-windows/releases)

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
