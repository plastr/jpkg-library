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

import com.threerings.antidote.RequiresValidationException;
import com.threerings.antidote.ValidStatus;

import static com.threerings.antidote.MutabilityHelper.requiresValidation;

/**
 * An abstract {@link BaseComponent} designed to hold lists of {@link BaseComponent} objects and
 * tools to validate those fields.
 * Package private. Use one of the subclasses.
 * @see ListField
 * @see ListTask
 */
abstract class ListComponent<V extends ReferenceField> extends BaseComponent
{
    /**
     * Returns the name of the child fields of this ListField.
     */
    public abstract String getChildFieldName ();

    /**
     * Append a field requiring validation to the list.
     */
    protected void appendRequiresValidation (V needsValidation)
    {
        _requireValidation.add(new RequiredField<V>(needsValidation, this));
    }

    /**
     * Return the list of validated fields. Must be called after {@link #validateFieldList()}
     */
    protected List<V> getValidatedFieldList ()
    {
        requireChildFieldValidation();
        return _validatedFields;
    }

    /**
     * Throws an {@link RequiresValidationException} if the child fields have not been validated.
     */
    protected void requireChildFieldValidation ()
    {
        requiresValidation(_validatedFields);
    }

    /**
     * Adds a violation and returns true if no child fields were defined, false otherwise.
     */
    protected boolean noChildFieldsDefined ()
    {
        if (_requireValidation.size() == 0) {
            appendViolation(new AtLeastOneFieldViolation(this));
            return true;

        } else {
            return false;
        }
    }

    /**
     * Validate the list of declared child fields. Must be called before {@link #getValidatedFieldList()}.
     * Returns a {@link ValidStatus} enum describing the validated fields.
     */
    protected ValidStatus validateFieldList ()
    {
        final ValidStatus status = validateChildFields(_requireValidation);
        switch (status) {
            case ALL_INVALID:
            case SOME_INVALID:
                return status;

            case ALL_VALID:
                break;
        }

        _validatedFields = new ArrayList<V>();
        for (final RequiredField<V> field : _requireValidation) {
            _validatedFields.add(field.getField());
        }
        return status;
    }

    /** The list of fields defined in the section that need to be validated. */
    private final List<RequiredField<V>> _requireValidation = new ArrayList<RequiredField<V>>();

    /** The list of fields after they have been validated. */
    private List<V> _validatedFields;
}

