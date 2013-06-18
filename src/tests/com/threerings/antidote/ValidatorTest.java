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
package com.threerings.antidote;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.junit.Test;
import org.xml.sax.Locator;

import com.threerings.antidote.RequiresValidation;
import com.threerings.antidote.Validator;
import com.threerings.antidote.Violation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ValidatorTest
{
    @Test
    public void testValidateAll ()
    {
        final Validator validator = new Validator();
        final TestRequiresValidation requires = new TestRequiresValidation();
        requires.setField("Test");
        validator.addValidation(requires);
        validator.validateAll();
        // we are expecting no exception to be thrown
    }

    @Test
    public void testValidateAllException ()
    {
        final Validator validator = new Validator();
        final TestRequiresValidation requires = new TestRequiresValidation();
        validator.addValidation(requires);

        BuildException exception = null;
        try {
            validator.validateAll();

        } catch (final BuildException be) {
            exception = be;
        }
        assertNotNull(exception);
        final String expected = TestRequiresValidation.LOCATION + TestRequiresValidation.VIOLATION;
        assertTrue(exception.getMessage().contains(expected));
    }

    private static class TestRequiresValidation
        implements RequiresValidation
    {
        public static final String VIOLATION = "The field was not set.";
        public static final Location LOCATION = new Location(new Locator () {
            public int getColumnNumber ()
            {
                return 20;
            }

            public int getLineNumber ()
            {
                return 101;
            }

            public String getPublicId ()
            {
                return "/test/file";
            }

            public String getSystemId ()
            {
                return "/test/file";
            }

        });

        public void setField (String field)
        {
            _field = field;
        }

        public List<Violation> validate ()
        {
            final List<Violation> violations = new ArrayList<Violation>();
            if (_field == null) {
                violations.add(new Violation(VIOLATION, LOCATION));
            }
            return violations;
        }

        private String _field;
    }
}
