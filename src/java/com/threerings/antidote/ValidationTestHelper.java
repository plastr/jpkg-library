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
package com.threerings.antidote;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A static helper utility for dealing with {@link RequiresValidation} objects in unit tests.
 */
public class ValidationTestHelper
{
    /**
     * Assert that the given {@link RequiresValidation} object has no violations.
     */
    public static void assertNoViolations (RequiresValidation validation)
    {
        final List<Violation> violations = validation.validate();
        assertEquals("Unexpected number of violations,", 0, violations.size());
    }

    /**
     * Assert that the given {@link RequiresValidation} object has one violation and is an instance
     * of the supplied {@link Violation} class.
     */
    public static void assertOneViolation (RequiresValidation validation, Class<? extends Violation> clazz)
    {
        final List<Violation> violations = validation.validate();
        assertViolationCount(violations, 1);

        assertViolationClass(violations.get(0), clazz);
    }

    /**
     * Assert that the given {@link RequiresValidation} object has two violations and are instances
     * of the supplied {@link Violation} class.
     */
    public static void assertTwoViolations (RequiresValidation validation, Class<? extends Violation> clazz)
    {
        assertTwoViolations(validation, clazz, clazz);
    }

    /**
     * Assert that the given {@link RequiresValidation} object has two violations and are instances
     * of the supplied {@link Violation} classes.
     */
    public static void assertTwoViolations (RequiresValidation validation,
        Class<? extends Violation> one, Class<? extends Violation> two)
    {
        final List<Violation> violations = validation.validate();
        assertViolationCount(violations, 2);

        assertViolationClass(violations.get(0), one);
        assertViolationClass(violations.get(1), two);
    }

    /**
     * Validate the supplied violation is a subclass of the supplied class.
     */
    private static void assertViolationCount (List<Violation> violations, int count)
    {
        assertEquals("Unexpected number of Violations,", count, violations.size());
    }

    /**
     * Validate the supplied violation is a subclass of the supplied class.
     */
    private static void assertViolationClass (Violation violation, Class<? extends Violation> clazz)
    {
        if (!(clazz.isAssignableFrom(violation.getClass()))) {
            fail("Incorrect Violation class. expected: " + clazz.getName() + " found: " + violation.getClass().getName());
        }
    }
}
