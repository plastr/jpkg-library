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
package com.threerings.antidote.field.text;

import static com.threerings.antidote.MutabilityHelper.requiresValidation;

/**
 * A {@link TextField} that enforces the text data is always set, not zero length, and has
 * no newlines or tabs.
 */
public abstract class SingleLineTextField extends TextField
{
    @Override // from BaseComponent
    protected final void validateField ()
    {
        // require the text was set, and was not empty.
        if (validateTextNotEmpty() == TextStatus.INVALID_TEXT) return;
        // scrub any newlines or tabs from the field.
        scrubTextWhitespace();

        _validatedText = super.getText();
        validateTextField();
    }

    /**
     * Override getText() so that the initial steps of validateField() must have happened before
     * access.
     */
    @Override // from TextField
    protected String getText()
    {
        requiresValidation(_validatedText);
        return _validatedText;
    }

    /**
     * Provide concrete classes a method for additional validation.
     */
    protected abstract void validateTextField ();

    /** The text after being validated. */
    private String _validatedText;
}
