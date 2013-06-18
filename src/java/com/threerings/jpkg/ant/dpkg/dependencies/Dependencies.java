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

import java.util.List;

import com.threerings.antidote.field.ListField;
import com.threerings.jpkg.debian.dependency.PackageConflicts;
import com.threerings.jpkg.debian.dependency.PackageDependencies;
import com.threerings.jpkg.debian.dependency.PackageReplacements;

/**
 * The &lt;dpkg&gt; task &lt;package&gt; &lt;dependencies&gt; field. Used to add package dependency information.
 * @see PackageDependencies
 * @see PackageConflicts
 * @see PackageReplacements
 */
public class Dependencies extends ListField<PackageInfoDependency>
{
    // from Field
    public String getFieldName ()
    {
        return "dependencies";
    }

    @Override // from ListField
    public String getChildFieldName ()
    {
        return "depedency";
    }

    /**
     * Ant adder field: dependency. All {@link BaseDependency}.
     */
    public void add (BaseDependency<?> dependency)
    {
        appendRequiresValidation(dependency);
    }

    /**
     * Ant adder field: alternatives.
     */
    public void addAlternatives (Alternatives alternatives)
    {
        appendRequiresValidation(alternatives);
    }

    /**
     * Returns the user data converted into a {@link PackageInfoDependency} objects.
     * Cannot be called before validate().
     */
    public List<PackageInfoDependency> getDependencies ()
    {
        requireChildFieldValidation();
        return _validatedDependencies;
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        if (noChildFieldsDefined()) {
            return;
        }

        switch (validateFieldList()) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        _validatedDependencies = getValidatedFieldList();
    }

    /** The list of {@link PackageInfoDependency} objects. Populated after all fields pass validation. */
    private List<PackageInfoDependency> _validatedDependencies;
}
