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

import java.util.List;

import com.threerings.antidote.field.Field;

public class NonEmptyListProperty extends ListProperty
{
    // from BaseProperty
    public NonEmptyListProperty (String name, Field field)
    {
        super(name, field);
    }

    // from BaseProperty
    public NonEmptyListProperty (String name, Field field, List<String> defaultValue)
    {
        super(name, field, defaultValue);
    }

    @Override // from ListProperty
    protected List<String> validateProperty ()
    {
        final List<String> list = super.validateProperty();
        if (wasSetBlank(list)) {
            appendViolation(new EmptyListPropertyViolation(this));
            return null;
        }
        return list;
    }

    /**
     * Returns true if the list was set by the user, but contains only the empty string.
     */
    private boolean wasSetBlank (List<String> list)
    {
        // if the user did not set a value, return false.
        if (isNotSet()) {
            return false;
        }

        // if the list contains only one element and it is the empty string, return true.
        if (list.size() == 1 && list.get(0).trim().equals("")) {
            return true;
        }

        return false;
    }
}
