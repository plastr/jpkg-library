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

public class PackageDescriptionTest
{
    @Test
    public void testValidShortDescription ()
        throws Exception
    {
        final PackageDescription description = new PackageDescription("This is a valid description.");
        assertEquals("This is a valid description.", description.getFieldValue());
    }

    @Test(expected=ControlDataInvalidException.class)
    public void testInvalidShortDescription ()
        throws Exception
    {
        new PackageDescription("This is an\tinvalid description.\n");
    }

    @Test
    public void testValidExtendedDescription ()
        throws Exception
    {
        final PackageDescription description = new PackageDescription("Valid description.");
        description.addParagraph("New paragraph of text.");
        description.addVerbatimParagraph("New verbatim line.");
        assertEquals("Valid description.\n New paragraph of text.\n  New verbatim line.", description.getFieldValue());
    }

    @Test(expected=ControlDataInvalidException.class)
    public void testInvalidExtendedDescription ()
        throws Exception
    {
        final PackageDescription description = new PackageDescription("Valid description.");
        description.addParagraph("New paragraph\tof text.");
    }

    @Test
    public void testAddBlankLine ()
        throws Exception
    {
        final PackageDescription description = new PackageDescription("Valid description.");
        description.addParagraph("New paragraph of text.");
        description.addBlankLine();
        assertEquals("Valid description.\n New paragraph of text.\n .", description.getFieldValue());
    }
}
