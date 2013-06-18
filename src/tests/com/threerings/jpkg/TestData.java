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

import com.threerings.jpkg.debian.ControlDataInvalidException;
import com.threerings.jpkg.debian.PackageArchitecture;
import com.threerings.jpkg.debian.PackageDescription;
import com.threerings.jpkg.debian.PackageInfo;
import com.threerings.jpkg.debian.PackageMaintainer;
import com.threerings.jpkg.debian.PackageName;
import com.threerings.jpkg.debian.PackagePriority;
import com.threerings.jpkg.debian.PackageSection;
import com.threerings.jpkg.debian.PackageVersion;
import com.threerings.jpkg.debian.dependency.PackageConflict;
import com.threerings.jpkg.debian.dependency.PackageDependency;
import com.threerings.jpkg.debian.dependency.PackageReplacement;

/**
 * Useful bits for unit testing.
 */
public class TestData
{
    public static final String TEST_PKG_NAME = "testpkg";
    public static final String TEST_PKG_VERSION = "1.1";
    public static final String TEST_PKG_ARCH = "i386";
    public static final String TEST_PKG_DESC = "Test package.";
    public static final String TEST_PKG_SECTION = "web";
    public static final PackagePriority TEST_PKG_PRIORITY = PackagePriority.EXTRA;
    public static final String TEST_PKG_DEPEND = "otherpkg";

    public static final String TEST_PKG_USER = "Test user";
    public static final String TEST_PKG_EMAIL = "test@test.com";
    public static final PackageMaintainer TEST_PKG_MAINTAINER;

    /** Locations for various testfiles and paths. */
    public static final String DESTROOT = new File("src/tests/data/package_destroot").getAbsolutePath();
    public static final String TEST_FILE_NAME = "file.txt";
    public static final File TEST_FILE = new File(DESTROOT, TEST_FILE_NAME);
    public static final String TEST_FILE_MD5 = "5228465a3ee0e7630c6748d16ee9dc00";

    /** Mock large files for testing. */
    public static final long LARGE_FILE_SIZE_KBS = 2201;
    public static final long LARGE_FILE_SIZE_BYTES = FileUtils.ONE_KB * LARGE_FILE_SIZE_KBS;
    public static final File LARGE_FILE = new MockFile(TEST_FILE.getAbsolutePath(), LARGE_FILE_SIZE_BYTES);

    /** The system temp directory in a handy constant. */
    public static final File TEMP_DIR = new File(System.getProperty("java.io.tmpdir"));

    public static final PackageInfo testPkgInfo ()
    {
        PackageInfo info = null;
        try {
            info = new PackageInfo(
                new PackageName(TEST_PKG_NAME),
                new PackageVersion(TEST_PKG_VERSION),
                new PackageArchitecture(TEST_PKG_ARCH),
                TEST_PKG_MAINTAINER,
                new PackageDescription(TEST_PKG_DESC),
                new PackageSection(TEST_PKG_SECTION),
                TEST_PKG_PRIORITY);

        } catch (final ControlDataInvalidException cdi) {
            throw new RuntimeException("Test package data was unexpectedly invalid. Tests will fail.");
        }
        info.addDependency(new PackageDependency(TEST_PKG_DEPEND));
        info.addConflict(new PackageConflict(TEST_PKG_DEPEND));
        info.addReplacement(new PackageReplacement(TEST_PKG_DEPEND));
        return info;
    }

    static {
        try {
            TEST_PKG_MAINTAINER = new PackageMaintainer(TEST_PKG_USER, TEST_PKG_EMAIL);

        } catch (final ControlDataInvalidException cde) {
            throw new RuntimeException("Unable to setup test maintainer. ", cde);
        }
    }
}
