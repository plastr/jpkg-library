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
import com.threerings.antidote.field.text.EmptyTextFieldViolation;
import com.threerings.antidote.field.text.UnsetTextFieldViolation;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;


public class NameTest extends AntTestHelper
{
    @Test
    public void testGoodData ()
    {
        // <name>packagename/name>
        final Name name = newName();
        name.addText("packagename");
        assertNoViolations(name);
    }

    @Test
    public void testBadData ()
    {
        // <name>badPackageName!</name>
        final Name name = newName();
        name.addText("badPackageName!");
        assertOneViolation(name, ControlDataViolation.class);
    }

    @Test
    public void testEmptyData ()
    {
        // <name/>
        Name name = newName();
        assertOneViolation(name, UnsetTextFieldViolation.class);

        // <name></name>
        name = newName();
        name.addText("");
        assertOneViolation(name, EmptyTextFieldViolation.class);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidation ()
    {
        final Name name = newName();
        name.addText("packagename");
        name.getPackageName();
    }

    private Name newName ()
    {
        final Name name = new Name();
        name.setProject(createProject());
        return name;
    }
}
