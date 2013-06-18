/*
 * Jpkg - Java library and tools for operating system package creation.
 *
 * Copyright (c) 2007 Three Rings Design, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright owner nor the names of contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.threerings.jpkg.ant;

import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * A helper class to generate silent velocity engines and work with {@link VelocityTemplate} classes.
 */
public class VelocityHelper
{
    /**
     * A null logger for Velocity.
     */
    private static final LogChute _nullLogger = new LogChute() {
        public void init (RuntimeServices rs) throws Exception {}
        public void log (int level, String message) {}
        public void log (int level, String message, Throwable t) {}
        public boolean isLevelEnabled (int level)
        {
            return false;
        }
    };

    /** The quieted engine. */
    private static final VelocityEngine _engine;

    /**
     * Initialize the static engine to be quiet.
     */
    static
    {
        _engine = new VelocityEngine();
        _engine.setProperty(RuntimeConstants.VM_LIBRARY, "");
        _engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        _engine.setProperty("classpath." + RuntimeConstants.RESOURCE_LOADER + ".class",
            ClasspathResourceLoader.class.getName());
        _engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, _nullLogger);

        try {
            _engine.init();
        } catch (final Exception e) {
            throw new RuntimeException("Failed to initialize Velocity engine", e);
        }
    }

    /**
     * Merge the supplied {@link VelocityTemplate}, performing all substitutions defined, into
     * the supplied {@link Writer}.
     * @param template The {@link VelocityTemplate} to merge.
     * @param writer The {@link Writer} to write the merged template to.
     * @throws MethodInvocationException If the merge fails.
     * @throws ParseErrorException If the template is invalid.
     * @throws ResourceNotFoundException If the template could not be found in the classpath.
     * @throws Exception If any other failure occurs constructing the merging the template.
     */
    public static void mergeTemplate (VelocityTemplate template, Writer writer)
        throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception
    {
        final VelocityContext context = new VelocityContext();
        template.populateContext(context);

        _engine.getTemplate(template.getTemplateName()).merge(context, writer);
    }
}
