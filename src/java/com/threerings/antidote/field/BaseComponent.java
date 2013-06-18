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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Reference;

import com.threerings.antidote.ValidStatus;
import com.threerings.antidote.Violation;
import com.threerings.antidote.property.Property;

import static com.threerings.antidote.MutabilityHelper.objectIsNotSet;
import static com.threerings.antidote.MutabilityHelper.objectIsSet;

/**
 * A base class for all Ant fields providing useful functionality, such as {@link Violation} management.
 * Ideally this class would extend from {@link ProjectComponent}. However, {@link Task} objects
 * act as fields and end up benefiting greatly from this classes functionality. Use the {@link BaseTask}
 * functionality if the implementing class requires {@link Task} functionality, otherwise, use
 * {@link BaseField}.
 * Package private. Use one of the subclasses.
 * @see BaseField
 * @see BaseTask
 */
abstract class BaseComponent extends Task
    implements ReferenceField
{
    /**
     * Implement validate() in the base abstract class ensuring correct behavior. Provide accessors
     * to the list of violations in protected methods.
     * @see #appendViolation(Violation)
     */
    // from RequiresValidation
    public final List<Violation> validate ()
    {
        validateField();
        return _violations;
    }

    // from ReferenceField
    public Object getReferencedField ()
    {
        if (objectIsNotSet(_reference)) {
            throw new UnsetReferenceException();
        }

        return _reference.getReferencedObject();
    }

    // from ReferenceField
    public boolean isReference ()
    {
        return objectIsSet(_reference);
    }

    /**
     * A protected setter meant for concrete classes to optionally support Ant reference setting
     * by using a public setter method which calls this, e.g. setRefid.
     */
    protected void setReference (Reference reference)
    {
        _reference = reference;
    }

    /**
     * Give each subclass a chance to do field specific validation.
     */
    protected abstract void validateField ();

    /**
     * Add a violation to the list of violations returned when this {@link Field} is validated.
     */
    protected void appendViolation (Violation violation)
    {
        _violations.add(violation);
    }

    /**
     * Add a list of violations to the list of violations returned when this {@link Field} is validated.
     */
    protected void appendViolationList (List<Violation> violations)
    {
        _violations.addAll(violations);
    }

    /**
     * Validate a varargs list of {@link FieldWrapper} objects that are children of this {@link Field}.
     * @see #validateChildFields(List)
     */
    protected ValidStatus validateChildFields (FieldWrapper<?>...childFields)
    {
        return validateChildFields(Arrays.asList(childFields));
    }

    /**
     * Validate a {@link List} of {@link FieldWrapper} objects that are children of this {@link Field}.
     * Return a {@link ValidStatus} enum describing the valid state of the list.
     * @throws IllegalArgumentException If the supplied {@link FieldWrapper} is null.
     */
    protected ValidStatus validateChildFields (List<? extends FieldWrapper<?>> childFields)
    {
        int invalid = 0;
        for (final FieldWrapper<?> childField : childFields) {
            if (objectIsNotSet(childField)) {
                throw new IllegalArgumentException("Programmer error. Fields being validated cannot" +
                    "be null.");
            }

            final int before = _violations.size();
            appendViolationList(childField.validate());

            if (_violations.size() > before) {
                invalid++;
            }
        }

        if (invalid == childFields.size()) {
            return ValidStatus.ALL_INVALID;

        } else if (invalid == 0) {
            return ValidStatus.ALL_VALID;

        } else {
            return ValidStatus.SOME_INVALID;
        }
    }

    /**
     * Validate a varargs list of {@link Property} objects.
     * @see #validateProperties(List)
     */
    protected ValidStatus validateProperties (Property<?>...properties)
    {
        return validateProperties(Arrays.asList(properties));
    }

    /**
     * Validate a list of {@link Property} objects.
     * Return a {@link ValidStatus} enum describing the valid state of the list.
     */
    protected ValidStatus validateProperties (List<Property<?>> properties)
    {
        int invalid = 0;
        for (final Property<?> property : properties) {
            final int before = _violations.size();
            appendViolationList(property.validate());

            if (_violations.size() > before) {
                invalid++;
            }
        }

        if (invalid == properties.size()) {
            return ValidStatus.ALL_INVALID;

        } else if (invalid == 0) {
            return ValidStatus.ALL_VALID;

        } else {
            return ValidStatus.SOME_INVALID;
        }
    }

    /**
     * Validate a list of {@link Property} objects that are optional. If they are unset, nothing
     * will happen. Returns the {@link ValidStatus} of the set properties. If no properties are set,
     * ALL_VALID is returned.
     */
    // TODO: can this be factored into the properties themselves, like FieldWrapper?
    protected ValidStatus validateOptionalProperties (Property<?>...properties)
    {
        final List<Property<?>> setProperties = new ArrayList<Property<?>>();
        for (final Property<?> property : properties) {
            if (property.isNotSet()) continue;
            setProperties.add(property);
        }

        if (setProperties.size() == 0) {
            return ValidStatus.ALL_VALID;

        } else {
            return validateProperties(setProperties);
        }
    }

    /**
     * If the supplied property is unset, then report a violation that the property cannot be
     * unset if any of the supplied list of {@link Property} objects are set.
     */
    protected void reportUnsetDependentProperties (Property<?> property, Property<?>...dependents)
    {
        if (property.isNotSet()) {
            appendViolation(new UnsetDependentPropertyViolation(property, propertyNames(dependents)));
        }
    }

    /**
     * If the supplied property is set, e.g. not null, then report a violation that the property cannot
     * be set if any of the supplied list of {@link Property} objects are set.
     */
    protected void reportConflictingProperties (Property<?> property, Property<?>...conflicts)
    {
        if (property.isSet()) {
            appendViolation(new ConflictingPropertiesViolation(property, propertyNames(conflicts)));
        }
    }

    /**
     * If the supplied {@link FieldWrapper} is unset then report a violation that the field cannot be
     * unset if any of the supplied list of {@link FieldWrapper} objects are set.
     */
    protected void reportUnsetDependentFields (FieldWrapper<?> field, FieldWrapper<?>...depends)
    {
        if (field.isNotSet()) {
            appendViolation(new UnsetDependentFieldViolation(field, getFieldNames(depends)));
        }
    }

    /**
     * Report as a violation if the supplied {@link FieldWrapper} is unset.
     */
     protected void reportUnsetField (FieldWrapper<?> field)
     {
         if (field.isNotSet()) {
             appendViolation(new UnsetFieldViolation(field));
         }
     }

    /**
     * Register a given {@link Field} class as a data type for this {@link Project}.
     */
    protected void registerField (Class<? extends Field> clazz)
    {
        final Project project = getProject();
        if (objectIsNotSet(project)) {
            throw new RuntimeException("Cannot register a field before setProject() has been called.");
        }
        project.addDataTypeDefinition(FieldHelper.getFieldInstance(clazz).getFieldName(), clazz);
    }

    /**
     * Given a list of {@link Field} Class objects, return a string representation of the field names.
     */
    private String getFieldNames (FieldWrapper<?>...fields)
    {
        final StringBuilder builder = new StringBuilder();
        for (final FieldWrapper<?> field : fields) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append('<').append(field.getFieldName()).append('>');
        }
        return builder.toString();
    }

    /**
     * Given a list of {@link Property} objects, return a comma separated list of the property names.
     */
    private String propertyNames (Property<?>...properties)
    {
        final StringBuilder builder = new StringBuilder();
        for (final Property<?> prop : properties) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(prop.getPropertyName());
        }
        return builder.toString();
    }

    /** The optional reference meant to replace this field. */
    private Reference _reference;

    /** The list of any validation violations for this {@link Field}. */
    private final List<Violation> _violations = new ArrayList<Violation>();
}
