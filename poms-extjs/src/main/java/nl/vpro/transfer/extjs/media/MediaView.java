package nl.vpro.transfer.extjs.media;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaObjects;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder =
    {
        "title",
        "subTitle",
        "shortTitle",
        "workTitle",
        "originalTitle",
        "lexicoTitle",
        "nextLexicoTitle",
        "abbreviatedTitle",
        "description",
        "shortDescription",
        "kickerDescription"
        })
public abstract class MediaView {
    protected String title;

    protected String subTitle;

    protected String shortTitle;

    protected String originalTitle;

    protected String lexicoTitle;

    protected String nextLexicoTitle;

    protected String workTitle;

    protected String abbreviatedTitle;

    protected String description;

    protected String shortDescription;

    protected String kickerDescription;

    protected MediaView() {
    }


    protected void init(MediaObject fullMedia) {
        title    = fullMedia.getMainTitle();
        subTitle = fullMedia.getSubTitle();
        shortTitle = fullMedia.getShortTitle();
        workTitle =  fullMedia.getWorkTitle();
        originalTitle = fullMedia.getOriginalTitle();
        Iterator<Title> lex = MediaObjects.getTitles(fullMedia.getTitles(), TextualType.LEXICO).iterator();
        if (lex.hasNext()) {
            Title next = lex.next();
            if (next.getOwner() == OwnerType.BROADCASTER) {
                lexicoTitle = next.getTitle();
                if (lex.hasNext()) {
                    nextLexicoTitle = lex.next().getTitle();
                } else {
                    nextLexicoTitle = "";
                }
            } else {
                // shown title is not owned by broadcaster, but will be on safe.
                lexicoTitle = next.getTitle();
                nextLexicoTitle = next.getTitle();
            }
        }


        abbreviatedTitle = fullMedia.getAbbreviatedTitle();

        description = fullMedia.getMainDescription();
        shortDescription = fullMedia.getShortDescription();
        kickerDescription = MediaObjects.getDescription(fullMedia, TextualType.KICKER);
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getWorkTitle() {
        return workTitle;
    }

    public String getAbbreviatedTitle() {
        return abbreviatedTitle;
    }

    public String getLexicoTitle() {
        return lexicoTitle;
    }

    /**
     * Used to determin whether the lexico title can be removed because it is equal to the main title
     * See MSE-2291
     * @return
     */
    public String getNextLexicoTitle() {
        return nextLexicoTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getKickerDescription() {
        return kickerDescription;
    }
}
