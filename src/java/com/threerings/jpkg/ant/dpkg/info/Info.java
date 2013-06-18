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

import com.threerings.antidote.field.BaseField;
import com.threerings.antidote.field.OptionalField;
import com.threerings.antidote.field.RequiredField;
import com.threerings.jpkg.debian.PackageInfo;

import static com.threerings.antidote.MutabilityHelper.areMutablesSet;
import static com.threerings.antidote.MutabilityHelper.requiresValidation;

/**
 * The &lt;dpkg&gt; task &lt;package&gt; &lt;info&gt; field. Used to keep track of all package meta-information.
 */
public class Info extends BaseField
{
    // from Field
    public String getFieldName ()
    {
        return "info";
    }

    /**
     * Ant adder field: Set the package name.
     */
    public void addName (Name name)
    {
        _name.setField(name);
    }

    /**
     * Ant adder field: Set the package version.
     */
    public void addVersion (Version version)
    {
        _version.setField(version);
    }

    /**
     * Ant adder field: Set the package architecture.
     */
    public void addArch (Arch arch)
    {
        _arch.setField(arch);
    }

    /**
     * Ant adder field: Set the package description.
     */
    public void addDescription (Description description)
    {
        _description.setField(description);
    }

    /**
     * Ant adder field: Set the package maintainer.
     */
    public void addMaintainer (Maintainer maintainer)
    {
        _maintainer.setField(maintainer);
    }

    /**
     * Ant adder field: Set the package priority.
     */
    public void addPriority (Priority priority)
    {
        _priority.setField(priority);
    }

    /**
     * Ant adder field: Set the package section.
     */
    public void addSection (Section section)
    {
        _section.setField(section);
    }

    /**
     * Returns the user data converted into a {@link PackageInfo} object. Cannot be called before validate().
     */
    public PackageInfo getPackageInfo ()
    {
        requiresValidation(_packageInfo);
        return _packageInfo;
    }

    /**
     * Returns the user data for the package name. Cannot be called before validate().
     */
    public String getPackageNameAsString ()
    {
        return _name.getField().getPackageName().getFieldValue();
    }

    /**
     * Returns the user data for the package version. Cannot be called before validate().
     */
    public String getVersionAsString ()
    {
        return _version.getField().getPackageVersion().getFieldValue();
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        // validate the required fields
        switch (validateChildFields(_name, _version, _arch, _description, _maintainer)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        // check the optional fields
        switch (areMutablesSet(_section, _priority)) {
            // if the optional fields are not set, create the simple PackageInfo object and finish.
            case ALL_UNSET:
                _packageInfo = new PackageInfo(
                    _name.getField().getPackageName(),
                    _version.getField().getPackageVersion(),
                    _arch.getField().getPackageArchitecture(),
                    _maintainer.getField().getPackageMaintainer(),
                    _description.getField().getPackageDescription());
                return;

            case SOME_UNSET:
                reportUnsetDependentFields(_priority, _priority, _section);
                reportUnsetDependentFields(_section, _section, _priority);
                return;

            // if all the optional fields are set, continue to validate those fields.
            case ALL_SET:
                break;
        }

        // validate the optional fields
        switch (validateChildFields(_section, _priority)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                // construct a fully populated PackageInfo object with the optional fields included.
                _packageInfo = new PackageInfo(
                    _name.getField().getPackageName(),
                    _version.getField().getPackageVersion(),
                    _arch.getField().getPackageArchitecture(),
                    _maintainer.getField().getPackageMaintainer(),
                    _description.getField().getPackageDescription(),
                    _section.getField().getPackageSection(),
                    _priority.getField().getPackagePriority());
                return;
        }
    }

    /** Ant adder/setter fields. */
    private final RequiredField<Name> _name = new RequiredField<Name>(Name.class, this);
    private final RequiredField<Version> _version = new RequiredField<Version>(Version.class, this);
    private final RequiredField<Arch> _arch = new RequiredField<Arch>(Arch.class, this);
    private final RequiredField<Description> _description = new RequiredField<Description>(Description.class, this);
    private final RequiredField<Maintainer> _maintainer = new RequiredField<Maintainer>(Maintainer.class, this);
    private final OptionalField<Priority> _priority = new OptionalField<Priority>(Priority.class, this);
    private final OptionalField<Section> _section = new OptionalField<Section>(Section.class, this);

    /** The PackageInfo object representing the user supplied data. */
    private PackageInfo _packageInfo;
}
