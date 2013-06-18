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
package com.threerings.jpkg.ant.dpkg.scripts.standard;

import com.threerings.antidote.property.StringProperty;
import com.threerings.jpkg.ant.dpkg.scripts.TemplateScript;
import com.threerings.jpkg.debian.MaintainerScript.Type;

public class HelloWorld extends TemplateScript
{
    public HelloWorld ()
    {
        super(Type.POSTINST);
    }

    // from Field
    public String getFieldName ()
    {
        return "helloworld";
    }

    // from PackageScript
    public String getFriendlyName ()
    {
        return "Hello world script";
    }

    // from PackageScript
    public boolean failOnError ()
    {
        return true;
    }

    // from VelocityTemplate
    public String getTemplateName ()
    {
        return "scripts/helloworld.vm";
    }

    /**
     * Ant setter field: world.
     */
    public void setWorld (String value)
    {
        _world.setValue(value);
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        switch (validateProperties(_world)) {
            case ALL_INVALID:
            case SOME_INVALID:
                break;

            case ALL_VALID:
                addSubstitution(_world.getPropertyName(), _world.getValue());
                return;
        }
    }

    /** Ant adder/setter fields. */
    private final StringProperty _world = new StringProperty("world", this);
}
