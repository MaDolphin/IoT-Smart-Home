/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be;

import de.montigem.be.ZahlwortDeutsch;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ZahlwortTest {

    @Test
    public void testZahlwort() {
        ZahlwortDeutsch.longConvert(Long.MAX_VALUE);

        assertEquals("", ZahlwortDeutsch.longConvert(0));
        assertEquals("zwölf Cent", ZahlwortDeutsch.longConvert(12));
        assertEquals("dreizehn Euro ", ZahlwortDeutsch.longConvert(1300));
        assertEquals("einundzwanzig Euro ", ZahlwortDeutsch.longConvert(2100));
        assertEquals("minus ein Cent", ZahlwortDeutsch.longConvert(-1));
        assertEquals("minus achtundachtzig Euro und sechsundvierzig Cent", ZahlwortDeutsch.longConvert(-8846));
        assertEquals("eintausendvierzig Euro und einundfünfzig Cent", ZahlwortDeutsch.longConvert(104051));
        assertEquals("eine millionen zweihundertvierunddreißigtausendfünfhundertsiebenundsechzig Euro und neunundachtzig Cent",
                ZahlwortDeutsch.longConvert(123456789));
    }
}
