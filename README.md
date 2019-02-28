# MontiGEM

MontiGEM

# Projekt erstes mal bauen
- im Parent-Ordner: `mvn clean install -DskipTests -Pprod`
- in 04.Backend: `mvn clean docker:stop install -DskipTests docker:start tomee:run`
- in 03.Frontend (andere Konsole):
    - `npm install`
    - `mvn generate-sources -U`
    - `npm run start:jit`

# Backend bauen und starten
- in 04.Backend: `mvn clean docker:stop install -DskipTests docker:start tomee:run`

# Frontend neu generieren lassen
- in 03.Frontend: `mvn generate-sources -U`

# Frontend starten
- in 03.Frontend: `npm run start:jit`

# Website aufrufen
- Im Webbrowser `localhost:4200`
