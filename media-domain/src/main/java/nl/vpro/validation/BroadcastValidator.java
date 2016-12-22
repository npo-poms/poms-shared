/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.media.Program;

public class BroadcastValidator implements ConstraintValidator<Broadcast, Program> {

    @Override
    public void initialize(Broadcast broadcast) {
    }

    @Override
    public boolean isValid(Program program, ConstraintValidatorContext constraintValidatorContext) {
        if(program.getType() == null) {
            // Invalid but we delegate the validation for this required field
            return true;
        }

        // DRS: Both BROADCAST and STRAND should be deletable by Poms processes, but not by plain users
        //if(program.getType().equals(ProgramType.BROADCAST) || program.getType().equals(ProgramType.STRAND)) {
        //    return program.getWorkflow() != Workflow.DELETED && program.getWorkflow() != Workflow.FOR_DELETION;
        //}
        return true;
    }
}
