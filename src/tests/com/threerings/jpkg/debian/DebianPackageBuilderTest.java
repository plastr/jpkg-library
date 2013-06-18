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

import java.io.File;

import org.junit.Test;

import com.threerings.jpkg.PackageBuilder;
import com.threerings.jpkg.TestData;

import static org.junit.Assert.assertTrue;

public class DebianPackageBuilderTest
{
    @Test
    public void testDebianPackageBuilder ()
    {
        new DebianPackageBuilder(TEST_PKG);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullChecks ()
        throws Exception
    {
        final PackageBuilder builder = new DebianPackageBuilder(TEST_PKG);
        builder.write(null, DESTROOT);
    }

    @Test
    public void testWrite ()
        throws Exception
    {
        final PackageBuilder builder = new DebianPackageBuilder(TEST_PKG);
        final File dpkg = File.createTempFile("jpkgtest", ".dpkg");
        dpkg.deleteOnExit();
        builder.write(dpkg, DESTROOT);
        assertTrue(dpkg.length() > 0);

        DpkgVerifier.checkInfo(dpkg, TestData.TEST_PKG_NAME);
        DpkgVerifier.checkFormat(dpkg, TestData.TEST_PKG_NAME, TestData.TEST_PKG_VERSION);
    }

    private static final File DESTROOT = new File("src/tests/data/package_destroot");

    private static final PackageInfo TEST_PKG = TestData.testPkgInfo();
}
