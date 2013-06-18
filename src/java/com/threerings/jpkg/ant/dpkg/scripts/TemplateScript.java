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
package com.threerings.jpkg.ant.dpkg.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.velocity.VelocityContext;

import com.threerings.jpkg.ant.dpkg.DpkgData;
import com.threerings.jpkg.debian.MaintainerScript;
import com.threerings.jpkg.debian.MaintainerScript.Type;

/**
 * A simple implementation of {@link BaseTemplateScript} which provides helpers to populate the
 * velocity context and inserts the {@link DpkgData} object into the context by default.
 */
public abstract class TemplateScript extends BaseTemplateScript
{
    /**
     * Construct a {@link TemplateScript} with the single script type implemented.
     */
    public TemplateScript (MaintainerScript.Type type)
    {
        _types = Collections.singleton(type);
    }

    /**
     * Construct a {@link TemplateScript} with a set of the script types implemented.
     * @see #typeList(MaintainerScript.Type...)
     */
    public TemplateScript (Set<MaintainerScript.Type> types)
    {
        _types = types;
    }

    /**
     * A static helper to pass a list of types to the constructor.
     */
    public static Set<MaintainerScript.Type> typeList (MaintainerScript.Type...types)
    {
        return Collections.unmodifiableSet(new TreeSet<MaintainerScript.Type>(Arrays.asList(types)));
    }

    // from PackageScript
    public Set<Type> getTypes ()
    {
        return _types;
    }

    // from PackageScript
    public InputStream getSource (DpkgData data)
        throws IOException
    {
        addSubstitution(DPKG_DATA_KEY, data);
        return mergeTemplate();
    }

    // from VelocityTemplate
    public void populateContext (VelocityContext context)
    {
        for (final Entry<String, Object> entry : _substitutions.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Add a Velocity template substitution, converting the supplied key into the supplied value.
     */
    protected void addSubstitution (String key, Object value)
    {
        _substitutions.put(key, value);
    }

    /**
     * The default key name to use when adding the {@link DpkgData} object to the context.
     */
    protected static final String DPKG_DATA_KEY = "dpkg";

    /**
     * The map of template substitutions.
     */
    private final Map<String, Object> _substitutions = new HashMap<String, Object>();

    /**
     * The set of maintainer script types this script implements.
     */
    private final Set<MaintainerScript.Type> _types;
}
