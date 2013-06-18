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

import com.threerings.jpkg.ant.dpkg.scripts.runner.PackageScript;

/**
 * A data class providing {@link PackageScript} objects meta information about the package
 * being created.
 */
public class DpkgData
{
    public DpkgData (String packageName, String packageVersion, String distribution, String prefix)
    {
        _packageName = packageName;
        _packageVersion = packageVersion;
        _distribution = distribution;
        _prefix = prefix;
    }

    /**
     * Returns the name of the package.
     */
    public String packageName ()
    {
        return _packageName;
    }

    /**
     * Returns the version of the package.
     */
    public String packageVersion ()
    {
        return _packageVersion;
    }

    /**
     * Returns the APT distribution the package is meant for.
     */
    public String distribution ()
    {
        return _distribution;
    }

    /**
     * Returns the filesystem prefix the package is being installed into.
     */
    public String prefix ()
    {
        return _prefix;
    }

    private final String _packageName;
    private final String _packageVersion;
    private final String _distribution;
    private final String _prefix;
}
