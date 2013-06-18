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

import com.threerings.antidote.field.text.SingleLineTextField;
import com.threerings.antidote.property.IntegerProperty;
import com.threerings.antidote.property.StringProperty;
import com.threerings.jpkg.debian.ControlDataInvalidException;
import com.threerings.jpkg.debian.PackageVersion;

import static com.threerings.antidote.MutabilityHelper.areMutablesSet;
import static com.threerings.antidote.MutabilityHelper.requiresValidation;


/**
 * Stores the &lt;info&gt; &lt;version&gt; field, which is the package version.
 * @see PackageVersion
 */
public class Version extends SingleLineTextField
{
    // from Field
    public String getFieldName ()
    {
        return "version";
    }

    /**
     * Ant setter field: epoch.
     */
    public void setEpoch (String value)
    {
        _epoch.setValue(value);
    }

    /**
     * Ant setter field: debianVersion.
     */
    public void setDebianVersion (String value)
    {
        _debianVersion.setValue(value);
    }

    /**
     * Returns the user data converted into a {@link PackageVersion}. Cannot be called before validate().
     */
    public PackageVersion getPackageVersion ()
    {
        requiresValidation(_packageVersion);
        return _packageVersion;
    }

    @Override // from SingleLineTextField
    protected void validateTextField ()
    {
        try {
            // check the optional properties
            switch (areMutablesSet(_epoch, _debianVersion)) {
                case ALL_UNSET:
                    // the optional fields are not set, construct the simple version
                    _packageVersion = new PackageVersion(getText());
                    return;

                case SOME_UNSET:
                    reportUnsetDependentProperties(_epoch, _debianVersion);
                    reportUnsetDependentProperties(_debianVersion, _epoch);
                    return;

                case ALL_SET:
                    // continue to validation
                    break;
            }

            // validate the optional properties
            switch (validateProperties(_debianVersion, _epoch)) {
                case SOME_INVALID:
                case ALL_INVALID:
                    return;

                case ALL_VALID:
                    // construct the complex version
                    _packageVersion = new PackageVersion(
                        getText(), _debianVersion.getValue(), _epoch.getValue());
                    return;
            }

        } catch (final ControlDataInvalidException cdie) {
            appendViolation(new ControlDataViolation(this, cdie));
        }
    }

    /** The PackageVersion object representing the user supplied data. */
    private PackageVersion _packageVersion;

    /** Ant adder/setter fields. */
    private final StringProperty _debianVersion = new StringProperty("debianversion", this);
    private final IntegerProperty _epoch = new IntegerProperty("epoch", this);
}
