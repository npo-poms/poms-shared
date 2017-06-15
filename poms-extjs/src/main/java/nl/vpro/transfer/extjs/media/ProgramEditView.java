/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Platform;
import nl.vpro.domain.media.Prediction;
import nl.vpro.domain.media.Program;

@XmlRootElement(name = "program")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "platforms",
        "episodeOf",
        "segments",
        "poSeriesID",
        "poProgType"
})
public class ProgramEditView extends MediaEditView{

    private int episodeOf;

    private int segments;

    private String poSeriesID;

    private String poProgType;

    private SortedSet<Platform> platforms = new TreeSet<>();

    private ProgramEditView() {
    }

    static ProgramEditView create(Program fullProgram) {
        ProgramEditView programEditView = new ProgramEditView();

        programEditView.episodeOf = fullProgram.getEpisodeOf().size();
        programEditView.segments = fullProgram.getSegments().size();
        //programEditView.poSeriesID = fullProgram.getPoSeriesID();
        programEditView.poProgType = fullProgram.getPoProgType();

        for (Prediction platform : fullProgram.getPredictions()) {
            programEditView.platforms.add(platform.getPlatform());
        }



        return programEditView;
    }

    public int getEpisodeOf() {
        return episodeOf;
    }

    public int getSegments() {
        return segments;
    }


    public String getPoSeriesID() {
        return poSeriesID;
    }

    public void setPoSeriesID(String poSeriesID) {
        this.poSeriesID = poSeriesID;
    }

    public String getPoProgType() {
        return poProgType;
    }

    public void setPoProgType(String poProgType) {
        this.poProgType = poProgType;
    }


    public SortedSet<Platform> getPlatforms() {
        return platforms;
    }

}
