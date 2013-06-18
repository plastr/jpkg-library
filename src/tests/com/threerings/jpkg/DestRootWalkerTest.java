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
package com.threerings.jpkg;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarInputStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DestRootWalkerTest extends TestTarFile
{
    @Test
    public void testWalkDestRoot ()
        throws Exception
    {
        final File destroot = dummyDestroot();

        final PackageTarFile tar = new PackageTarFile(TestData.TEMP_DIR, new PermissionsMap());
        try {
            final DestrootWalker walker = new DestrootWalker(destroot, tar);
            walker.walk();
            tar.close();

            TarInputStream input = null;
            try {
                input = getTarInput(tar);

                assertEquals(ROOT_DIR, input.getNextEntry().getName());
                assertEquals(ROOT_DIR_FILE, input.getNextEntry().getName());
                assertEquals(ROOT_FILE, input.getNextEntry().getName());

                // verify there are no more entries in the tar file.
                assertNull(input.getNextEntry());

            } finally {
                IOUtils.closeQuietly(input);
            }

        } finally {
            tar.delete();
            FileUtils.deleteDirectory(destroot);
        }
    }

    /**
     * Dummy up a destroot instead of using a tree full of .svn entries.
     */
    private File dummyDestroot ()
        throws Exception
    {
        final File destroot = File.createTempFile("jpkgtest", "destroot");
        destroot.delete();
        destroot.mkdir();
        final String destrootPath = destroot.getAbsolutePath();
        FileUtils.touch(new File(destrootPath, ROOT_FILE));
        final File rootDir = new File(destrootPath, ROOT_DIR);
        rootDir.mkdir();
        FileUtils.touch(new File(destrootPath, ROOT_DIR_FILE));
        return destroot;
    }

    /** Test files and directories. */
    public static final String ROOT_FILE = "file.txt";
    public static final String ROOT_DIR = "directory/";
    public static final String ROOT_DIR_FILE = ROOT_DIR + "file.txt";
}
