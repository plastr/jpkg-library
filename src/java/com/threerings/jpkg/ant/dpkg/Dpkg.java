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
package com.threerings.jpkg.ant.dpkg;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.threerings.antidote.Validator;
import com.threerings.antidote.field.ListTask;
import com.threerings.antidote.property.FileProperty;
import com.threerings.antidote.property.StringProperty;
import com.threerings.jpkg.PackageBuilder;
import com.threerings.jpkg.ant.dpkg.dependencies.Conflict;
import com.threerings.jpkg.ant.dpkg.dependencies.Replacement;
import com.threerings.jpkg.ant.dpkg.dependencies.Require;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.EqualOrGreaterThan;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.EqualOrLesserThan;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.EqualTo;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.GreaterThan;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.LesserThan;
import com.threerings.jpkg.debian.DebianPackageBuilder;
import com.threerings.jpkg.debian.PackageInfo;

public class Dpkg extends ListTask<Package>
{
    /**
     * All user supplied data will be assumed to be in this character encoding, e.g. maintainer
     * script source.
     */
    public static final String CHAR_ENCODING = "UTF-8";

    // from Field
    public String getFieldName ()
    {
        return "dpkg";
    }

    @Override // from ListComponent
    public String getChildFieldName ()
    {
        return "package";
    }

    @Override // from Task
    public void init ()
    {
        // register the dependency fields.
        registerField(Require.class);
        registerField(Conflict.class);
        registerField(Replacement.class);

        // register the dependency condition fields.
        registerField(EqualTo.class);
        registerField(GreaterThan.class);
        registerField(EqualOrGreaterThan.class);
        registerField(LesserThan.class);
        registerField(EqualOrLesserThan.class);
    }

    @Override // from Task
    public void execute ()
    {
        final Validator validator = new Validator();
        validator.addValidation(this);
        validator.validateAll();

        for (final Package pkg : getValidatedFieldList()) {
            final File destination = new File(FilenameUtils.concat(_output.getValue().getAbsolutePath(), pkg.getFilename()));

            final PackageInfo info = pkg.createPackageInfo(_distribution.getValue(), _prefix.getValue());
            final PackageBuilder builder = new DebianPackageBuilder(info);
            log("Creating dpkg package " + destination.getAbsolutePath() + " from destroot " +
                pkg.getDestroot().getAbsolutePath(), Project.MSG_INFO);
            try {
                builder.write(destination, pkg.getDestroot());

            } catch (final Exception e) {
                throw new BuildException(e);
            }
        }
    }

    /**
     * Ant setter field: output. The directory to create all packages in.
     */
    public void setOutput (String value)
    {
        _output.setValue(value);
    }

    /**
     * Ant setter field: prefix. The prefix, or root, of all defined packages.
     */
    public void setPrefix (String value)
    {
        _prefix.setValue(value);
    }

    /**
     * Ant setter field: distribution. The Apt distribution these packages will be apart of.
     */
    public void setDistribution (String value)
    {
        _distribution.setValue(value);
    }

    /**
     * Ant adder field: Add a &lt;package&gt; definition.
     */
    public void addPackage (Package pkg)
    {
        appendRequiresValidation(pkg);
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

        switch (validateProperties(_output, _prefix, _distribution)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }
    }

    /** Ant adder/setter fields. */
    private final FileProperty _output = new FileProperty("output", this);
    private final StringProperty _prefix = new StringProperty("prefix", this);
    private final StringProperty _distribution = new StringProperty("distribution", this);
}
