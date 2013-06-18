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
package com.threerings.jpkg.debian;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackageVersionTest
{
    @Test
    public void testUpstreamVersionOnly ()
        throws Exception
    {
        final PackageVersion version = new PackageVersion("1.1~test412+other");
        assertEquals("1.1~test412+other", version.getFieldValue());
    }

    @Test
    public void testUpstreamVersionOnlySingleDigit ()
        throws Exception
    {
        final PackageVersion version = new PackageVersion("1");
        assertEquals("1", version.getFieldValue());
    }

    @Test(expected=ControlDataInvalidException.class)
    public void testBadUpstreamCharacters ()
        throws Exception
    {
        new PackageVersion("1.1!test");
    }

    @Test(expected=ControlDataInvalidException.class)
    public void testNoColonCharacters ()
        throws Exception
    {
        new PackageVersion("1.1:test");
    }

    @Test(expected=ControlDataInvalidException.class)
    public void testNoDashCharacters ()
        throws Exception
    {
        new PackageVersion("1.1-test");
    }

    @Test(expected=ControlDataInvalidException.class)
    public void testBadUpstreamNoLeadingDigit ()
        throws Exception
    {
        new PackageVersion("a1.1");
    }

    @Test
    public void testAllFields ()
        throws Exception
    {
        final PackageVersion version = new PackageVersion("1.1", "45~5", 2);
        assertEquals("2:1.1-45~5", version.getFieldValue());
    }

    @Test(expected=ControlDataInvalidException.class)
    public void testBadDebianVersion ()
        throws Exception
    {
        new PackageVersion("1.1", "45-5", 2);
    }

    @Test(expected=ControlDataInvalidException.class)
    public void testBadEpoch ()
        throws Exception
    {
        new PackageVersion("1.1", "5", -2);
    }
}
