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

import org.apache.tools.ant.Project;

import com.threerings.antidote.EnumHelper;
import com.threerings.antidote.field.text.SingleLineTextField;
import com.threerings.antidote.property.BooleanProperty;
import com.threerings.jpkg.debian.DebianArchitectures;
import com.threerings.jpkg.debian.PackageArchitecture;

import static com.threerings.antidote.MutabilityHelper.objectIsSet;
import static com.threerings.antidote.MutabilityHelper.requiresValidation;


/**
 * Stores the &lt;info&gt; &lt;arch&gt; field, which is the package architecture.
 * @see PackageArchitecture
 */
public class Arch extends SingleLineTextField
{
    // from Field
    public String getFieldName ()
    {
        return "arch";
    }

    /**
     * Ant setter field: strict. If set to false, unknown architectures will be permitted.
     * Defaults to true.
     */
    public void setStrict (String value)
    {
        _strict.setValue(value);
    }

    /**
     * Returns the user data converted into a {@link PackageArchitecture}. Cannot be called before validate().
     */
    public PackageArchitecture getPackageArchitecture ()
    {
        requiresValidation(_packageArchitecture);
        return _packageArchitecture;
    }

    @Override // from SingleLineTextField
    protected void validateTextField ()
    {
        // strict defaults to true
        switch (validateProperties(_strict)) {
            case SOME_INVALID:
            case ALL_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        // if the user supplied architecture name is known, use it.
        final DebianArchitectures arch = EnumHelper.parseEnum(getText(), DebianArchitectures.class);
        if (objectIsSet(arch)) {
            _packageArchitecture = new PackageArchitecture(arch);
            return;
        }

        // if the user supplied name is not known, and we are in strict mode, append a violation,
        // and end. if not in strict mode, log a warning and proceed.
        if (_strict.getValue()) {
            appendViolation(new UnknownArchitectureViolation(this, getText()));
            return;

        } else {
            log("The supplied architecture name \'" + getText() + "\' for the <" + getFieldName() +
                "> field is unknown. Strict was set to false so the data will be trusted. See " +
                "DebianArchitectures or dpkg-architecture for a list of known architectures.", Project.MSG_WARN);
            _packageArchitecture = new PackageArchitecture(getText());
            return;
        }
    }

    /** Ant adder/setter fields. */
    private final BooleanProperty _strict = new BooleanProperty("strict", this, true);

    /** The PackageArchitecture object representing the user supplied data. */
    private PackageArchitecture _packageArchitecture;
}
