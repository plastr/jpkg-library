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
package com.threerings.antidote.property;

import com.threerings.antidote.EnumHelper;
import com.threerings.antidote.field.Field;

import static com.threerings.antidote.MutabilityHelper.objectIsSet;


/**
 * A {@link BaseProperty} that holds an {@link Enum} object.
 */
public class EnumProperty<T extends Enum<T>> extends BaseProperty<T>
{
    // from BaseProperty
    public EnumProperty (String name, Field field, Class<T> enumClass)
    {
        super(name, field);
        _enumClass = enumClass;
    }

    // from BaseProperty
    public EnumProperty (String name, Field field, Class<T> enumClass, T defaultValue)
    {
        super(name, field, defaultValue);
        _enumClass = enumClass;
    }

    @Override // from BaseProperty
    protected T validateProperty ()
    {
        final T enumType = EnumHelper.parseEnum(getRawValue(), _enumClass);

        if (objectIsSet(enumType)) {
            return enumType;

        } else {
            final StringBuilder builder = new StringBuilder();
            for (final Enum<? extends Enum<?>> type : _enumClass.getEnumConstants()) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(type.name().toLowerCase(EnumHelper.LOCALE));
            }

            appendViolation(new InvalidEnumViolation(this, builder.toString()));
            return null;
        }
    }

    /** The Enum Class for the enum held in this Property. */
    private final Class<T> _enumClass;
}
