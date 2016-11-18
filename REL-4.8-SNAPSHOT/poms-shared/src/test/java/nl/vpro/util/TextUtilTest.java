/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import org.junit.Test;

import static nl.vpro.util.TextUtil.isValid;
import static nl.vpro.util.TextUtil.sanitize;
import static org.fest.assertions.Assertions.assertThat;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class TextUtilTest {

    private static String MSE_2589 = "Uit een grondige analyse in het British Medical Journal van duizenden Amerikaanse auto-ongelukken waarbij een of beide bestuurders omkwamen, blijkt dat te zware bestuurders meer kans hebben om bij zo'n crash het leven te laten dan mensen met een normaal gewicht. Bij mannen loopt het risico evenredig op met de body mass index (BMI). Een ernstig obese man (BMI > 40) heeft bijna twee keer zo veel risico op een fatale afloop als iemand met een normale BMI (tussen 18,5 en 25).  Anderzijds, heel magere mannen (BMI < 18,5) blijken net zo kwetsbaar als de ernstig obesen. Bij vrouwelijke bestuurders is het beeld iets anders. Magere vrouwen zijn net zo crashbestendig als vrouwen met een normaal gewicht, maar daarna loopt ook bij hen het overlijdensrisico op met de BMI. Er is echter weinig verschil in overlijdensrisico tussen ernstig obese (BMI > 40) en 'gewoon' obese (35 <  BMI  < 40) vrouwen: beide categorieën overlijden twee keer zo vaak als slanke vrouwen. Als je simpelweg zou turven hoeveel dikke/normale/magere mensen omkomen bij auto-ongelukken, is er een scala aan confounders (letterlijk: 'verwarrers') die tot valse resultaten kan leiden. Misschien rijden dikke mensen minder vaak zelf, en is de passagiersplaats gevaarlijker dan de bestuurdersplaats. Misschien rijden dikke mensen in kleinere, kwetsbaarder auto's. Misschien doen dikke mensen vaker hun gordel niet om, of zitten ze vaker te bellen of te eten achter het stuur. Misschien rijden ze slordiger of roekelozer. Epidemiologen Thomas Rice en Motao Zhu bekeken daarom alleen ongelukken tussen twee auto's van hetzelfde type en ongeveer dezelfde grootte, en matchten deze paren bestuurders ook nog qua gebruik van de autogordel. Verder zijn twee auto's die met elkaar botsen, automatisch gematched op eigenschappen als de tijd van de dag, de drukte op de weg, de weersomstandigheden en de hevigheid van de botsing. Als je ook één-auto-crashes meeneemt, zijn dat allemaal potentiele confounders. Bijvoorbeeld: als dikke mensen gemiddeld harder zouden rijden dan slanke, dan rijden ze zichzelf vaker dood. Uiteindelijk hielden Rice en Zhu een kleine 3500 autobotsingen over met bijna 7000 paarsgewijs gematchte bestuurders, waaruit het verband tussen BMI en overlijdensrisico volgde. De grote vraag is natuurlijk: hoe komt dat? De onderzoekers speculeren, dat dikke mensen door hun vetlaag rond de heup niet goed vast te snoeren zijn in de heupgordel. Daardoor vliegen ze bij een botsing eerst een stuk naar voren, voordat de heupbotten gestuit worden door de gordel, en dat zou de geïncasseerde klap vergroten. Andere onderzoekers hebben dit effect zelfs getest door auto's te laten crashen in het laboratorium met dikke of slanke lijken op de bestuurdersplaats. Maar waarom dit tot een hogere sterftekans zou leiden blijft de vraag.  Een andere verklaring, net zo speculatief, is dat dikke mensen gemiddeld een zwakkere gezondheid hebben, en dus eerder het loodje leggen als de klap van een auto-botsing daar bovenop komt. Een simpele fysische verklaring die de onderzoekers buiten beschouwing laten, zou je het olifant-effect kunnen noemen. Een auto-botsing is vergelijkbaar met van een zekere hoogte verticaal op de grond vallen. De hoge piekvertraging (in een fractie van een seconde van 30 of 50 km/u naar 0) is in wezen wat de schade aanricht.  Een kat of hond kan probleemloos van anderhalve meter hoog op een stenen vloer springen, maar een olifant die hetzelfde probeert, breekt minstens een paar botten en overleeft het misschien niet eens.  Je kunt je ook zelf het verschil voorstellen tussen enerzijds, van anderhalve meter hoogte van een muurtje springen, en hetzelfde doen met 25 kilo aan halterschijven om je middel.  Hoe meer kilo's je meetorst, hoe harder de klap van een hoge piekvertraging aankomt. Dat geldt zeker als een bestuurder de gordel niet om heeft en door het auto-interieur tot stilstand wordt gebracht bij een botsing. Driver obesity and the risk of fatal injury during traffic collisions, T.Rice, M. Zhu, British Medical journal, 21 januari 2013";

    @Test
    public void testPatternOnTag() {
        assertThat(isValid("Tekst met <bold>html</bold>")).isFalse();
    }

    @Test
    public void testPatternOnLowerThen() {
        assertThat(isValid("a < b")).isTrue();
    }

    @Test
    public void testMSE_2589() {
        assertThat(isValid(MSE_2589)).isTrue();
    }


    @Test
    public void testPatternOnValidAmpersand() {
        assertThat(isValid("Pauw & Witteman;")).isTrue();
    }

    @Test
    public void testPatternOnNumericEscape() {
        assertThat(isValid("&#233;")).isFalse();
    }

    @Test
    public void testPatternOnTextEscape() {
        assertThat(isValid("&eacute;")).isFalse();
    }

    @Test
    public void testPatternOnTextEscapeWithUC() {
        assertThat(isValid("&Ograve;")).isFalse();
    }

    @Test
    public void testPatternOnTextEscapeWithDigits() {
        assertThat(isValid("&frac14;")).isFalse();
    }

    @Test
    public void testPatternOnLineBreak() {
        assertThat(isValid("Text with line\u2028break.")).isFalse();
    }

    @Test
    public void testPatternOnNewLine() {
        assertThat(isValid("Text with newline\nbreak.")).isTrue();
    }

    @Test
    public void testSanitizePreserveInputWithSmallerThen() {
        assertThat(sanitize("a < b")).isEqualTo("a < b");
    }

    @Test
    public void testSanitizePreserveInputWithAmpersand() {
        assertThat(sanitize("a & b")).isEqualTo("a & b");
    }

    @Test
    public void testSanitizeOnHtmlInput() {
        assertThat(sanitize("<p>Hello world</p><br>")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnHtmlFantasy() {
        assertThat(sanitize("<fantasy>Hello world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnAmpersands() {
        assertThat(sanitize("Hello&nbsp;world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnIllegalLineBreak() {
        assertThat(sanitize("Hello\u2028world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnHtmlEscapedNbsp() {
        assertThat(sanitize("Hello&nbsp;world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeOnDoubleEscapedInput() {
        assertThat(sanitize("A &amp;amp; B")).isEqualTo("A & B");
    }

    @Test
    public void testSanitizeOnDoubleEscapedNbsp() {
        assertThat(sanitize("Hello&amp;nbsp;world")).isEqualTo("Hello world");
    }

    @Test
    public void testSanitizeUnicodeChar() throws UnsupportedEncodingException {
        String result = sanitize("KRO De Re&#252;nie");
        assertThat(result.getBytes("UTF8")).isEqualTo("KRO De Reünie".getBytes("UTF8"));
    }

    @Test
    public void testSanitizeMSE_2589() {
        String result = sanitize(MSE_2589);
        assertThat(result.replaceAll("\\s", "")).isEqualTo(MSE_2589.replaceAll("\\s", ""));
    }

    @Test
    public void testLexico() {
        assertThat(TextUtil.getLexico("Het grote huis", new Locale("nl", "NL"))).isEqualTo("Grote huis, het");
    }

    @Test
    public void testLexicoLowercase() {
        assertThat(TextUtil.getLexico("het grote huis", new Locale("nl", "NL"))).isEqualTo("grote huis, het");
    }

    @Test
    public void testLexicoUppercase() {
        assertThat(TextUtil.getLexico("HET GROTE HUIS", new Locale("nl", "NL"))).isEqualTo("GROTE HUIS, HET");
    }

    @Test
    public void testLexicoOtherwise() {
        assertThat(TextUtil.getLexico("Daar gaat ie weer", new Locale("nl", "NL"))).isEqualTo("Daar gaat ie weer");
    }

    @Test
    public void testLexicoNoWordboundary() {
        assertThat(TextUtil.getLexico("Hete broodjes", new Locale("nl", "NL"))).isEqualTo("Hete broodjes");
    }

    @Test
    public void testLexicoParticleOnly() {
        assertThat(TextUtil.getLexico("Het", new Locale("nl", "NL"))).isEqualTo("Het");
    }

    @Test
    public void testTruncateLong() {
        assertThat(TextUtil.truncate("Bla bla. Bloe bloe", 10)).isEqualTo("Bla bla.");
    }

    @Test
    public void testTruncateShorter() {
        assertThat(TextUtil.truncate("Bla bla", 3)).isEqualTo("Bla");
    }

    @Test
    public void testTruncateShort() {
        assertThat(TextUtil.truncate("Bla bla. Bloe bloe", 3)).isEqualTo("Bla");

    }

    @Test
    public void testTruncateShort2() {
        assertThat(TextUtil.truncate("Bla bla. Bloe bloe", 5)).isEqualTo("Bla");

    }
}
