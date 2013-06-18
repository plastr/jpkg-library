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
package com.threerings.jpkg.ant.dpkg.scripts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tools.ant.Location;

import com.threerings.antidote.Violation;
import com.threerings.antidote.field.Field;

public class SimpleValidationScript extends SimpleScript
    implements ValidationScript
{
    public class SimpleViolation extends Violation
    {
        public SimpleViolation (Field field)
        {
            super("Simple violation.", field.getLocation());
        }
    }

    // true will mean the script is valid, false invalid
    public SimpleValidationScript (boolean valid)
    {
        _valid = valid;
    }

    // from Field
    public String getFieldName ()
    {
        return "simplevalidationscript";
    }

    // from Field
    public Location getLocation ()
    {
        return new Location("");
    }

    // from ReferenceField
    public Object getReferencedField ()
    {
        return null;
    }

    // from ReferenceField
    public boolean isReference ()
    {
        return false;
    }

    // from RequiresValidation
    public List<Violation> validate ()
    {
        if (_valid) {
            return Collections.emptyList();

        } else {
            final List<Violation> violations = new ArrayList<Violation>();
            violations.add(new SimpleViolation(this));
            return violations;
        }
    }

    private final boolean _valid;
}
