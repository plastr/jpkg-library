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
package com.threerings.jpkg.ant.dpkg;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.Project;

public class MockDpkg extends Dpkg
{
    public static final String PREFIX = "/usr/local";
    public static final String DISTRIBUTION = "unstable";

    // <dpkg output="/tmplocation" prefix="/usr/local" distribution="unstable">
    //   <package destroot="src/tests/data/package_destroot/" filename="testpkg.dpkg">...</package>
    // </dpkg>
    public MockDpkg ()
        throws IOException
    {
        this(true);
    }

    // optionally skip a <package> section
    public MockDpkg (boolean withPackage)
        throws IOException
    {
        final Project project = new Project();
        // init() depends on ant-launcher.jar being in the classpath
        project.init();
        setProject(project);

        _output = File.createTempFile("dpkgtest", "output");
        _output.delete();
        _output.mkdir();

        setOutput(_output.getAbsolutePath());
        setPrefix(PREFIX);
        setDistribution(DISTRIBUTION);

        if (withPackage) {
            final Package pkg = new MockPackage(MockPackage.FILENAME);
            addPackage(pkg);
        }
    }

    /**
     * Returns the output directory for this mock object.
     */
    public File getOutput ()
    {
        return _output;
    }

    /**
     * Call to cleanup the output location for this Dpkg object.
     */
    public void deleteOutput ()
        throws IOException
    {
        FileUtils.deleteDirectory(_output);
    }

    private final File _output;
}
