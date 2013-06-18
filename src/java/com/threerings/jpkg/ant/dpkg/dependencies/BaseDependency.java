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
package com.threerings.jpkg.ant.dpkg.dependencies;

import com.threerings.antidote.field.BaseField;
import com.threerings.antidote.field.OptionalField;
import com.threerings.antidote.property.StringProperty;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.Condition;
import com.threerings.jpkg.debian.dependency.AbstractDependency;

import static com.threerings.antidote.MutabilityHelper.areMutablesSet;
import static com.threerings.antidote.MutabilityHelper.requiresValidation;

/**
 * Base class for all {@link Dependencies} fields.
 * @see AbstractDependency
 */
public abstract class BaseDependency<T extends AbstractDependency> extends BaseField
    implements PackageInfoDependency
{
    /**
     * Ant setter field: package. The name of the package.
     */
    public void setPackage (String value)
    {
        _packageName.setValue(value);
    }

    /**
     * Ant adder field: condition. All {@link Condition} objects.
     */
    public void add (Condition condition)
    {
        _condition.setField(condition);
    }

    /**
     * Construct an instance of the {@link AbstractDependency} defined in this field with just
     * the package name defined.
     */
    protected abstract T createDependency (StringProperty packageName);

    /**
     * Construct an instance of the {@link AbstractDependency} defined in this field with a
     * package name and condition defined.
     */
    protected abstract T createDependency (StringProperty packageName, Condition condition);

    /**
     * Get the concrete {@link AbstractDependency} after it has been created.
     */
    protected T getDependency ()
    {
        requiresValidation(_abstractDependency);
        return _abstractDependency;
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        // validate required properties
        switch (validateProperties(_packageName)) {
            case SOME_INVALID:
            case ALL_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        // determine if we have a Condition set
        switch (areMutablesSet(_condition)) {
            case ALL_UNSET:
            case SOME_UNSET:
                // no condition set, construct the AbstractDependency
                _abstractDependency = createDependency(_packageName);
                return;

            case ALL_SET:
                // condition set, move on to validation
                break;
        }

        // validate condition field
        switch (validateChildFields(_condition)) {
            case SOME_INVALID:
            case ALL_INVALID:
                return;

            case ALL_VALID:
                _abstractDependency = createDependency(_packageName, _condition.getField());
                return;
        }
    }

    /** The AbstractDependency object representing the user supplied data. */
    private T _abstractDependency;

    /** Ant adder/setter fields. */
    private final StringProperty _packageName = new StringProperty("package", this);
    private final OptionalField<Condition> _condition = new OptionalField<Condition>("condition", this);
}
