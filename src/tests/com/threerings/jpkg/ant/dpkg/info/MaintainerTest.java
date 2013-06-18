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
import com.threerings.antidote.field.UnsetFieldViolation;
import com.threerings.antidote.field.text.EmptyTextFieldViolation;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;



public class MaintainerTest extends AntTestHelper
{
    @Test
    public void testMaintainer ()
    {
        // <maintainer>
        //   <name>Maintainer</name>
        //   <email>maintainer@package.com</email>
        // </maintainer>
        final Maintainer maintainer = newMaintainer("Maintainer", "maintainer@package.com");
        assertNoViolations(maintainer);
    }

    @Test
    public void testBadData ()
    {
        // <maintainer>
        //   <name>Maintainer</name>
        //   <email>badEmail@!@package.com</email>
        // </maintainer>
        final Maintainer maintainer = newMaintainer("Maintainer", "badEmail@!@package.com");
        assertOneViolation(maintainer, ControlDataViolation.class);
    }

    @Test
    public void testEmptyData ()
    {
        // <maintainer>
        //   <email>maintainer@package.com</email>
        // </maintainer>
        Maintainer maintainer = newMaintainer(null, "maintainer@package.com");
        assertOneViolation(maintainer, UnsetFieldViolation.class);

        // <maintainer>
        //   <name></name>
        //   <email>maintainer@package.com</email>
        // </maintainer>
        maintainer = newMaintainer("", "maintainer@package.com");
        assertOneViolation(maintainer, EmptyTextFieldViolation.class);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidation ()
    {
        final Maintainer maintainer = newMaintainer("Maintainer", "maintainer@package.com");
        maintainer.getPackageMaintainer();
    }

    // pass null for the string values to not set that field
    private Maintainer newMaintainer (String name, String email)
    {
        final Maintainer maintainer = new Maintainer();
        maintainer.setProject(createProject());

        final MaintainerName maintainerName = new MaintainerName();
        final MaintainerEmail maintainerEmail = new MaintainerEmail();
        maintainerName.setProject(maintainer.getProject());
        maintainerEmail.setProject(maintainer.getProject());

        if (name != null) {
            maintainerName.addText(name);
            maintainer.addName(maintainerName);
        }

        if (email != null) {
            maintainerEmail.addText(email);
            maintainer.addEmail(maintainerEmail);
        }

        return maintainer;
    }
}
