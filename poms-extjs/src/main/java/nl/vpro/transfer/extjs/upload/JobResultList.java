/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.upload;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.domain.encoder.Job;
import nl.vpro.domain.encoder.JobResult;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.util.Helper;

@XmlRootElement(name = "jobs")
public class JobResultList extends TransferList<JobResultView> {

    private static final Logger LOG = LoggerFactory.getLogger(JobResultList.class);


    public JobResultList() {
    }

    public static JobResultList create(List<Job> jobs) {
        LOG.debug("Making a list of {}", jobs);
        JobResultList simpleList = new JobResultList();
        simpleList.success = true;

        if(Helper.isEmpty(jobs)) {
            return simpleList;
        }

        int index = 0;
        for(Job job : jobs) {
            if(job!= null) {
                for(JobResult result : job.getJobResults()) {
                    simpleList.add(JobResultView.create(++index, job, result));
                }
            }
        }

        return simpleList;
    }
}
