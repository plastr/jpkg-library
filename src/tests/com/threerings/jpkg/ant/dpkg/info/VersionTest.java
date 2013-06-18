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
package com.threerings.jpkg.ant.dpkg.info;

import org.junit.Test;

import com.threerings.antidote.AntTestHelper;
import com.threerings.antidote.RequiresValidationException;
import com.threerings.antidote.field.UnsetDependentPropertyViolation;
import com.threerings.antidote.field.text.EmptyTextFieldViolation;
import com.threerings.antidote.field.text.UnsetTextFieldViolation;
import com.threerings.antidote.property.InvalidIntegerViolation;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;


public class VersionTest extends AntTestHelper
{
    @Test
    public void testGoodData ()
    {
        // <version>1.2</version>
        Version version = newVersion();
        version.addText("1.2");
        assertNoViolations(version);

        // <version epoch="1" debianVersion="3a">1.2</version>
        version = newVersion();
        version.addText("1.2");
        version.setEpoch("1");
        version.setDebianVersion("3a");
        assertNoViolations(version);
    }

    @Test
    public void testEmptyData ()
    {
        // <version/>
        Version version = newVersion();
        assertOneViolation(version, UnsetTextFieldViolation.class);

        // <version></version>
        version = newVersion();
        version.addText("");
        assertOneViolation(version, EmptyTextFieldViolation.class);
    }

    @Test
    public void testOptionalFields ()
    {
        // <version epoch="1">1.2</version>
        Version version = newVersion();
        version.addText("1.2");
        version.setEpoch("1");
        assertOneViolation(version, UnsetDependentPropertyViolation.class);

        // <version debianVersion="3a">1.2</version>
        version = newVersion();
        version.addText("1.2");
        version.setDebianVersion("3a");
        assertOneViolation(version, UnsetDependentPropertyViolation.class);
    }

    @Test
    public void testNotAnIntegerEpoch ()
    {
        // <version epoch="1a" debianVersion="3a">1.2</version>
        final Version version = newVersion();
        version.addText("1.2");
        version.setEpoch("1a");
        version.setDebianVersion("3a");
        assertOneViolation(version, InvalidIntegerViolation.class);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidation ()
    {
        final Version version = newVersion();
        version.addText("1.2");
        version.getPackageVersion();
    }

    private Version newVersion ()
    {
        final Version version = new Version();
        version.setProject(createProject());
        return version;
    }
}
