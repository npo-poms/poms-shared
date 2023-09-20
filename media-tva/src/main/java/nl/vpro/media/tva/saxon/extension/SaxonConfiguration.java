/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.media.tva.saxon.extension;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.ExtensionFunctionDefinition;

import java.util.Collection;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class SaxonConfiguration extends Configuration {

    public static final String VPRO_URN = "urn:vpro:saxon";

    public void setExtensions(Collection<ExtensionFunctionDefinition> extensions) {
        for(ExtensionFunctionDefinition extension : extensions) {
            super.registerExtensionFunction(extension);
        }
    }
}
