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
package com.threerings.jpkg.ar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.threerings.jpkg.UnixStandardPermissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArchiveTest
{
    @Before
    public void setUp ()
        throws Exception
    {
        _arFile = File.createTempFile("test_archive", ".ar");
        _arFile.deleteOnExit();
    }

    @Test
    public void testOpenGoodArchive ()
        throws Exception
    {
        new Archive(GOOD_ARCHIVE);
    }

    @Test(expected=InvalidMagicException.class)
    public void testOpenBadArchive ()
        throws Exception
    {
        new Archive(BAD_ARCHIVE);
    }

    @Test
    public void testCreateArchiveExistingPath ()
        throws Exception
    {
        InputStream input = null;
        try {
            new Archive(_arFile);
            assertTrue(_arFile.exists());

            input = new FileInputStream(_arFile);
            final byte[] header = new byte[Archive.AR_MAGIC.length];
            input.read(header);
            assertTrue(Arrays.equals(header, Archive.AR_MAGIC));

        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    @Test
    public void testCreateArchiveNonExistingPath ()
        throws Exception
    {
        InputStream input = null;
        try {
            // delete the temp file so that we know the path is valid to create a new archive
            _arFile.delete();
            new Archive(_arFile);
            assertTrue(_arFile.exists());

            input = new FileInputStream(_arFile);
            final byte[] header = new byte[Archive.AR_MAGIC.length];
            input.read(header);
            assertTrue(Arrays.equals(header, Archive.AR_MAGIC));

        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    @Test
    public void testAppendEntry ()
        throws Exception
    {
        InputStream input = null;
        try {
            final Archive archive = new Archive(_arFile);
            final ArchiveEntry entry = new ArchiveStringEntry(APPEND_DATA, "filename.txt");
            archive.appendEntry(entry);

            input = new FileInputStream(_arFile);
            byte[] header = new byte[Archive.AR_MAGIC.length];
            input.read(header);
            assertTrue(Arrays.equals(header, Archive.AR_MAGIC));

            // read the file header and verify it looks correct.
            header = new byte[Archive.FILE_HEADER_LENGTH];
            input.read(header);
            final String[] headerSplit = new String(header).split("\\s+");
            assertEquals("filename.txt", headerSplit[0]);
            assertEquals(UnixStandardPermissions.ROOT_USER.getId(), Integer.parseInt(headerSplit[2]));
            assertEquals(UnixStandardPermissions.ROOT_GROUP.getId(), Integer.parseInt(headerSplit[3]));
            assertEquals(Integer.toOctalString(UnixStandardPermissions.STANDARD_FILE_MODE), (headerSplit[4]));
            assertEquals(String.valueOf(APPEND_DATA.length()), headerSplit[5]);

            // read the file data and verify it looks correct.
            byte[] data = new byte[APPEND_DATA.length()];
            input.read(data);
            assertTrue(Arrays.equals(data, APPEND_DATA.getBytes(Archive.CHAR_ENCODING)));

            // verify that the data padding bit was added.
            data = new byte[1];
            input.read(data);
            assertTrue(Arrays.equals(data, Archive.PADDING));

            // verify that is the end of the archive
            assertEquals(0, input.available());

        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    @Test(expected=PathnameTooLongException.class)
    public void testAppendTooLongPathname ()
        throws Exception
    {
        final Archive archive = new Archive(_arFile);
        final ArchiveEntry entry = new ArchiveStringEntry(APPEND_DATA, "seventeen_letters");
        archive.appendEntry(entry);
    }

    @Test(expected=PathnameInvalidException.class)
    public void testAppendInvalidPathname ()
        throws Exception
    {
        final Archive archive = new Archive(_arFile);
        final ArchiveEntry entry = new ArchiveStringEntry(APPEND_DATA, "test file");
        archive.appendEntry(entry);
    }

    @Test(expected=DataTooLargeException.class)
    public void testAppendTooBigFile ()
        throws Exception
    {
        final Archive archive = new Archive(_arFile);
        archive.appendEntry(TWO_GIG_PLUS_ENTRY);
    }

    @Test
    public void testAppendBiggestFile ()
        throws Exception
    {
        final Archive archive = new Archive(_arFile);
        archive.appendEntry(TWO_GIG_ENTRY);
    }

    /** Test archives. */
    private static final File GOOD_ARCHIVE = new File("src/tests/data/good_archive.ar");
    private static final File BAD_ARCHIVE = new File("src/tests/data/bad_archive.ar");

    /** Data used for append testing. */
    private static final String APPEND_DATA = "New file test data.\nEnd of line.\n";

    /** Mock large files for testing. */
    private static final ArchiveEntry TWO_GIG_ENTRY = new MockArchiveEntry("testfile", Integer.MAX_VALUE);
    private static final ArchiveEntry TWO_GIG_PLUS_ENTRY = new MockArchiveEntry("testfile", (Integer.MAX_VALUE + 1L));

    /** A temporary ar file available to each test which will be deleted when the JVM exits. */
    private File _arFile;
}
