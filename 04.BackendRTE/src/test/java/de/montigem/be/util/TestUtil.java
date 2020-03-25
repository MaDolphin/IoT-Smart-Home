/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.util;

import de.montigem.be.GeneralService;
import de.montigem.be.marshalling.JsonMarshal;
import de.se_rwth.commons.logging.Log;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Collection of utility methods for the implementation of Unit Tests
 * 
 */
public class TestUtil {

  /**
   * Reusable utility method Move to a shared class or replace with equivalent
   */
  public static String slurp(final InputStream in) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final byte[] buffer = new byte[1024];
    int length;
    while ((length = in.read(buffer)) != -1) {
      out.write(buffer, 0, length);
    }
    out.flush();
    return new String(out.toByteArray());
  }
  
  /**
   * Reusable utility method Move to a shared class or replace with equivalent
   * 
   * @throws Exception
   */
  public static <T> T slurp(final InputStream in, final Class<T> clazz) throws Exception {
    return (T) (JsonMarshal.getInstance().unmarshal(slurp(in), clazz));
  }
  
  public static void addClassesToWebArchive(WebArchive wa) {
    wa.addPackages(true, GeneralService.class.getPackage());
    wa.setWebXML(new File("src/main/webapp-test/WEB-INF/web.xml"));
    wa.addAsResource("META-INF/persistence.xml");
    wa.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    wa.addAsWebInfResource(new File("src/main/webapp-test/WEB-INF/resources.xml"));
  }

  /**
   * TODO: Write me!
   *
   * @param d1
   * @param d2
   * @return
   */
  public static boolean compareDates(ZonedDateTime d1, ZonedDateTime d2) {
    // round to seconds
    ZonedDateTime d1_truncated = d1, d2_truncated = d2;

    if (d1_truncated.getNano() >= 500_000_000) {
      d1_truncated = d1_truncated.plus(1, ChronoUnit.SECONDS);
    }
    d1_truncated = d1_truncated.truncatedTo(ChronoUnit.SECONDS);

    if (d2_truncated.getNano() >= 500_000_000) {
      d2_truncated = d2_truncated.plus(1, ChronoUnit.SECONDS);
    }
    d2_truncated = d2_truncated.truncatedTo(ChronoUnit.SECONDS);

    boolean res = JsonMarshal.getInstance().marshal(d1_truncated)
        .equals(JsonMarshal.getInstance().marshal(d2_truncated));
    if (!res) {
      Log.trace("dates differ: " + JsonMarshal.getInstance().marshal(d1) + ", "
          + JsonMarshal.getInstance().marshal(d2), TestUtil.class.getName());
    }
    return res;
  }

  public static boolean compareDates(Optional<ZonedDateTime> d1, Optional<ZonedDateTime> d2) {
    if (d1.isPresent() && d2.isPresent()) {
      return compareDates(d1.get(), d2.get());
    }
    else {
      return !d1.isPresent() && !d2.isPresent();
    }
  }

}
