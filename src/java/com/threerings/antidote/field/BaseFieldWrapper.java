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

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Location;

import com.threerings.antidote.Violation;

import static com.threerings.antidote.MutabilityHelper.objectIsNotSet;
import static com.threerings.antidote.MutabilityHelper.objectIsSet;

/**
 * A base class for common {@link FieldWrapper} functionality.
 * Package private. Use one of the subclasses.
 * @see RequiredField
 * @see OptionalField
 */
abstract class BaseFieldWrapper<F extends ReferenceField>
    implements FieldWrapper<F>
{
    /**
     * Construct a {@link FieldWrapper} by providing the already constructed {@link Field} to be
     * wrapped and the parent {@link Field} which holds the wrapped {@link Field}.
     * @param wrapped The {@link Field} to be wrapped.
     * @param parent The wrapped {@link Field} parent.
     * @throws IllegalArgumentException If either wrapped or parent is null.
     */
    public BaseFieldWrapper (F wrapped, Field parent)
    {
        verifyWrappedNotNull(wrapped);
        verifyParentNotNull(parent);

        _wrapped = wrapped;
        _name = wrapped.getFieldName();
        _parent = parent;
    }

    /**
     * Construct a {@link FieldWrapper} which expects the wrapped {@link Field} to be set later
     * using {@link #setField}. The {@link Class} of the wrapped {@link Field} is provided
     * so that the name of the {@link Field} can be determined. The parent {@link Field} which
     * holds the wrapped {@link Field} is also provided.
     * @param parent The wrapped {@link Field} parent.
     * @throws IllegalArgumentException If parent is null.
     */
    public BaseFieldWrapper (Class<? extends Field> clazz, Field parent)
    {
        verifyParentNotNull(parent);

        _name = FieldHelper.getFieldInstance(clazz).getFieldName();
        _parent = parent;
    }

    /**
     * Construct a {@link FieldWrapper} which expects the wrapped {@link Field} to be set later
     * using {@link #setField}. This constructor should be used for abstract {@link Field}
     * classes where the name of the field cannot be known until a concrete class is constructed
     * via the Ant setter or added method. The name of the abstract {@link Field} class is provided
     * to be used for the field name until the wrapped field is set. The parent {@link Field} which
     * holds the wrapped {@link Field} is also provided.
     * @param abstractName The name of the abstract {@link Field} to be wrapped.
     * @param parent The wrapped {@link Field} parent.
     * @throws IllegalArgumentException If parent or abstractName is null.
     */
    // TODO: revisit this.
    public BaseFieldWrapper (String abstractName, Field parent)
    {
        if (objectIsNotSet(abstractName)) {
            throw new IllegalArgumentException("Programmer error. The abstract Field name may not " +
                "be null.");
        }
        verifyParentNotNull(parent);

        _name = abstractName;
        _parent = parent;
    }

    // from FieldWrapper
    public void setField (F wrapped)
    {
        if (isSet()) {
            _violations.add(new OnlyOneFieldViolation(this));
            return;
        }

        // verify the supplied field is not null
        verifyWrappedNotNull(wrapped);

        // update the name of the field in case it was initialized to the abstract class name.
        _name = wrapped.getFieldName();

        _wrapped = wrapped;
    }

    // from FieldWrapper
    public F getField ()
    {
        if (isNotSet()) {
            throw new UnsetWrappedFieldException();
        }

        if (_wrapped.isReference()) {
            @SuppressWarnings("unchecked")
            final F field = (F)_wrapped.getReferencedField();
            return field;

        } else {
            return _wrapped;
        }
    }

    // from FieldWrapper
    public Field getParent ()
    {
        return _parent;
    }

    // from Field
    public String getFieldName ()
    {
        return _name;
    }

    /**
     * Provide the location of the wrapped {@link Field} if it is set, otherwise use the parent
     * {@link Field} location, which will be a good estimate.
     */
    // from Field
    public Location getLocation ()
    {
        if (isSet()) {
            return _wrapped.getLocation();

        } else {
            return _parent.getLocation();
        }
    }

    // from Mutable
    public boolean isSet ()
    {
        return objectIsSet(_wrapped);
    }

    // from Mutable
    public boolean isNotSet ()
    {
        return objectIsNotSet(_wrapped);
    }

    // from RequiresValidation
    public final List<Violation> validate ()
    {
        validateWrappedField();
        return _violations;
    }

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
     * Provide subclasses a chance to perform additional validation, including validation of the
     * wrapped field.
     */
    protected abstract void validateWrappedField ();

    /**
     * Throws an {@link IllegalArgumentException} if the parent was not set.
     */
    private void verifyParentNotNull (Field parent)
    {
        if (objectIsNotSet(parent)) {
            throw new IllegalArgumentException("Programmer error. The wrapped Field parent may not " +
                " be null. ");
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the wrapped field was not set.
     */
    private void verifyWrappedNotNull (Field wrapped)
    {
        if (objectIsNotSet(wrapped)) {
            throw new IllegalArgumentException("Programmer error. The wrapped Field may not be null. " +
                " Use setField() if the wrapped Field must be set after construction.");
        }
    }

    /** The wrapped {@link Field}. May be null. */
    private F _wrapped;

    /** The name of the wrapped {@link Field}. Will not be null. Non-final to support abstract fields. */
    private String _name;

    /** The parent {@link Field} of this wrapped {@link Field}. */
    private final Field _parent;

    /** The list of any validation violations for this {@link Field}. */
    private final List<Violation> _violations = new ArrayList<Violation>();
}
