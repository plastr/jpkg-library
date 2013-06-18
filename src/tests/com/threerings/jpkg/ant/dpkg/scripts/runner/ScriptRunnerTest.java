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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.threerings.jpkg.ant.dpkg.Dpkg;
import com.threerings.jpkg.ant.dpkg.DpkgData;
import com.threerings.jpkg.ant.dpkg.MockDpkgData;
import com.threerings.jpkg.ant.dpkg.scripts.SimpleScript;
import com.threerings.jpkg.debian.MaintainerScript;
import com.threerings.jpkg.debian.MaintainerScript.Type;

import static org.junit.Assert.assertTrue;

public class ScriptRunnerTest
{
    @Test
    public void testScriptRunner ()
        throws Exception
    {
        final List<PackageScript> scripts = new ArrayList<PackageScript>();
        scripts.add(new SimpleScript());
        final ScriptRunner runner = new ScriptRunner(MaintainerScript.Type.POSTINST, scripts, new MockDpkgData());
        assertTrue(runner.getSize() > 0);
        // validate the generated script runner contains the uuencoded script source
        assertTrue(IOUtils.toString(runner.getStream(), Dpkg.CHAR_ENCODING).contains(SimpleScript.ENCODED_SCRIPT));
    }

    @Test(expected=UnexpectedScriptTypeException.class)
    public void testUnexpectedScriptType ()
        throws Exception
    {
        final List<PackageScript> scripts = new ArrayList<PackageScript>();
        final SimpleScript simple = new SimpleScript();
        final MockPreinstallScript preinst = new MockPreinstallScript();
        assertTrue(simple.getTypes() != preinst.getTypes());
        scripts.add(simple);
        scripts.add(preinst);
        new ScriptRunner(MaintainerScript.Type.POSTINST, scripts, new MockDpkgData());
    }

    private class MockPreinstallScript
        implements PackageScript
    {
        public String getFriendlyName ()
        {
            return "Mock Preinstall Script";
        }

        public boolean failOnError ()
        {
            return true;
        }

        public Set<Type> getTypes ()
        {
            return Collections.singleton(MaintainerScript.Type.PREINST);
        }

        public InputStream getSource (DpkgData data)
            throws IOException
        {
            return null;
        }
    }
}
