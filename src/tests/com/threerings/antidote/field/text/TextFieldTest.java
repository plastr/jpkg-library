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
package com.threerings.antidote.field.text;

import org.junit.Test;

import com.threerings.antidote.field.text.EmptyTextFieldViolation;
import com.threerings.antidote.field.text.TextStatus;
import com.threerings.antidote.field.text.UnsetTextFieldViolation;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;

import static org.junit.Assert.assertEquals;

public class TextFieldTest
{
    @Test
    public void testAddText ()
    {
        final TestTextField field = new TestTextField();
        field.addText("Example text");
        assertEquals("Example text", field.getText());
        assertNoViolations(field);
    }

    @Test
    public void testValidateTextWasSet ()
    {
        TestTextField field = new TestTextField();
        field.addText("text");
        assertEquals(TextStatus.VALID_TEXT, field.validateTextWasSet());
        assertNoViolations(field);

        field = new TestTextField();
        assertEquals(TextStatus.INVALID_TEXT, field.validateTextWasSet());
        assertOneViolation(field, UnsetTextFieldViolation.class);
    }

    @Test
    public void testValidateTextNotEmpty ()
    {
        TestTextField field = new TestTextField();
        field.addText("text");
        assertEquals(TextStatus.VALID_TEXT, field.validateTextNotEmpty());
        assertNoViolations(field);

        field = new TestTextField();
        assertEquals(TextStatus.INVALID_TEXT, field.validateTextNotEmpty());
        assertOneViolation(field, UnsetTextFieldViolation.class);

        field = new TestTextField();
        // <textfield> </textfield>
        field.addText(" ");
        assertEquals(TextStatus.INVALID_TEXT, field.validateTextNotEmpty());
        assertOneViolation(field, EmptyTextFieldViolation.class);

        field = new TestTextField();
        field.addText("");
        assertEquals(TextStatus.INVALID_TEXT, field.validateTextNotEmpty());
        assertOneViolation(field, EmptyTextFieldViolation.class);

        // <textfield>
        // </textfield>
        field = new TestTextField();
        field.addText("\n");
        assertEquals(TextStatus.INVALID_TEXT, field.validateTextNotEmpty());
        assertOneViolation(field, EmptyTextFieldViolation.class);
    }

    @Test
    public void testScrubText ()
    {
        final TestTextField field = new TestTextField();
        field.addText("Example\n\t text\n");
        field.scrubTextWhitespace();
        assertEquals("Example text", field.getText());
        assertNoViolations(field);
    }
}
