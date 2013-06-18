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
package com.threerings.antidote.field;

import org.apache.tools.ant.Location;
import org.apache.tools.ant.types.Reference;
import org.junit.Test;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseFieldWrapperTest
{
    @Test
    public void testWrapConstructor ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseField field = new TestBaseField();
        final TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper(field, parent);
        assertNoViolations(wrapper);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWrapConstructorNullField ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseField field = null;
        new TestBaseFieldWrapper(field, parent);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWrapConstructorNullParent ()
    {
        final TestBaseField parent = null;
        final TestBaseField field = new TestBaseField();
        new TestBaseFieldWrapper(field, parent);
    }

    @Test
    public void testClassConstructor ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper(TestBaseField.class, parent);
        assertNoViolations(wrapper);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testClassConstructorNullParent ()
    {
        final TestBaseField parent = null;
        new TestBaseFieldWrapper(TestBaseField.class, parent);
    }

    @Test
    public void testAbstractConstructor ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper("testabstractfield", parent);
        assertNoViolations(wrapper);
        assertEquals(wrapper.getFieldName(), "testabstractfield");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAbstractConstructorNullParent ()
    {
        final TestBaseField parent = null;
        new TestBaseFieldWrapper("testbasefield", parent);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAbstractConstructorNullName ()
    {
        final TestBaseField parent = new TestBaseField();
        final String name = null;
        new TestBaseFieldWrapper(name, parent);
    }

    @Test
    public void testAbstractFieldNameChange ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseField field = new TestBaseField();
        final TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper("testabstractfield", parent);
        assertNoViolations(wrapper);
        assertEquals("testabstractfield", wrapper.getFieldName());
        wrapper.setField(field);
        assertEquals(TestBaseField.FIELD_NAME, wrapper.getFieldName());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetNullField ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseField field = null;
        final TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper(TestBaseField.class, parent);
        wrapper.setField(field);
    }

    @Test
    public void testOnlyOneFieldViolation ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseField field = new TestBaseField();
        TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper(field, parent);
        wrapper.setField(field);
        assertOneViolation(wrapper, OnlyOneFieldViolation.class);

        wrapper = new TestBaseFieldWrapper(TestBaseField.class, parent);
        wrapper.setField(field);
        assertNoViolations(wrapper);
        wrapper.setField(field);
        assertOneViolation(wrapper, OnlyOneFieldViolation.class);
    }

    @Test(expected=UnsetWrappedFieldException.class)
    public void testUnsetWrappedFieldException ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper(TestBaseField.class, parent);
        wrapper.getField();
    }

    @Test
    public void testGetField ()
    {
        final TestBaseField parent = new TestBaseField();
        TestBaseField field = new TestBaseField();
        TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper(field, parent);
        assertEquals(field.getFieldName(), wrapper.getField().getFieldName());

        field = new TestBaseField();
        final OtherNamedField refField = new OtherNamedField();
        field.getProject().addReference("test.ref", refField);
        final Reference ref = new Reference(field.getProject(), "test.ref");
        field.setRefid(ref);
        wrapper = new TestBaseFieldWrapper(field, parent);
        assertEquals(refField.getFieldName(), wrapper.getField().getFieldName());
    }

    @Test
    public void testIsSet ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseField field = new TestBaseField();
        TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper(field, parent);
        assertTrue(wrapper.isSet());
        assertFalse(wrapper.isNotSet());

        wrapper = new TestBaseFieldWrapper(TestBaseField.class, parent);
        assertFalse(wrapper.isSet());
        assertTrue(wrapper.isNotSet());
    }

    @Test
    public void testGetLocation ()
    {
        final TestBaseField parent = new TestBaseField();
        parent.setLocation(PARENT_LOCATION);
        final TestBaseField field = new TestBaseField();
        field.setLocation(FIELD_LOCATION);
        TestBaseFieldWrapper wrapper = new TestBaseFieldWrapper(field, parent);
        assertEquals(FIELD_LOCATION.getFileName(), wrapper.getLocation().getFileName());

        wrapper = new TestBaseFieldWrapper(TestBaseField.class, parent);
        assertEquals(PARENT_LOCATION.getFileName(), wrapper.getLocation().getFileName());
    }

    private static final Location FIELD_LOCATION = new Location("filename");
    private static final Location PARENT_LOCATION = new Location("parentfilename");

    private static class OtherNamedField extends TestBaseField
    {
        public static final String OTHER_FIELD_NAME = "otherfieldname";

        @Override
        public String getFieldName ()
        {
            return OTHER_FIELD_NAME;
        }
    }
}
