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
package com.threerings.jpkg.ant.dpkg.dependencies;

import org.junit.Test;

import com.threerings.antidote.AntTestHelper;
import com.threerings.antidote.RequiresValidationException;
import com.threerings.antidote.field.AtLeastOneFieldViolation;
import com.threerings.antidote.property.UnsetPropertyViolation;
import com.threerings.jpkg.ant.dpkg.info.MockInfo;
import com.threerings.jpkg.debian.PackageInfo;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AlternativesTest extends AntTestHelper
{
    @Test
    public void testGoodDependencies ()
    {
        final Alternatives alternatives = mockAlternatives();
        assertNoViolations(alternatives);
    }

    @Test
    public void testAddToPackgeInfo ()
    {
        final Alternatives alternatives = mockAlternatives();
        assertNoViolations(alternatives);

        final MockInfo mockInfo = MockInfo.generateValidInfo();
        mockInfo.populateFields();
        mockInfo.validate();
        final PackageInfo info = mockInfo.getPackageInfo();
        assertFalse(info.toString().contains("Depends"));
        alternatives.addToPackageInfo(info);
        assertTrue(info.toString().contains("Depends"));
    }

    @Test
    public void testMissingBadField ()
    {
        //    <alternatives>
        //      <require/>
        //    </alternatives>
        final Alternatives alternatives = newAlternatives();
        final Require require = new Require();
        require.setProject(alternatives.getProject());
        alternatives.add(require);
        assertOneViolation(alternatives, UnsetPropertyViolation.class);
    }

    @Test
    public void testMissingRequireField ()
    {
        //    <alternatives>
        //    </alternatives>
        final Alternatives alternatives = newAlternatives();
        assertOneViolation(alternatives, AtLeastOneFieldViolation.class);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationAddToPackageInfo ()
    {
        final MockInfo mockInfo = MockInfo.generateValidInfo();
        mockInfo.populateFields();
        mockInfo.validate();
        final PackageInfo info = mockInfo.getPackageInfo();

        final Alternatives alternatives = mockAlternatives();
        alternatives.addToPackageInfo(info);
    }

    private Alternatives mockAlternatives ()
    {
        //    <alternatives>
        //      <require package="option1"/>
        //      <require package="option2"/>
        //    </alternatives>
        final Alternatives alternatives = newAlternatives();
        final Require option1 = new Require();
        option1.setProject(alternatives.getProject());
        option1.setPackage("option1");
        final Require option2 = new Require();
        option2.setProject(alternatives.getProject());
        option2.setPackage("option1");

        alternatives.add(option1);
        alternatives.add(option2);
        return alternatives;
    }

    private Alternatives newAlternatives ()
    {
        final Alternatives alternatives = new Alternatives();
        alternatives.setProject(createProject());
        return alternatives;
    }
}
