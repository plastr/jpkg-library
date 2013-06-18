/*
 * Jpkg - Java library and tools for operating system package creation.
 *
 * Copyright (c) 2007 Three Rings Design, Inc.
 * All rights reserved.
 * Copyright (c) 2004, Regents of the University of California
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

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PathUtilsTest
{
    @Test
    public void testNormalize ()
    {
        assertEquals("/foobar", PathUtils.normalize("/foobar/"));

        assertEquals("foo", PathUtils.normalize("foo"));
    }

    /** Test ignorable /./ path elements */
    @Test
    public void testSingleDot ()
    {
        assertEquals("xyz/foo.txt", PathUtils.normalize("xyz/./foo.txt"));
    }

    /** Test handling of double initial slashes */
    @Test
    public void testDoubleSlash ()
    {
        assertEquals("/file", PathUtils.normalize("//file"));
    }

    /** Test handling of relative paths */
    @Test
    public void relativePath ()
    {
        assertEquals("foo/bar.txt", PathUtils.normalize("./foo/bar.txt"));

        assertEquals("foo/bar.txt", PathUtils.normalize("foo/bar.txt"));

        /* Handling attempted relative traversals is very important for security */
        assertEquals("/foo/bar.txt", PathUtils.normalize("../foo/bar.txt"));

        assertEquals("/foo/bar.txt", PathUtils.normalize("../../foo/bar.txt"));

        assertEquals("/foo/bar.txt", PathUtils.normalize("../.././foo/bar.txt"));

        assertEquals("/foo/bar/test.txt", PathUtils.normalize("../../foo/bar/test.txt"));
    }

    /** Verify that '..' traversal is handled correctly */
    @Test
    public void dotTraversal ()
    {
        assertEquals("/usr/foo/bar.txt", PathUtils.normalize("/usr/tmp/../foo/bar.txt"));

        assertEquals("/usr/foo/bar", PathUtils.normalize("/usr/local/tmp/../../foo/bar/"));

        assertEquals("/usr/.../tmp", PathUtils.normalize("/usr/.../tmp/"));

        assertEquals("/usr/tmp", PathUtils.normalize("/usr/./tmp/"));
    }

    /** Verify that trailing slashes are removed as necessary */
    @Test
    public void trailingSlash ()
    {
        assertEquals("/test", PathUtils.normalize("/test//"));

        assertEquals("/test", PathUtils.normalize("/test/"));

        assertEquals("/test", PathUtils.normalize("/test///"));

        assertEquals("/test", PathUtils.normalize("/test"));
    }

    @Test
    public void testStripLeadingSeparators ()
    {
        assertEquals("", PathUtils.stripLeadingSeparators(""));
        assertEquals("root/dir", PathUtils.stripLeadingSeparators("///root/dir"));
    }
}
