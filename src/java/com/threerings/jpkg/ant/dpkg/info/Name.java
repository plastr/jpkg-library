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

import com.threerings.antidote.field.text.SingleLineTextField;
import com.threerings.jpkg.debian.ControlDataInvalidException;
import com.threerings.jpkg.debian.PackageName;

import static com.threerings.antidote.MutabilityHelper.requiresValidation;


/**
 * Stores the &lt;info&gt; &lt;name&gt; field, which is the package name.
 * @see PackageName
 */
public class Name extends SingleLineTextField
{
    // from Field
    public String getFieldName ()
    {
        return "name";
    }

    /**
     * Returns the user data converted into a {@link PackageName}. Cannot be called before validate().
     */
    public PackageName getPackageName ()
    {
        requiresValidation(_packageName);
        return _packageName;
    }

    @Override // from SingleLineTextField
    protected void validateTextField ()
    {
        try {
            _packageName = new PackageName(getText());

        } catch (final ControlDataInvalidException cdie) {
            appendViolation(new ControlDataViolation(this, cdie));
        }
    }

    /** The PackageName object representing the user supplied data. */
    private PackageName _packageName;
}
