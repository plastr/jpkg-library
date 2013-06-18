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
package com.threerings.antidote.field;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.junit.Test;

import com.threerings.antidote.AntTestHelper;
import com.threerings.antidote.TestViolation;
import com.threerings.antidote.ValidStatus;
import com.threerings.antidote.property.OneViolationProperty;
import com.threerings.antidote.property.SetProperty;
import com.threerings.antidote.property.TestProperty;
import com.threerings.antidote.property.UnsetProperty;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BaseFieldTest extends AntTestHelper
{
    @Test
    public void testReference ()
    {
        final TestBaseField refField = new TestBaseField();
        final TestBaseField field = new TestBaseField();
        assertFalse(field.isReference());

        field.getProject().addReference("test.ref", refField);
        final Reference ref = new Reference(field.getProject(), "test.ref");
        field.setRefid(ref);
        assertTrue(field.isReference());
        assertEquals(refField, field.getReferencedField());
    }

    @Test(expected=UnsetReferenceException.class)
    public void testReferenceUnset ()
    {
        final TestBaseField field = new TestBaseField();
        assertFalse(field.isReference());
        field.getReferencedField();
    }

    @Test
    public void testValidateChildFields ()
    {
        final TestBaseField parentField = new TestBaseField();

        final TestBaseField noViolations = new TestBaseField();
        final OneViolationField oneViolation = new OneViolationField();

        final RequiredField<TestBaseField> noViolationsWrap = new RequiredField<TestBaseField>(noViolations, parentField);
        final RequiredField<OneViolationField> oneViolationWrap = new RequiredField<OneViolationField>(oneViolation, parentField);

        assertEquals(ValidStatus.ALL_VALID, parentField.validateChildFields(noViolationsWrap, noViolationsWrap));
        assertEquals(ValidStatus.SOME_INVALID, parentField.validateChildFields(oneViolationWrap, noViolationsWrap));
        assertEquals(ValidStatus.ALL_INVALID, parentField.validateChildFields(oneViolationWrap, oneViolationWrap));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValidateNullChildField ()
    {
        final TestBaseField parentField = new TestBaseField();

        final FieldWrapper<?>[] fields = {null};
        parentField.validateChildFields(fields);
    }

    @Test
    public void testValidateProperties ()
    {
        TestBaseField field = new TestBaseField();

        final TestProperty noViolations = new TestProperty(field);
        noViolations.setValue("testvalue");
        final OneViolationProperty oneViolation = new OneViolationProperty(field);
        oneViolation.setValue("testvalue");

        assertEquals(ValidStatus.ALL_VALID, field.validateProperties(noViolations, noViolations));
        assertNoViolations(field);

        field = new TestBaseField();
        assertEquals(ValidStatus.SOME_INVALID, field.validateProperties(oneViolation, noViolations));
        assertOneViolation(field, TestViolation.class);

        field = new TestBaseField();
        assertEquals(ValidStatus.ALL_INVALID, field.validateProperties(oneViolation, oneViolation));
    }

    @Test
    public void testValidateOptionalProperties ()
    {
        TestBaseField field = new TestBaseField();

        final TestProperty noViolations = new TestProperty(field);
        noViolations.setValue("testvalue");
        final OneViolationProperty oneViolation = new OneViolationProperty(field);
        oneViolation.setValue("testvalue");
        final TestProperty unsetProperty = new TestProperty(field);

        assertEquals(ValidStatus.ALL_VALID, field.validateOptionalProperties(noViolations, noViolations));

        field = new TestBaseField();
        assertEquals(ValidStatus.ALL_VALID, field.validateOptionalProperties(noViolations, unsetProperty));
        assertNoViolations(field);

        field = new TestBaseField();
        assertEquals(ValidStatus.SOME_INVALID, field.validateOptionalProperties(oneViolation, noViolations));
        assertOneViolation(field, TestViolation.class);

        field = new TestBaseField();
        assertEquals(ValidStatus.ALL_INVALID, field.validateOptionalProperties(oneViolation, unsetProperty));

        field = new TestBaseField();
        assertEquals(ValidStatus.ALL_INVALID, field.validateOptionalProperties(oneViolation, oneViolation));

        field = new TestBaseField();
        assertEquals(ValidStatus.ALL_VALID, field.validateOptionalProperties(unsetProperty, unsetProperty));
        assertNoViolations(field);
    }

    @Test
    public void testReportUnsetDependentProperties ()
    {
        TestBaseField field = new TestBaseField();

        // if the property is set no violation
        final SetProperty dependent = new SetProperty(field);
        field.reportUnsetDependentProperties(new SetProperty(field), dependent);
        assertNoViolations(field);

        // if the property is unset, then there is a violation
        field = new TestBaseField();
        field.reportUnsetDependentProperties(new UnsetProperty(field), dependent);
        assertOneViolation(field, UnsetDependentPropertyViolation.class);
    }

    @Test
    public void testReportConflictingProperties ()
    {
        TestBaseField field = new TestBaseField();
        final SetProperty conflict = new SetProperty(field);

        // if the property is not set, no conflict
        field.reportConflictingProperties(new UnsetProperty(field), conflict);
        assertNoViolations(field);

        // if the property is set, there is a conflict
        field = new TestBaseField();
        field.reportConflictingProperties(new SetProperty(field), conflict);
        assertOneViolation(field, ConflictingPropertiesViolation.class);
    }

    @Test
    public void testReportUnsetField ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseField field = new TestBaseField();

        final RequiredField<TestBaseField> setField = new RequiredField<TestBaseField>(field, parent);
        field.reportUnsetField(setField);
        assertNoViolations(field);

        final RequiredField<TestBaseField> unsetField = new RequiredField<TestBaseField>(TestBaseField.class, parent);
        field.reportUnsetField(unsetField);
        assertOneViolation(field, UnsetFieldViolation.class);
    }

    @Test
    public void testReportUnsetDependentFields ()
    {
        final TestBaseField parent = new TestBaseField();
        final TestBaseField otherField = new TestBaseField();
        final TestBaseField field = new TestBaseField();
        final RequiredField<TestBaseField> otherFieldWrap = new RequiredField<TestBaseField>(otherField, parent);

        final RequiredField<TestBaseField> setField = new RequiredField<TestBaseField>(field, parent);
        field.reportUnsetDependentFields(setField, otherFieldWrap);
        assertNoViolations(field);

        final RequiredField<TestBaseField> unsetField = new RequiredField<TestBaseField>(TestBaseField.class, parent);
        field.reportUnsetDependentFields(unsetField, otherFieldWrap);
        assertOneViolation(field, UnsetDependentFieldViolation.class);
    }

    @Test
    public void testRegisterField ()
    {
        final TestBaseField field = new TestBaseField();
        final Project project = field.getProject();
        field.registerField(TestBaseField.class);
        // since BaseField actually descends from Task, Field objects will be added to the task
        // definitions and not the data type definitions
        assertTrue(project.getTaskDefinitions().containsKey(TestBaseField.FIELD_NAME));
    }

    @Test(expected=RuntimeException.class)
    public void testRegisterFieldNoProject ()
    {
        final TestBaseField field = new TestBaseField();
        field.setProject(null);
        field.registerField(TestBaseField.class);
    }
}
