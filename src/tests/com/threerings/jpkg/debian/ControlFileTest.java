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
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.junit.Before;
import org.junit.Test;

import com.threerings.jpkg.MockFile;
import com.threerings.jpkg.PackageTarFile;
import com.threerings.jpkg.TestData;
import com.threerings.jpkg.UnixStandardPermissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ControlFileTest
{
    @Before
    public void setUp ()
        throws Exception
    {
        // attempt to setup the test maintainer scripts
        TEST_PKG.setMaintainerScript(new FileMaintainerScript(
            MaintainerScript.Type.POSTINST, POSTINST_SCRIPT));
        TEST_PKG.setMaintainerScript(new FileMaintainerScript(
            MaintainerScript.Type.PRERM, PRERM_SCRIPT));
    }

    @Test
    public void testControlFile ()
        throws Exception
    {
        TarInputStream input = null;
        PackageTarFile tar = null;
        try {
            tar = new PackageTarFile(TestData.TEMP_DIR);
            tar.addFile(TestData.LARGE_FILE, TestData.DESTROOT);
            tar.close();

            final ControlFile control = new ControlFile(TEST_PKG, tar);
            input = new TarInputStream(new GZIPInputStream(control.getInputStream()));

            // check the control file
            TarEntry entry = input.getNextEntry();
            assertEquals("control", entry.getName());
            byte[] data = new byte[(int)entry.getSize()];
            input.read(data);
            final String header = new String(data);
            final String[] split = header.split("\n");
            // verify there are the expected number of fields
            assertEquals(11, split.length);
            assertEquals("Package: " + TestData.TEST_PKG_NAME, split[0]);
            assertEquals("Version: " + TestData.TEST_PKG_VERSION, split[1]);
            assertEquals("Section: " + TestData.TEST_PKG_SECTION, split[2]);
            assertEquals("Priority: " + TestData.TEST_PKG_PRIORITY.getFieldValue(), split[3]);
            assertEquals("Architecture: " + TestData.TEST_PKG_ARCH, split[4]);
            assertEquals("Depends: " + TestData.TEST_PKG_DEPEND, split[5]);
            assertEquals("Conflicts: " + TestData.TEST_PKG_DEPEND, split[6]);
            assertEquals("Replaces: " + TestData.TEST_PKG_DEPEND, split[7]);
            assertEquals("Maintainer: " + TestData.TEST_PKG_USER + " <" + TestData.TEST_PKG_EMAIL + ">", split[8]);
            assertEquals("Description: " + TestData.TEST_PKG_DESC, split[9]);
            assertEquals("Installed-Size: " + String.valueOf(TestData.LARGE_FILE_SIZE_KBS), split[10]);

            // dpkg insists on a terminating newline
            assertTrue(header.endsWith("\n"));
            assertEquals(UnixStandardPermissions.STANDARD_FILE_MODE, entry.getMode());

            // check the md5sums file
            entry = input.getNextEntry();
            assertEquals("md5sums", entry.getName());
            data = new byte[(int)entry.getSize()];
            input.read(data);
            final String foundSums = new String(data);
            assertTrue(foundSums.contains(TestData.TEST_FILE_NAME + " " + TestData.TEST_FILE_MD5 + "\n"));
            assertEquals(UnixStandardPermissions.STANDARD_FILE_MODE, entry.getMode());

            // check the maintainer scripts
            // postinst script
            entry = input.getNextEntry();
            assertEquals("postinst", entry.getName());
            data = new byte[(int)entry.getSize()];
            input.read(data);
            byte[] fileData = FileUtils.readFileToByteArray(POSTINST_SCRIPT);
            assertTrue(Arrays.equals(fileData, data));
            assertEquals(UnixStandardPermissions.EXECUTABLE_FILE_MODE, entry.getMode());
            // prerm script
            entry = input.getNextEntry();
            assertEquals("prerm", entry.getName());
            data = new byte[(int)entry.getSize()];
            input.read(data);
            fileData = FileUtils.readFileToByteArray(PRERM_SCRIPT);
            assertTrue(Arrays.equals(fileData, data));
            assertEquals(UnixStandardPermissions.EXECUTABLE_FILE_MODE, entry.getMode());

            // verify there are no more entries in the tar file.
            assertNull(input.getNextEntry());

        } finally {
            tar.delete();
            IOUtils.closeQuietly(input);
        }
    }

    @Test(expected=ScriptDataTooLargeException.class)
    public void testTooLargeScriptData ()
        throws Exception
    {
        PackageTarFile tar = null;
        try {
            TEST_PKG.setMaintainerScript(new FileMaintainerScript(
                MaintainerScript.Type.PREINST, new MockFile("testfile", (Integer.MAX_VALUE + 1L))));

            tar = new PackageTarFile(TestData.TEMP_DIR);
            tar.addFile(TestData.LARGE_FILE, TestData.DESTROOT);
            tar.close();

            new ControlFile(TEST_PKG, tar);

        } finally {
            tar.delete();
        }
    }

    private static final PackageInfo TEST_PKG = TestData.testPkgInfo();

    /** Test maintainer scripts. */
    private static final File POSTINST_SCRIPT = new File("src/tests/data/postinst.sh");
    private static final File PRERM_SCRIPT = new File("src/tests/data/prerm.sh");
}

