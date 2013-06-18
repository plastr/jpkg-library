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
import com.threerings.antidote.field.OnlyOneFieldViolation;
import com.threerings.antidote.field.text.UnsetTextFieldViolation;
import com.threerings.antidote.property.UnsetPropertyViolation;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.Condition;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.EqualOrGreaterThan;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.EqualTo;
import com.threerings.jpkg.ant.dpkg.info.MockInfo;
import com.threerings.jpkg.debian.PackageInfo;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseDependencyTest extends AntTestHelper
{
    @Test
    public void testGoodDependency ()
    {
        // <require package="packagename"/>
        Require require = newRequire();
        require.setPackage("packagename");
        assertNoViolations(require);
        assertEquals("packagename", require.getDependency().asString());

        // <require package="packagename">
        //   <equalorgreaterthan>3.4a</equalorgreaterthan>
        // </require>
        require = newRequire();
        final Condition condition = new EqualOrGreaterThan();
        condition.setProject(require.getProject());
        condition.addText("3.4a");
        require.setPackage("packagename");
        require.add(condition);
        assertNoViolations(require);
        assertEquals("packagename (>= 3.4a)", require.getDependency().asString());
    }

    @Test
    public void testMissingPackageName ()
    {
        // <require/>
        final Require require = newRequire();
        assertOneViolation(require, UnsetPropertyViolation.class);
    }

    @Test
    public void testMissingVersion ()
    {
        // <require package="packagename">
        //   <equalorgreaterthan</equalorgreaterthan>
        // </require>
        final Require require = newRequire();
        final Condition condition = new EqualOrGreaterThan();
        condition.setProject(require.getProject());
        require.setPackage("packagename");
        require.add(condition);
        assertOneViolation(require, UnsetTextFieldViolation.class);
    }

    @Test
    public void testTooManyConditions ()
    {
        // <require package="packagename">
        //   <equalorgreaterthan>3.4a</equalorgreaterthan>
        //   <equalTo>1.5a</equalTo>
        // </require>
        final Require require = newRequire();
        final Condition equalOrGreater = new EqualOrGreaterThan();
        equalOrGreater.setProject(require.getProject());
        equalOrGreater.addText("3.4a");
        final Condition equalTo = new EqualTo();
        equalTo.setProject(require.getProject());
        equalTo.addText("1.5a");

        require.setPackage("packagename");
        require.add(equalOrGreater);
        require.add(equalTo);

        assertOneViolation(require, OnlyOneFieldViolation.class);
    }

    @Test
    public void testAddToPackgeInfo ()
    {
        // <require package="packagename"/>
        final Require require = newRequire();
        require.setPackage("packagename");
        assertNoViolations(require);

        final MockInfo mockInfo = MockInfo.generateValidInfo();
        mockInfo.populateFields();
        mockInfo.validate();
        final PackageInfo info = mockInfo.getPackageInfo();
        assertFalse(info.toString().contains("Depends"));
        require.addToPackageInfo(info);
        assertTrue(info.toString().contains("Depends"));
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationDependency ()
    {
        final Require require = newRequire();
        require.getDependency();
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationAddToPackageInfo ()
    {
        final MockInfo mockInfo = MockInfo.generateValidInfo();
        mockInfo.populateFields();
        mockInfo.validate();
        final PackageInfo info = mockInfo.getPackageInfo();

        final Require require = newRequire();
        require.addToPackageInfo(info);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationCondition ()
    {
        final Condition equalOrGreater = new EqualOrGreaterThan();
        equalOrGreater.getVersion();
    }

    private Require newRequire ()
    {
        final Require require = new Require();
        require.setProject(createProject());
        return require;
    }
}
