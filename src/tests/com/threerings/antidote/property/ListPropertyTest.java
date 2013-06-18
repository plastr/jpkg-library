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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.threerings.antidote.RequiresValidationException;
import com.threerings.antidote.field.TestBaseField;
import com.threerings.antidote.property.ListProperty;
import com.threerings.antidote.property.UnsetPropertyViolation;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;

import static org.junit.Assert.assertEquals;

public class ListPropertyTest
{
    @Test
    public void testListProperty ()
    {
        ListProperty property = new ListProperty("testprop", new TestBaseField());
        property.setValue(SINGLE_ITEM.get(0));
        assertNoViolations(property);
        assertEquals(SINGLE_ITEM, property.getValue());

        property = new ListProperty("testprop", new TestBaseField());
        property.setValue(MULTIPLE_ITEMS_STRING);
        assertNoViolations(property);
        assertEquals(MULTIPLE_ITEMS, property.getValue());

        property = new ListProperty("testprop", new TestBaseField());
        property.setValue(null);
        assertOneViolation(property, UnsetPropertyViolation.class);
        // accessing getValue() would throw a RequiresValidationException at this point
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationInvalidValue ()
    {
        final ListProperty property = new ListProperty("testprop", new TestBaseField());
        property.setValue("before_validation");
        property.getValue();
    }

    protected static final String EMPTY_LIST = "";
    protected static final List<String> SINGLE_ITEM = new ArrayList<String>();
    protected static final List<String> MULTIPLE_ITEMS = new ArrayList<String>();
    // note the white space between second and third
    protected static final String MULTIPLE_ITEMS_STRING = "first,second, third";

    static {
        SINGLE_ITEM.add("singleitem");
        MULTIPLE_ITEMS.add("first");
        MULTIPLE_ITEMS.add("second");
        MULTIPLE_ITEMS.add("third");
    }
}
