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
package com.threerings.antidote;

import org.junit.Test;

import com.threerings.antidote.field.TestBaseField;
import com.threerings.antidote.property.SetProperty;
import com.threerings.antidote.property.UnsetProperty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MutabilityHelperTest
{
    @Test
    public void testObjectIsSet ()
    {
        assertTrue(MutabilityHelper.objectIsSet(SET_FIELD));
        assertFalse(MutabilityHelper.objectIsSet(UNSET_FIELD));
    }

    @Test
    public void testObjectIsNotSet ()
    {
        assertTrue(MutabilityHelper.objectIsNotSet(UNSET_FIELD));
        assertFalse(MutabilityHelper.objectIsNotSet(SET_FIELD));
    }

    @Test
    public void testAreMutablesSet ()
    {
        assertEquals(SetStatus.ALL_SET, MutabilityHelper.areMutablesSet(SET_PROPERTY,SET_PROPERTY));
        assertEquals(SetStatus.SOME_UNSET, MutabilityHelper.areMutablesSet(SET_PROPERTY, UNSET_PROPERTY));
        assertEquals(SetStatus.ALL_UNSET, MutabilityHelper.areMutablesSet(UNSET_PROPERTY, UNSET_PROPERTY));
    }

    @Test
    public void testRequiresValidationWithData ()
    {
        MutabilityHelper.requiresValidation(SET_FIELD);
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationMissingData ()
    {
        MutabilityHelper.requiresValidation(UNSET_FIELD);
    }

    private static final TestBaseField SET_FIELD = new TestBaseField();
    private static final TestBaseField UNSET_FIELD = null;

    private static final SetProperty SET_PROPERTY = new SetProperty(new TestBaseField());
    private static final UnsetProperty UNSET_PROPERTY = new UnsetProperty(new TestBaseField());
}
