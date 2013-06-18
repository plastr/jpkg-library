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
package com.threerings.jpkg.ant.dpkg.info;

import java.util.ArrayList;
import java.util.List;

import com.threerings.antidote.ValidStatus;
import com.threerings.antidote.field.FieldWrapper;
import com.threerings.antidote.field.OptionalField;
import com.threerings.antidote.field.RequiredField;
import com.threerings.antidote.field.text.TextField;
import com.threerings.antidote.field.text.TextStatus;
import com.threerings.jpkg.ant.dpkg.info.description.Blank;
import com.threerings.jpkg.ant.dpkg.info.description.DescriptionAction;
import com.threerings.jpkg.ant.dpkg.info.description.Paragraph;
import com.threerings.jpkg.ant.dpkg.info.description.Summary;
import com.threerings.jpkg.ant.dpkg.info.description.Verbatim;
import com.threerings.jpkg.debian.ControlDataInvalidException;
import com.threerings.jpkg.debian.PackageDescription;

import static com.threerings.antidote.MutabilityHelper.areMutablesSet;
import static com.threerings.antidote.MutabilityHelper.requiresValidation;


/**
 * Stores the &lt;info&gt; &lt;description&gt; field, which is the package description.
 * @see PackageDescription
 */
public class Description extends TextField
{
    // from Field
    public String getFieldName ()
    {
        return "description";
    }

    /**
     * Ant adder field: Add the summary description.
     */
    public void addSummary (Summary summary)
    {
        _summary.setField(summary);
    }

    /**
     * Ant adder field: Add a blank line to this description.
     */
    public void addBlank (Blank blank)
    {
        _actions.add(new RequiredField<Blank>(blank, this));
    }

    /**
     * Ant adder field: Add a word wrapped paragraph to this description.
     */
    public void addParagraph (Paragraph paragraph)
    {
        _actions.add(new RequiredField<Paragraph>(paragraph, this));
    }

    /**
     * Ant adder field: Add a none word wrapped verbatim paragraph to this description.
     */
    public void addVerbatim (Verbatim verbatim)
    {
        _actions.add(new RequiredField<Verbatim>(verbatim, this));
    }

    /**
     * Returns the user data converted into a {@link PackageDescription}. Cannot be called before validate().
     */
    public PackageDescription getPackageDescription ()
    {
        requiresValidation(_packageDescription);
        return _packageDescription;
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        String summaryText = null;
        // either the description has the summary provided in a <summary> field with optional
        // additional fields or it is provided in a simpler <description>summary</description>
        switch (areMutablesSet(_summary)) {
            case ALL_UNSET:
            case SOME_UNSET:
                // if other actions were declared, but no <summary> was set, report the missing field.
                if (_actions.size() > 0) {
                    reportUnsetField(_summary);
                    return;
                }

                // otherwise the simple <description>description</description> form must have been
                // used so pull the summary text from the text field.
                if (validateTextNotEmpty() == TextStatus.INVALID_TEXT) return;
                scrubTextWhitespace();

                summaryText = getText();
                break;

            case ALL_SET:
                // the <summary> field was set so validate it and pull the summary text from there.
                if (validateChildFields(_summary) == ValidStatus.ALL_INVALID) return;

                summaryText = _summary.getField().getSummary();
                break;
        }

        // create the PackageDescription with the summary text
        try {
            _packageDescription = new PackageDescription(summaryText);

        } catch (final ControlDataInvalidException cdie) {
            appendViolation(new ControlDataViolation(this, cdie));
            return;
        }

        // if we have no additional actions to perform on the description, return.
        if (_actions.size() == 0) {
            return;
        }

        // validate each action field,
        switch (validateChildFields(_actions)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        // apply each action field to the description.
        for (final FieldWrapper<? extends DescriptionAction> field : _actions) {
            appendViolationList(field.getField().apply(_packageDescription));
        }
    }

    /** The list of {@link DescriptionAction} objects to apply to this description. */
    private final List<RequiredField<? extends DescriptionAction>> _actions =
        new ArrayList<RequiredField<? extends DescriptionAction>>();

    /** Ant adder/setter fields. */
    private final OptionalField<Summary> _summary = new OptionalField<Summary>(Summary.class, this);

    /** The PackageDescription object representing the user supplied data. */
    private PackageDescription _packageDescription;
}
