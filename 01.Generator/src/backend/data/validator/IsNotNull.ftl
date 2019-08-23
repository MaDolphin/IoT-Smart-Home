<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The core method in a builder to validate if an object is consistent.

  @author: SE RWTH Aachen
-->
${signature("paramName")}

{

// Ich glaube, dass das Fehlen eines Werts (also sein null sein)
// nur auf einen ernsthaften internen Fehler zurückgeführt werden kann
// und daher mt einer adäquaten Fehlermeldung & dann auch Behandlung 
// bearbeitet werden muss

// diese Methode ist also keine echte "Validierungsmethode", sondern 
// eine scharfe Vorbedingung. Was soll denn ein Nutzer auch machen, 
// wenn wir im Hintergrund kein Objekt anlegen --> kann kein Nutzerfehler sein

  if (${paramName} == null) {
    String msg = "0xEFFFF Interner Fehler: ${paramName} ist null. Sorry.\n";
    Log.warn(msg);
    return Optional.of(msg);
  }

  return Optional.empty();
}
