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
package com.threerings.jpkg.ant.dpkg.scripts.standard;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.threerings.antidote.property.BooleanProperty;
import com.threerings.antidote.property.FileProperty;
import com.threerings.antidote.property.StringProperty;
import com.threerings.jpkg.ant.dpkg.DpkgData;
import com.threerings.jpkg.ant.dpkg.scripts.TemplateScript;
import com.threerings.jpkg.debian.MaintainerScript;

import static com.threerings.antidote.MutabilityHelper.areMutablesSet;


/**
 * An abstract {@link TemplateScript} for defining the script source from either a file or a
 * single command listed as a string.
 */
public abstract class AbstractTypeScript extends TemplateScript
{
    public AbstractTypeScript (MaintainerScript.Type type)
    {
        super(type);
    }

    // from VelocityTemplate
    public String getTemplateName ()
    {
        return "scripts/command_script.vm";
    }

    @Override // from TemplateScript
    public InputStream getSource (DpkgData data)
        throws IOException
    {
        // if the command property was set, return the velocity template.
        if (_command.isSet()) {
            addSubstitution("command", _command.getValue());
            return super.getSource(data);

        // otherwise return the file data.
        } else {
            return new FileInputStream(_source.getValue());
        }
    }

    // from PackageScript
    public boolean failOnError ()
    {
        return _failonerror.getValue();
    }

    /**
     * Ant setter field: the command line.
     */
    public void setCommand (String value)
    {
        _command.setValue(value);
    }

    /**
     * Ant setter field: the source file.
     */
    public void setSource (String value)
    {
        _source.setValue(value);
    }

    /**
     * Ant setter field: failonerror.
     */
    public void setFailonerror (String value)
    {
        _failonerror.setValue(value);
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        switch (areMutablesSet(_command, _source)) {
            case ALL_UNSET:
                appendViolation(new UnsetScriptPropertiesViolation(this));
                return;

            case ALL_SET:
                reportConflictingProperties(_command, _source);
                reportConflictingProperties(_source, _command);
                return;

            case SOME_UNSET:
                // only one property is set, which is correct.
                break;
        }

        // validate required properties. failonerror defaults to true if not set.
        switch (validateProperties(_failonerror)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        // validate optional properties.
        switch (validateOptionalProperties(_command, _source)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }
    }

    /** Ant adder/setter fields. */
    private final StringProperty _command = new StringProperty("command", this);
    private final FileProperty _source = new FileProperty("source", this);
    private final BooleanProperty _failonerror = new BooleanProperty("failonerror", this, true);
}
