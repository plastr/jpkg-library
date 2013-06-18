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
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.threerings.jpkg.PathPermissions;
import com.threerings.jpkg.PermissionsMap;
import com.threerings.jpkg.TestData;
import com.threerings.jpkg.debian.dependency.PackageConflicts;
import com.threerings.jpkg.debian.dependency.PackageDependencies;
import com.threerings.jpkg.debian.dependency.PackageReplacements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PackageInfoTest
{
    @Test
    public void testPackageInfo ()
        throws Exception
    {
        new PackageInfo(
            new PackageName("testpkg"),
            new PackageVersion("1.1"),
            new PackageArchitecture("i386"),
            TestData.TEST_PKG_MAINTAINER,
            new PackageDescription("Test package."));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullChecks ()
        throws Exception
    {
        new PackageInfo(
            null,
            new PackageVersion("1.1"),
            new PackageArchitecture("i386"),
            TestData.TEST_PKG_MAINTAINER,
            new PackageDescription("Test package."));
    }

    @Test
    public void testNoDependencies ()
        throws Exception
    {
        final PackageDependencies depends = new PackageDependencies();
        final PackageConflicts conflicts = new PackageConflicts();
        final PackageReplacements replaces = new PackageReplacements();

        final PackageInfo info = new PackageInfo(
            new PackageName("testpkg"),
            new PackageVersion("1.1"),
            new PackageArchitecture("i386"),
            TestData.TEST_PKG_MAINTAINER,
            new PackageDescription("Test package."));

        // assert that no field in the package info output matches any dependencies field name.
        assertNull(info.getControlHeaders().getHeader(depends.getField()));
        assertNull(info.getControlHeaders().getHeader(conflicts.getField()));
        assertNull(info.getControlHeaders().getHeader(replaces.getField()));
    }

    @Test
    public void testPermissionsMap ()
        throws Exception
    {
        PermissionsMap map = new PermissionsMap();
        map.addPathPermissions("nonexistent/path", new PathPermissions("user", "group", 0644, false));
        final PackageInfo info = new PackageInfo(
            new PackageName("testpkg"),
            new PackageVersion("1.1"),
            new PackageArchitecture("i386"),
            TestData.TEST_PKG_MAINTAINER,
            new PackageDescription("Test package."));
        info.addPathPermissions("nonexistent/path", new PathPermissions("user", "group", 0644, false));

        map = info.getPermissionsMap();
        assertEquals(1, map.getPermissions().size());
        assertNotNull(map.getPathPermissions("nonexistent/path"));
    }

    @Test
    public void testSingleMaintainerScript ()
        throws Exception
    {
        final PackageInfo info = TestData.testPkgInfo();
        final MaintainerScript postinst =
            new FileMaintainerScript(MaintainerScript.Type.POSTINST, POSTINST_SCRIPT);
        info.setMaintainerScript(postinst);

        assertEquals(1, info.getMaintainerScripts().size());
        assertTrue(IOUtils.contentEquals(
            info.getMaintainerScripts().get(MaintainerScript.Type.POSTINST).getStream(),
            new FileInputStream(POSTINST_SCRIPT)));
    }

    @Test
    public void testRepeatMaintainerScript ()
        throws Exception
    {
        final PackageInfo info = TestData.testPkgInfo();
        MaintainerScript postinst =
            new FileMaintainerScript(MaintainerScript.Type.POSTINST, POSTINST_SCRIPT);
        info.setMaintainerScript(postinst);
        postinst =
            new FileMaintainerScript(MaintainerScript.Type.POSTINST,PRERM_SCRIPT);
        info.setMaintainerScript(postinst);

        assertEquals(1, info.getMaintainerScripts().size());
        assertTrue(IOUtils.contentEquals(
            info.getMaintainerScripts().get(MaintainerScript.Type.POSTINST).getStream(),
            new FileInputStream(PRERM_SCRIPT)));
    }

    @Test
    public void testMultipleMaintainerScripts ()
        throws Exception
    {
        final PackageInfo info = TestData.testPkgInfo();
        MaintainerScript script =
            new FileMaintainerScript(MaintainerScript.Type.POSTINST, POSTINST_SCRIPT);
        info.setMaintainerScript(script);
        script =
            new FileMaintainerScript(MaintainerScript.Type.PRERM, PRERM_SCRIPT);
        info.setMaintainerScript(script);

        assertEquals(2, info.getMaintainerScripts().size());
        assertTrue(IOUtils.contentEquals(
            info.getMaintainerScripts().get(MaintainerScript.Type.POSTINST).getStream(),
            new FileInputStream(POSTINST_SCRIPT)));
        assertTrue(IOUtils.contentEquals(
            info.getMaintainerScripts().get(MaintainerScript.Type.PRERM).getStream(),
            new FileInputStream(PRERM_SCRIPT)));
    }

    /** Test maintainer scripts. */
    private static final File POSTINST_SCRIPT = new File("src/tests/data/postinst.sh");
    private static final File PRERM_SCRIPT = new File("src/tests/data/prerm.sh");
}
