/*
 * Jpkg - Java library and tools for operating system package creation.
 *
 * Copyright (c) 2007-2008 Three Rings Design, Inc.
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
package com.threerings.jpkg.ant.dpkg.scripts.standard;

import java.io.File;

import org.junit.Test;

import com.threerings.antidote.AntTestHelper;
import com.threerings.antidote.RequiresValidationException;
import com.threerings.antidote.field.ConflictingPropertiesViolation;
import com.threerings.antidote.property.InvalidFileViolation;
import com.threerings.jpkg.ant.dpkg.MockDpkgData;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;
import static com.threerings.antidote.ValidationTestHelper.assertTwoViolations;


public class TypeScriptTest extends AntTestHelper
{
    @Test
    public void testGoodData ()
    {
        // <preinst source="/path/to/testfile"/>
        PreInst script = newTypeScript();
        script.setSource(SCRIPT_SOURCE.getAbsolutePath());
        assertNoViolations(script);

        // <preinst command="echo hello"/>
        script = newTypeScript();
        script.setCommand("echo hello");
        assertNoViolations(script);
    }

    @Test
    public void testBadProperties ()
    {
        // <preinst/>
        PreInst script = newTypeScript();
        assertOneViolation(script, UnsetScriptPropertiesViolation.class);

        // <preinst source="/path/to/testfile"/ command="echo hell"/>
        script = newTypeScript();
        script.setSource(SCRIPT_SOURCE.getAbsolutePath());
        script.setCommand("echo hello");
        assertTwoViolations(script, ConflictingPropertiesViolation.class);
    }

    @Test
    public void testMissingSource ()
    {
        // <preinst source="/badpath"/>
        final PreInst script = newTypeScript();
        script.setSource(MISSING_SOURCE.getAbsolutePath());
        assertOneViolation(script, InvalidFileViolation.class);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationSource () throws Exception
    {
        final PreInst script = newTypeScript();
        script.getSource(new MockDpkgData());
    }

    private PreInst newTypeScript ()
    {
        final PreInst script = new PreInst();
        script.setProject(createProject());
        return script;
    }

    private static final File SCRIPT_SOURCE = new File("src/tests/data/testfile.txt");
    private static final File MISSING_SOURCE = new File("/path/does/not/exist");
}
