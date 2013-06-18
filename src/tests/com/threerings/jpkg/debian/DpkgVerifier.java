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
package com.threerings.jpkg.debian;

import java.io.File;

import org.apache.commons.io.IOUtils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Provides unit tests with facilities to validate generated Debian packages against the actual dpkg
 * tools, if available.
 */
public class DpkgVerifier
{
    /**
     * Check the output of -I info looks correct for the supplied package location. Use the supplied
     * packageName as part of the check. Only perform checks if the dpkg tools could be found.
     */
    public static void checkInfo (File dpkg, String packageName)
        throws Exception
    {
        final File dpkgDeb = findDpkgDeb();
        // if the tools cannot be found do nothing.
        if (dpkgDeb == null) {
            return;
        }

        final ProcessBuilder procBuilder = new ProcessBuilder(
            dpkgDeb.getAbsolutePath(),
            "-I", dpkg.getAbsolutePath());
        procBuilder.redirectErrorStream(true);
        final Process proc = procBuilder.start();

        // block the test waiting for the command to exit
        final int exitCode = proc.waitFor();
        final String result = IOUtils.toString(proc.getInputStream());

        if (exitCode > 0) {
            fail("Calling dpkg-deb -I failed on file=[" + dpkg.getAbsolutePath() + "]. output=[" + result + "].");
        }

        assertTrue(result.contains("new debian package, version 2.0"));
        assertTrue(result.contains("Package: " + packageName));
    }

    /**
     * Check the output of -W showformat looks correct for the supplied package location. Use the
     * supplied packageName and packageVersion as part of the check. Only perform checks if the dpkg
     * tools could be found.
     */
    public static void checkFormat (File dpkg, String packageName, String packageVersion)
        throws Exception
    {
        final File dpkgDeb = findDpkgDeb();
        // if the tools cannot be found do nothing.
        if (dpkgDeb == null) {
            return;
        }

        final ProcessBuilder procBuilder = new ProcessBuilder(
            dpkgDeb.getAbsolutePath(),
            "-W", dpkg.getAbsolutePath());
        procBuilder.redirectErrorStream(true);
        final Process proc = procBuilder.start();

        // block the test waiting for the command to exit
        final int exitCode = proc.waitFor();
        final String result = IOUtils.toString(proc.getInputStream());

        if (exitCode > 0) {
            fail("Calling dpkg-deb -W failed on file=[" + dpkg.getAbsolutePath() + "]. output=[" + result + "].");
        }

        // NOTE: the epoch is not shown since it defaults to 0, which dpkg does not show in its
        // output
        assertTrue(result.equals(packageName + "\t" + packageVersion + "\n"));
    }

    /**
     * Determine if any of the known locations for dpkg-deb contain the binary.
     * Returns null if the binary could not be located.
     */
    private static File findDpkgDeb ()
    {
        for (final File path : DPKG_DEB_PATHS) {
            if (path.exists() && path.isFile()) {
                return path;
            }
        }
        return null;
    }

    /**
     * The list of possible paths to dpkg-deb for optional extra testing.
     * If dpkg-deb is not found, it will not be used in the tests.
     */
    private static final File[] DPKG_DEB_PATHS = {
        new File("/usr/local/bin/dpkg-deb"),
        new File("/opt/local/bin/dpkg-deb"),
        new File("/usr/bin/dpkg-deb") };
}
