/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be;

public class ZahlwortDeutsch{
public static final String[] units = {
        "", "ein", "zwei", "drei", "vier", "fünf", "sechs", "sieben",
        "acht", "neun", "zehn", "elf", "zwölf", "dreizehn", "vierzehn",
        "fünfzehn", "sechzehn", "siebzehn", "achtzehn", "neunzehn"
        };

public static final String[] tens = {
        "", // 0
        "", // 1
        "zwanzig", // 2
        "dreißig", // 3
        "vierzig", // 4
        "fünfzig", // 5
        "sechzig", // 6
        "siebzig", // 7
        "achtzig", // 8
        "neunzig" // 9
        };

public static String longConvert(final long n) {

        String pass = "";

        long preDecimal = Math.abs(n / 100);
        long postDecimal = Math.abs(n % 100);

        if(n < 0)
            pass += "minus ";
        if(preDecimal>0) {
                if(preDecimal==1){
                     pass+= "ein Euro ";
                }
                else {
                        pass += convert(preDecimal) ;
                        pass += " Euro ";
                        if (postDecimal > 0) {
                                if(postDecimal==1){
                                        pass += "und ein Cent";
                                }
                                else {
                                        pass += "und" + " ";
                                        pass += convert(postDecimal);
                                        pass += " Cent";
                                }
                        }
                }
        }
        else if(postDecimal>0) {
                if(postDecimal==1){
                        pass += "ein Cent";
                }
                else {
                        pass += convert(postDecimal);
                        pass += " Cent";
                }

        }

        return pass;
        }

public static String convert(final long n) {
        if (n == 0) {
        return "";
        }

        if (n < 20) {
        return units[(int)n];
        }

        if (n < 100) {
        return (n % 10 >0 ? units[(int)n % 10] +"und": "") + tens[(int)n / 10];
        }

        if (n < 1000) {
                if((int)n / 100!=1) {
                        return units[(int) n / 100] + "hundert" + ((n % 100 != 0) ? "" : "") + convert(n % 100);
                }
                else{
                        return "einhundert" + ((n % 100 != 0) ? "" : "") + convert(n % 100);
                }
        }

        if (n < 1000000) {
                if((int)n/1000!=1) {
                        return convert(n / 1000) + "tausend" + ((n % 1000 != 0) ? "" : "") + convert(n % 1000);
                }
                else{
                        return "eintausend" + ((n % 1000 != 0) ? "" : "") + convert(n % 1000);
                }
        }

        if (n < 1000000000) {
                if((int)n/1000000!=1) {
                        return convert(n / 1000000) + " millionen " + ((n % 1000000 != 0) ? " " : "") + convert(n % 1000000);
                }
                else{
                        return "eine millionen " + ((n % 1000000 != 0) ? "" : "") + convert(n % 1000000);
                }
        }
        if((int)n/1000000000!=1) {
                return convert(n / 1000000000) + " milliarden " + ((n % 1000000000 != 0) ? " " : "") + convert(n % 1000000000);
        }
        else{
                return "eine milliarden " + ((n % 1000000000 != 0) ? "" : "") + convert(n % 1000000000);
        }

        }
}
