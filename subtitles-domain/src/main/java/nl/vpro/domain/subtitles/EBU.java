package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Code borrowed from https://github.com/JDaren/subtitleConverter
 *
 * Adapted so it returns a stream of our {@link Cue} objects
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class EBU {

    private EBU() {
    }


    public static Stream<Cue> parse(String parent, Duration offset, Function<TimeLine, Duration> offsetGuesser, InputStream is) {

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(parseToIterator(parent, offset, offsetGuesser, is), Spliterator.ORDERED), false);
    }

    protected static Iterator<Cue> parseToIterator(final String parent, final Duration offsetArgument, Function<TimeLine, Duration> offsetGuesser, final InputStream is) {

        if (is == null) {
            throw new IllegalArgumentException("Inputstream cannot be null");
        }
        return new Iterator<Cue>() {

            protected Cue next;

            protected Boolean hasNext = null;
            private int i = 0;
            final byte[] ttiBlock = new byte[128];
            final int numberOfTTIBlocks;
            final int fps;
            final int numberOfSubtitles;
            int subtitleNumber = 1;
            Duration offset = offsetArgument;


            {
                byte[] gsiBlock = new byte[1024];



                try {
                    //we read the file
                    //but first we create the possible styles
                    int bytesRead = is.read(gsiBlock);
                    //the GSI block is loaded
                    if (bytesRead < 1024) {
                        //the file must contain at least a GSI block and a TTI block
                        //this is a fatal parsing error.
                        throw new IllegalArgumentException("The file must contain at least a GSI block, but read only " + bytesRead + " (< 1024)");
                    }
                    //CPC : code page number 0..2
                    //DFC : disk format code 3..10
                    //save the number of frames per second
                    byte[] dfc = {gsiBlock[6], gsiBlock[7]};
                    fps = Integer.parseInt(new String(dfc));
                    //DSC : Display Standard Code 11
                    //CCT : Character Code Table number 12..13
                    byte[] cct = {gsiBlock[12], gsiBlock[13]};
                    int table = Integer.parseInt(new String(cct));
                    //LC : Language Code 14..15
                    //OPT : Original Programme Title 16..47
                    byte[] opt = new byte[32];
                    System.arraycopy(gsiBlock, 16, opt, 0, 32);
                    String title = new String(opt);
                    //OEP : Original Episode Title 48..79
                    byte[] oet = new byte[32];
                    System.arraycopy(gsiBlock, 48, oet, 0, 32);
                    String episodeTitle = new String(oet);
                    //TPT : Translated Programme Title 80..111
                    //TEP : Translated Episode Title 112..143
                    //TN : Translator's Name 144..175
                    //TCD : Translators Contact Details 176..207
                    //SLR : Subtitle List Reference code 208..223
                    //CD : Creation Date 224..229
                    //RD : Revision Date 230..235
                    //RN : Revision Number 236..237
                    //TNB : Total Number of TTI Blocks 238..242
                    byte[] tnb = {gsiBlock[238], gsiBlock[239], gsiBlock[240], gsiBlock[241], gsiBlock[242]};
                    numberOfTTIBlocks = Integer.parseInt(new String(tnb).trim());
                    //TNS : Total Number of Subtitles 243..247
                    byte[] tns = {gsiBlock[243], gsiBlock[244], gsiBlock[245], gsiBlock[246], gsiBlock[247]};
                    numberOfSubtitles = Integer.parseInt(new String(tns).trim());
                    //TNG : Total Number of Subtitle Groups 248..250
                    //MNC : Max Number of characters in row 251..252
                    //MNR : Max number of rows 253..254
                    //TCS : Time Code: Status 255
                    //TCP : Time Code: Start-of-Programme 256..263
                    //TCF : Time Code: First In-Cue 264..271
                    //TND : Total Number of Disks 272
                    //DSN : Disk Sequence Number 273
                    //CO : Country of Origin 274..276
                    //PUB : Publisher 277..308
                    //EN : Editor's Name 309..340
                    //ECD : Editor's Contact Details 341..372
                    // Spare bytes 373..447
                    //UDA : User-Defined Area 448..1023

                    //we add the title
                    //this checks the reference to the characters coding employed.
                    if (table > 4 || table < 0) {
                        log.error("Invalid Character Code table number, corrupt data? will try to parse anyways assuming it is latin.");
                    } else if (table != 0) {
                        log.error("Only latin alphabet supported for import from STL, other languages may produce unexpected results.");
                    }

                    int subtitleNumber = 1;
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean hasNext() {
                findNext();
                return hasNext;
            }

            @Override
            public Cue next() {
                findNext();
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                hasNext = null;
                return next;
            }


            protected void findNext() {

                if (hasNext == null) {
                    next = null;
                    try {
                        Cue toFill = new Cue();
                        toFill.parent = parent;
                        boolean additionalText = false;
                        StringBuilder currentContent = new StringBuilder();
                        //the TTI blocks are read
                        while (i < numberOfTTIBlocks && next == null) {
                            i++;
                            //the TTI block is loaded
                            int bytesRead = is.read(ttiBlock);
                            if (bytesRead < 128) {
                                //unexpected end of file
                                log.error("Unexpected end of file, {}  blocks read, expecting {} blocks in total.", i, numberOfTTIBlocks);
                                break;
                            }


                            //SGN : Subtitle group number 0
                            //SN : Subtitle Number 1..2
                            int currentSubNumber = Math.abs(ttiBlock[1] + 256 * ttiBlock[2]);
                            if (currentSubNumber != subtitleNumber) {
                                //missing subtitle number?
                                log.debug("Unexpected subtitle number {} (expected {}) at TTI block {}  Parsing proceeds...", subtitleNumber, currentSubNumber, i);
                            }
                            toFill.sequence = subtitleNumber;
                            toFill.identifier = "" + subtitleNumber;

                            //EBN : Extension Block Number 3
                            int ebn = ttiBlock[3];
                            if (ebn != -1 && ebn != -2) {
                                additionalText = true;
                            } else if (ebn == -2) {
                                //EBN is UserData so Jump it.
                                additionalText = false;
                                continue;
                            } else {
                                additionalText = false;
                            }
                            //CS : Cumulative Status 4
                            //TCI : Time Code In 5..8
                            String startTime = "" + ttiBlock[5] + ":" + ttiBlock[6] + ":" + ttiBlock[7] + ":" + ttiBlock[8];
                            //TCO : Time Code Out 9..12
                            String endTime = "" + ttiBlock[9] + ":" + ttiBlock[10] + ":" + ttiBlock[11] + ":" + ttiBlock[12];
                            ;
                            //VP : Vertical Position 13
                            //JC : Justification Code 14
                            int justification = ttiBlock[14];
                            //0:none, 1:left, 2:centered, 3:right
                            //CF : Comment Flag 15
                            if (ttiBlock[15] == 0) {
                                //comments are ignored
                                //TF : Text Field 16..112
                                byte[] textField = new byte[112];
                                System.arraycopy(ttiBlock, 16, textField, 0, 112);

                                if (additionalText)
                                    //if it is just additional text for the caption
                                    parseTextForSTL(currentContent, textField, justification);
                                else {
                                    TimeLine timeLine = parseTime(startTime, endTime, fps);
                                    if (offset == null) {
                                        offset = offsetGuesser.apply(timeLine);
                                    }
                                    toFill.start = parseTime(startTime + "/" + fps).minus(offset);
                                    toFill.end = parseTime(endTime + "/" + fps).minus(offset);
                                    parseTextForSTL(currentContent, textField, justification);
                                }
                            }
                            //we increase the subtitle number
                            if (!additionalText) {
                                subtitleNumber++;
                                next = toFill;
                                break;
                            }

                        }
                        if (next != null) {
                            hasNext = true;
                            next.content = currentContent.toString().trim();
                        } else {
                            hasNext = false;
                            //we close the reader
                            is.close();
                            if (subtitleNumber != numberOfSubtitles) {
                                log.debug("Number of parsed subtitles ({}) different from expected number of subtitles ({})", subtitleNumber, numberOfSubtitles);
                            }
                        }

                    } catch (Exception e) {
                        hasNext = false;
                        //format error
                        throw new IllegalArgumentException("Format error in the file, might be due to corrupt data." + e.getMessage(), e);
                    }
                }
            }

        };
    }


    private static TimeLine parseTime(String startTime, String endTime, int fps) {
        return new TimeLine(null, parseTime(startTime + "/" + fps), parseTime(endTime + "/" + fps));
    }

    /**
     * This method parses the text field taking into account STL control codes
     */
    private static void parseTextForSTL(StringBuilder currentCaption, byte[] textField, int justification) {

        boolean italics = false;
        boolean underline = false;
        int diacritical_mark = 0;
        String color = "white";
        String text = "";

        //we go around the field in pair of bytes to decode them
        for (int i = 0; i < textField.length; i++) {

            if (textField[i] < 0) {
                //first byte > 8 (4 bits)
                if (textField[i] <= -113) {
                    //we might be with a  control code
                    if (i + 1 < textField.length && textField[i] == textField[i + 1])
                        i++; //if repeated skip one
                    switch (textField[i]) {
                        case -128:
                            italics = true;
                            break;
                        case -127:
                            italics = false;
                            break;
                        case -126:
                            underline = true;
                            break;
                        case -125:
                            underline = false;
                            break;
                        case -124:
                            //Boxing not supported
                            break;
                        case -123:
                            //Boxing not supported
                            break;
                        case -118:
                            //line break
                            currentCaption.append(text.trim()).append("\n");
                            text = "";
                            break;
                        case -113:
                            //end of caption
                            currentCaption.append(text.trim());
                            text = "";
                            //we check the style
                            if (underline)
                                color += "U";
                            if (italics)
                                color += "I";
                            //style = tto.styling.get(color);

                            if (justification == 1) {
                                color += "L";

                            } else if (justification == 3) {
                                color += "R";

                            }

                            //we end the loop
                            i = textField.length;
                            break;
                        default:
                            //non valid code...
                    }
                } else if (textField[i] >= -64 && textField[i] <= -49) {
                    // diacritical characters
                    diacritical_mark = textField[i];
                } else {
                    //other codes and non supported characters...
                    //corresponds to the upper half of the character code table
                }
            } else if (textField[i] < 32) {
                //it is a teletext control code, only colors are supported
                if (i + 1 < textField.length && textField[i] == textField[i + 1])
                    i++; //if repeated skip one
                switch (textField[i]) {
                    case 7:
                        color = "white";
                        break;
                    case 2:
                        color = "green";
                        break;
                    case 4:
                        color = "blue";
                        break;
                    case 6:
                        color = "cyan";
                        break;
                    case 1:
                        color = "red";
                        break;
                    case 3:
                        color = "yellow";
                        break;
                    case 5:
                        color = "magenta";
                        break;
                    case 0:
                        color = "black";
                        break;
                    default:
                        //non supported
                }

            } else {
                //we have a supported character coded in the two bytes, range is from 0x20 to 0x7F
                byte[] x = {textField[i]};
                String raw_string = new String(x);

                if (diacritical_mark != 0) {
                    if ((diacritical_mark == -62) && (textField[i] == 101)) raw_string = "é";
                    else if ((diacritical_mark == -56) && (textField[i] == 105)) raw_string = "ï";
                    else if ((diacritical_mark == -63) && (textField[i] == 97)) raw_string = "à";
                    else if ((diacritical_mark == -56) && (textField[i] == 101)) raw_string = "ë";
                    else if ((diacritical_mark == -61) && (textField[i] == 101)) raw_string = "ê";
                    else if ((diacritical_mark == -63) && (textField[i] == 117)) raw_string = "ù";
                    else if ((diacritical_mark == -61) && (textField[i] == 105)) raw_string = "î";
                    else if ((diacritical_mark == -63) && (textField[i] == 101)) raw_string = "è";
                    else if ((diacritical_mark == -61) && (textField[i] == 97)) raw_string = "â";
                    else if ((diacritical_mark == -61) && (textField[i] == 111)) raw_string = "ô";
                    else if ((diacritical_mark == -61) && (textField[i] == 117)) raw_string = "û";
                    else if ((diacritical_mark == -53) && (textField[i] == 99)) raw_string = "ç";
                    else if ((diacritical_mark == -56) && (textField[i] == 97)) raw_string = "ä";
                    else if ((diacritical_mark == -56) && (textField[i] == 111)) raw_string = "ö";
                    else if ((diacritical_mark == -56) && (textField[i] == 117)) raw_string = "ü";
                    diacritical_mark = 0;
                }
                text += raw_string;
            }

        }
    }

    /**

     * @param value  string in the correct format
     */
    private static Duration parseTime(String value) {
        int h, m, s, f;
        String[] args = value.split("/");
        float fps = Float.parseFloat(args[1]);
        args = args[0].split(":");
        h = Integer.parseInt(args[0]);
        m = Integer.parseInt(args[1]);
        s = Integer.parseInt(args[2]);
        f = Integer.parseInt(args[3]);

        return Duration.ofMillis((long) (f * 1000L / fps) + s * 1000 + m * 60000 + h * 3600000);
    }

}
