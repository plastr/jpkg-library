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
import com.threerings.antidote.field.OnlyOneFieldViolation;
import com.threerings.antidote.field.UnsetDependentFieldViolation;
import com.threerings.antidote.field.UnsetFieldViolation;
import com.threerings.antidote.field.UnsetWrappedFieldException;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;


public class InfoTest extends AntTestHelper
{
    @Test
    public void testGoodInfo ()
    {
        MockInfo info = MockInfo.generateValidInfo();
        info.populateFields();

        assertNoViolations(info);

        info = MockInfo.generateFullValidInfo();
        info.populateFields();

        assertNoViolations(info);
    }

    @Test
    public void tooManyFields ()
    {
        final MockInfo info = MockInfo.generateValidInfo();

        // add an additional <name> field.
        final Name name = new Name();
        name.setProject(info.getProject());
        name.addText("duplicatename");
        info.addName(name);
        info.populateFields();

        assertOneViolation(info, OnlyOneFieldViolation.class);
    }

    @Test
    public void testUnsetFields ()
    {
        final MockInfo info = MockInfo.generateValidInfo();

        // remove the <arch> field
        info.archField = null;
        info.populateFields();

        assertOneViolation(info, UnsetFieldViolation.class);
    }

    @Test
    public void testOptionalFields ()
    {
        MockInfo info = MockInfo.generateFullValidInfo();

        // remove the <priority> field
        info.priorityField = null;
        info.populateFields();

        assertOneViolation(info, UnsetDependentFieldViolation.class);

        info = MockInfo.generateFullValidInfo();

        // remove the <section> field
        info.sectionField = null;
        info.populateFields();

        assertOneViolation(info, UnsetDependentFieldViolation.class);
    }

    @Test
    public void testBadFieldData ()
    {
        final MockInfo info = MockInfo.generateValidInfo();

        // set a bad package name
        info.nameField = new Name();
        info.nameField.setProject(info.getProject());
        info.nameField.addText("badPackName!");
        info.populateFields();

        assertOneViolation(info, ControlDataViolation.class);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationPackageInfo ()
    {
        final MockInfo info = MockInfo.generateValidInfo();
        info.getPackageInfo();
    }

    @Test(expected=UnsetWrappedFieldException.class)
    public void testUnsetWrappedFieldName ()
    {
        final MockInfo info = MockInfo.generateValidInfo();
        info.getPackageNameAsString();
    }

    @Test(expected=UnsetWrappedFieldException.class)
    public void testUnsetWrappedFieldVerson ()
    {
        final MockInfo info = MockInfo.generateValidInfo();
        info.getVersionAsString();
    }
}
