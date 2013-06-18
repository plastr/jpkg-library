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
package com.threerings.jpkg.ant.dpkg.permissions;

import org.apache.tools.ant.Project;

public class MockPermission
{
    public static final String BIN_MODE = "755";

    public static final String PREFIX = "/usr/local";

    public static final String ABSOLUTE_PATH = "/root/logs";
    public static final String RELATIVE_PATH = "bin";
    public static final String RELATIVE_PATH_WITH_PREFIX = PREFIX + "/" + RELATIVE_PATH;

    // <permission user="user" group="group" mode="644" recursive="true">
    //   <path>/root/logs</path>
    // </permission>
    public static Permission createNamePermission ()
    {
        return createNamePermission(true);
    }

    // optional flag as to whether a <path> field will be added.
    public static Permission createNamePermission (boolean withPath)
    {
        final Project project = new Project();
        // init() depends on ant-launcher.jar being in the classpath
        project.init();
        final Permission permission = new Permission();
        permission.setProject(project);
        permission.setUser("user");
        permission.setGroup("group");
        permission.setMode("644");
        permission.setRecursive("true");
        if (withPath) {
            final Path path = new Path();
            path.setProject(project);
            path.addText(ABSOLUTE_PATH);
            permission.addPath(path);
        }
        return permission;
    }

    // <permission userId="100" groupId="200" mode="755" recursive="false">
    //   <path>bin/</path>
    // </permission>
    public static Permission createIdPermission ()
    {
        final Project project = new Project();
        // init() depends on ant-launcher.jar being in the classpath
        project.init();
        final Permission permission = new Permission();
        permission.setProject(project);
        permission.setUserId("100");
        permission.setGroupId("200");
        permission.setMode(BIN_MODE);
        permission.setRecursive("false");
        final Path path = new Path();
        path.setProject(project);
        path.addText(RELATIVE_PATH);
        permission.addPath(path);
        return permission;
    }

    // <permission mode="755" recursive="false">
    //   <path>bin</path>
    // </permission>
    public static Permission createSimplePermission ()
    {
        final Project project = new Project();
        // init() depends on ant-launcher.jar being in the classpath
        project.init();
        final Permission permission = new Permission();
        permission.setProject(project);
        permission.setMode(BIN_MODE);
        permission.setRecursive("false");
        final Path path = new Path();
        path.setProject(project);
        path.addText(RELATIVE_PATH);
        permission.addPath(path);
        return permission;
    }
}
