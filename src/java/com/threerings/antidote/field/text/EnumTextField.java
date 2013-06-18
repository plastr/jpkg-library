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

import com.threerings.antidote.property.EnumProperty;

import static com.threerings.antidote.MutabilityHelper.requiresValidation;


public abstract class EnumTextField<T extends Enum<T>> extends SingleLineTextField
{
    public EnumTextField (Class<T> enumClass)
    {
        _enumClass = enumClass;
    }

    /**
     * Returns the user data converted into an enum. Cannot be called before validate().
     */
    public T getEnum ()
    {
        requiresValidation(_enum);
        return _enum;
    }

    @Override // from SingleLineTextField
    protected final void validateTextField ()
    {
        // store the text data in an enum property for validation.
        // TODO: the InvalidEnumViolation mentions properties. Revisit this.
        final EnumProperty<T> property = new EnumProperty<T>("text field", this, _enumClass);
        property.setValue(getText());

        switch (validateProperties(property)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                _enum = property.getValue();
                return;
        }
    }

    /** The enum after it has been validated. */
    private T _enum;

    /** The class for the enum expected in this text field. */
    private final Class<T> _enumClass;
}
