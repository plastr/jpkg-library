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

import org.apache.tools.ant.Project;
import org.junit.Test;

import com.threerings.antidote.AntTestHelper;
import com.threerings.antidote.RequiresValidationException;
import com.threerings.antidote.field.UnsetFieldViolation;
import com.threerings.antidote.field.text.EmptyTextFieldViolation;
import com.threerings.antidote.field.text.UnsetTextFieldViolation;
import com.threerings.jpkg.ant.dpkg.info.description.Blank;
import com.threerings.jpkg.ant.dpkg.info.description.Paragraph;
import com.threerings.jpkg.ant.dpkg.info.description.Summary;
import com.threerings.jpkg.ant.dpkg.info.description.Verbatim;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;


public class DescriptionTest extends AntTestHelper
{
    @Test
    public void testSimpleDescription ()
    {
        // <description>Simple description</description>
        final Description description = newDescription();
        description.addText("Simple description");
        assertNoViolations(description);
    }

    @Test
    public void testDetailedDescription ()
    {
        // <description>
        //   <summary>Package summary</summary>
        //   <blank/>
        //   <paragraph>Paragraph Text</paragraph>
        //   <verbatim>Verbatim Text</verbatim>
        // </description>
        final Description description = newDescription();
        final Summary summary = new Summary();
        final Blank blank = new Blank();
        final Paragraph paragraph = new Paragraph();
        final Verbatim verbatim = new Verbatim();

        final Project project = description.getProject();
        summary.setProject(project);
        blank.setProject(project);
        paragraph.setProject(project);
        verbatim.setProject(project);

        summary.addText("Package summary");
        paragraph.addText("Paragraph Text");
        verbatim.addText("Verbatim Text");

        description.addSummary(summary);
        description.addBlank(blank);
        description.addParagraph(paragraph);
        description.addVerbatim(verbatim);

        assertNoViolations(description);
    }

    @Test
    public void testMissingSummary ()
    {
        // <description>
        //   <blank/>
        // </description>
        final Description description = newDescription();
        final Blank blank = new Blank();
        blank.setProject(description.getProject());
        description.addBlank(blank);

        assertOneViolation(description, UnsetFieldViolation.class);
    }

    @Test
    public void testEmptyData ()
    {
        // <description/>
        Description description = newDescription();
        assertOneViolation(description, UnsetTextFieldViolation.class);

        // <description></description>
        description = newDescription();
        description.addText("");
        assertOneViolation(description, EmptyTextFieldViolation.class);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidation ()
    {
        final Description description = newDescription();
        description.addText("Simple description");
        description.getPackageDescription();
    }

    private Description newDescription ()
    {
        final Description description = new Description();
        description.setProject(createProject());
        return description;
    }
}
