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

import java.util.ArrayList;
import java.util.List;

import com.threerings.antidote.field.ListField;
import com.threerings.jpkg.debian.PackageInfo;
import com.threerings.jpkg.debian.dependency.DependencyAlternatives;
import com.threerings.jpkg.debian.dependency.PackageDependency;

import static com.threerings.antidote.MutabilityHelper.requiresValidation;


/**
 * The &lt;dependencies&gt; &lt;alternatives&gt; field. Holds &lt;require&gt; fields which indicate a list of
 * package dependencies, one of which must be met.
 * @see DependencyAlternatives
 */
public class Alternatives extends ListField<Require>
    implements PackageInfoDependency
{
    // from Field
    public String getFieldName ()
    {
        return "alternatives";
    }

    @Override // from ListField
    public String getChildFieldName ()
    {
        return "require";
    }

    // from PackageInfoDependency
    public void addToPackageInfo (PackageInfo info)
    {
        requiresValidation(_alternatives);
        info.addDependencyAlternative(_alternatives);
    }

    /**
     * Ant adder field: require.
     */
    public void add (Require require)
    {
        appendRequiresValidation(require);
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

        final List<PackageDependency> depends = new ArrayList<PackageDependency>();
        for (final Require require : getValidatedFieldList()) {
            depends.add(require.getDependency());
        }

        _alternatives = new DependencyAlternatives(depends);
    }

    /** The DependencyAlternatives object representing the user supplied data. */
    private DependencyAlternatives _alternatives;
}
