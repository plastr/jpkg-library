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
package com.threerings.jpkg.ant.dpkg;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import com.threerings.antidote.AntTestHelper;
import com.threerings.antidote.field.AtLeastOneFieldViolation;
import com.threerings.antidote.property.UnsetPropertyViolation;
import com.threerings.jpkg.ant.dpkg.info.MockInfo;
import com.threerings.jpkg.debian.DpkgVerifier;

import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;

import static org.junit.Assert.assertTrue;

public class DpkgTest extends AntTestHelper
{
    @Test
    public void testAddPackage ()
        throws Exception
    {
        MockDpkg dpkg = null;
        try {
            dpkg = new MockDpkg();
            dpkg.execute();

            final File outputPkg = new File(FilenameUtils.concat(dpkg.getOutput().getAbsolutePath(), MockPackage.FILENAME));
            assertTrue(outputPkg.exists());
            assertTrue(outputPkg.length() > 0);

            // perform additional dpkg tool checking if available.
            DpkgVerifier.checkInfo(outputPkg, MockInfo.PACKAGENAME);
            DpkgVerifier.checkFormat(outputPkg,  MockInfo.PACKAGENAME, MockInfo.PACKAGEVERSION);

        } finally {
            dpkg.deleteOutput();
        }
    }

    @Test
    public void testMissingPackage ()
        throws Exception
    {
        // <dpkg>
        // </dpkg>
        MockDpkg dpkg = null;
        try {
            dpkg = new MockDpkg(false);

            assertOneViolation(dpkg, AtLeastOneFieldViolation.class);

        } finally {
            dpkg.deleteOutput();
        }
    }

    @Test
    public void testMissingOutput ()
        throws Exception
    {
        MockDpkg dpkg = null;
        try {
            dpkg = new MockDpkg();
            dpkg.setOutput(null);
            assertOneViolation(dpkg, UnsetPropertyViolation.class);

        } finally {
            dpkg.deleteOutput();
        }
    }

    @Test
    public void testMissingDistribution ()
        throws Exception
    {
        MockDpkg dpkg = null;
        try {
            dpkg = new MockDpkg();
            dpkg.setDistribution(null);
            assertOneViolation(dpkg, UnsetPropertyViolation.class);

        } finally {
            dpkg.deleteOutput();
        }
    }

    @Test
    public void testMissingPrefix ()
        throws Exception
    {
        MockDpkg dpkg = null;
        try {
            dpkg = new MockDpkg();
            dpkg.setPrefix(null);
            assertOneViolation(dpkg, UnsetPropertyViolation.class);

        } finally {
            dpkg.deleteOutput();
        }
    }
}
