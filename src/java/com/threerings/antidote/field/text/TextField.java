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

import com.threerings.antidote.field.BaseField;
import com.threerings.antidote.field.Field;

import static com.threerings.antidote.MutabilityHelper.objectIsNotSet;

/**
 * An Ant {@link Field} which allows text input in the form &lt;field&gt;Text here&lt;/field&gt;.
 */
public abstract class TextField extends BaseField
{
    /**
     * Ant adder field: grabs the text between any two &lt;field&gt;&lt;/field&gt; elements.
     */
    public void addText (String text)
    {
        // perform all Ant property substitutions, e.g. ${field} -> value
        _text = getProject().replaceProperties(text);
    }

    /**
     * Returns the text supplied to the field.
     */
    protected String getText ()
    {
        return _text;
    }

    /**
     * Provide concrete classes a method to validate that the text field was set.
     * A violation will be appended to the field if the text is not set.
     * @return the {@link TextStatus} status of the text.
     */
    protected TextStatus validateTextWasSet ()
    {
        if (objectIsNotSet(_text)) {
            appendViolation(new UnsetTextFieldViolation(this));
            return TextStatus.INVALID_TEXT;
        }
        return TextStatus.VALID_TEXT;
    }

    /**
     * Provide concrete classes a method to validate that the text field was set and not blank.
     * The text data will first have any newline or tab characters removed before it is checked
     * e.g. a field filled with only tabs and newlines will be considered empty.
     * A violation will be appended to the field if the text is not set and blank.
     * @return the {@link TextStatus} status of the text.
     */
    protected TextStatus validateTextNotEmpty ()
    {
        if (validateTextWasSet() == TextStatus.INVALID_TEXT) {
            return TextStatus.INVALID_TEXT;
        }
        // scrub new lines etc. and then trim meaning if the string is only empty chracters
        // it will now have a 0 length.
        final String scrubbed = scrubString(_text).trim();

        if (scrubbed.length() <= 0) {
            appendViolation(new EmptyTextFieldViolation(this));
            return TextStatus.INVALID_TEXT;
        }
        return TextStatus.VALID_TEXT;
    }

    /**
     * Provide concrete classes a convenience method to scrub whitespace from the text field.
     */
    protected void scrubTextWhitespace ()
    {
        _text = scrubString(_text);
    }

    /**
     * Scrub the supplied String of all characters which may have been placed into the build file
     * for the text field which is assumed to be just a single line.
     */
    private String scrubString (String text)
    {
        return text.replaceAll("[\t\n\r\f]", "");
    }

    private String _text;
}
