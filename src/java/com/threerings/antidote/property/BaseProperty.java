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
package com.threerings.antidote.property;

import java.util.ArrayList;
import java.util.List;

import com.threerings.antidote.RequiresValidation;
import com.threerings.antidote.Violation;
import com.threerings.antidote.field.Field;

import static com.threerings.antidote.MutabilityHelper.objectIsNotSet;
import static com.threerings.antidote.MutabilityHelper.objectIsSet;
import static com.threerings.antidote.MutabilityHelper.requiresValidation;


/**
 * A base implementation for {@link Property} objects to extend from. Generic type indicates the
 * type of object being held by the property after validation.
 */
public abstract class BaseProperty<T>
    implements Property<T>, RequiresValidation
{
    /**
     * Construct a new BaseProperty.
     * @param name The name of this property.
     * @param field The {@link Field} holding this property.
     */
    public BaseProperty (String name, Field field)
    {
        this(name, field, null);
    }

    /**
     * Construct a new BaseProperty with a default value.
     * @param name The name of this property.
     * @param field The {@link Field} holding this property.
     * @param defaultValue The default value for the field.
     */
    public BaseProperty (String name, Field field, T defaultValue)
    {
        _name = name;
        _field = field;
        _defaultValue = defaultValue;
    }

    // from Property
    public final String getPropertyName ()
    {
        return _name;
    }

    // from Property
    public final Field getField ()
    {
        return _field;
    }

    // from Property
    public final T getValue ()
    {
        requiresValidation(_validatedValue);
        return _validatedValue;
    }

    // from Mutable
    public boolean isSet ()
    {
        return objectIsSet(_rawValue);
    }

    // from Mutable
    public boolean isNotSet ()
    {
        return objectIsNotSet(_rawValue);
    }

    /**
     * Implement validate() in the base abstract class ensuring correct behavior. Provide accessors
     * to the list of violations in protected methods.
     * @see #appendViolation(Violation)
     */
    // from RequiresValidation
    public final List<Violation> validate ()
    {
        // if the raw value has not been set, use the default value if set. otherwise append a
        // violation and stop processing, skipping property specific validation.
        if (isNotSet()) {
            if (objectIsSet(_defaultValue)) {
                _validatedValue = _defaultValue;

            } else {
                appendViolation(new UnsetPropertyViolation(this));
            }

        } else {
            _validatedValue = validateProperty();
        }
        return _violations;
    }

    /**
     * Sets the raw String value of the property. It is intended that this method would be called
     * from an Ant setter method.
     */
    public final void setValue (String value)
    {
        _rawValue = value;
    }

    /**
     * Give each {@link Property} a chance to do property specific validation and return the
     * validated value. If the raw value could not turned into a valid value, this will return null.
     */
    protected abstract T validateProperty ();

    /**
     * Add a violation to the list of violations returned when this {@link Property} is validated.
     */
    protected void appendViolation (Violation violation)
    {
        _violations.add(violation);
    }

    /**
     * Provides concrete classes access to the raw user value.
     */
    protected final String getRawValue ()
    {
        return _rawValue;
    }

    /** The name of this property. */
    private final String _name;

    /** The {@link Field} holding this property. */
    private final Field _field;

    /** The value assigned to this property in its original string form. Can be null. */
    private String _rawValue;

    /** Holds the value after it has been validated from the raw value. */
    private T _validatedValue;

    /** The optional default value for the property if the property is unset. */
    private T _defaultValue;

    /** The list of any validation violations for this {@link Property}. */
    private final List<Violation> _violations = new ArrayList<Violation>();
}
