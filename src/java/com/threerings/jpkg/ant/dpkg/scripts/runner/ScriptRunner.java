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
package com.threerings.jpkg.ant.dpkg.scripts.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;

import com.threerings.jpkg.ant.VelocityHelper;
import com.threerings.jpkg.ant.VelocityTemplate;
import com.threerings.jpkg.ant.dpkg.DpkgData;
import com.threerings.jpkg.debian.MaintainerScript;

/**
 * Handles compiling {@link PackageScript} maintainer scripts into a single master script which
 * executes each individual script in turn.
 */
public class ScriptRunner
    implements MaintainerScript, VelocityTemplate
{
    /**
     * Construct a new ScriptRunner for the associated {@link Type}, using the list of
     * {@link PackageScript} scripts. The class will verify all supplied scripts
     * match the type given.
     * @param type The {@link Type} of all scripts being encoded.
     * @param scripts The list of {@link PackageScript} scripts to encode.
     * @param data The {@link DpkgData} to pass to each script being encoded.
     * @throws IOException If any i/o errors occur.
     * @throws UnexpectedScriptTypeException If any script type in the supplied list does not match the type
     * provided as the first parameter to the constructor.
     */
    public ScriptRunner (MaintainerScript.Type type, List<PackageScript> scripts, DpkgData data)
        throws IOException, UnexpectedScriptTypeException
    {
        _type = type;
        _file = File.createTempFile("script_runner_" + type.getFilename(), ".tmp");
        _file.deleteOnExit();

        for (final PackageScript source : scripts) {
            if (!source.getTypes().contains(type)) {
                throw new UnexpectedScriptTypeException(type);
            }

            _encodedScripts.add(new EncodedScript(source, data));
        }

        final FileWriter writer = new FileWriter(_file);
        try {
            VelocityHelper.mergeTemplate(this, writer);

        } catch (final Exception e) {
            throw new IOException(e.getMessage());

        } finally {
            writer.close();
        }
    }

    // from MaintainerScript
    public Type getType ()
    {
        return _type;
    }

    // from MaintainerScript
    public InputStream getStream ()
        throws IOException
    {
        return new FileInputStream(_file);
    }

    // from MaintainerScript
    public long getSize ()
    {
        return _file.length();
    }

    // from VelocityTemplate
    public String getTemplateName ()
    {
        return "script_runner.vm";
    }

    // from VelocityTemplate
    public void populateContext (VelocityContext context)
    {
        context.put("script_type", getType().getFilename());
        context.put("scripts", _encodedScripts);
    }

    /** The type of maintainer script. */
    private final MaintainerScript.Type _type;

    /** The file backing store for the script being created. */
    private final File _file;

    /** The list of uuencoded scripts. */
    private final List<EncodedScript> _encodedScripts = new ArrayList<EncodedScript>();
}
