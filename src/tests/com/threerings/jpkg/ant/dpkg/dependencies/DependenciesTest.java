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
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.Condition;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.EqualTo;

import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;


public class DependenciesTest extends AntTestHelper
{
    @Test
    public void testGoodDependencies ()
    {
        //  <dependencies>
        //    <require package="packagename">
        //      <equalTo>1.4</equalTo>
        //    </require>
        //    <alternatives>
        //      <require package="option1"/>
        //      <require package="option2"/>
        //    </alternatives>
        //  </dependencies>
        final Dependencies dependencies = newDependencies();

        final Condition condition = new EqualTo();
        condition.setProject(dependencies.getProject());
        condition.addText("1.4");
        final Require require = new Require();
        require.setPackage("packagename");
        require.add(condition);

        final Alternatives alternatives = new Alternatives();
        final Require option1 = new Require();
        option1.setPackage("option1");
        alternatives.add(option1);
        final Require option2 = new Require();
        option2.setPackage("option2");
        alternatives.add(option2);

        dependencies.add(require);
        dependencies.addAlternatives(alternatives);
    }

    @Test
    public void testBadDependencies ()
    {
        //  <dependencies>
        //    <require/>
        //  </dependencies>
        final Dependencies dependencies = newDependencies();
        final Require require = new Require();
        dependencies.add(require);

        assertOneViolation(dependencies, UnsetPropertyViolation.class);
    }

    @Test
    public void testMissingDependency ()
    {
        //  <dependencies>
        //  </dependencies>
        final Dependencies dependencies = newDependencies();

        assertOneViolation(dependencies, AtLeastOneFieldViolation.class);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidation ()
    {
        final Dependencies dependencies = newDependencies();
        dependencies.getDependencies();
    }

    private Dependencies newDependencies ()
    {
        final Dependencies dependencies = new Dependencies();
        dependencies.setProject(createProject());
        return dependencies;
    }
}
