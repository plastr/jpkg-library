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
package com.threerings.jpkg.ant.dpkg;

import org.junit.Test;

import com.threerings.antidote.AntTestHelper;
import com.threerings.antidote.RequiresValidationException;
import com.threerings.antidote.field.OnlyOneFieldViolation;
import com.threerings.antidote.field.UnsetFieldViolation;
import com.threerings.antidote.property.UnsetPropertyViolation;
import com.threerings.jpkg.PermissionsMap;
import com.threerings.jpkg.ant.dpkg.dependencies.Dependencies;
import com.threerings.jpkg.ant.dpkg.dependencies.Require;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.Condition;
import com.threerings.jpkg.ant.dpkg.dependencies.conditions.EqualTo;
import com.threerings.jpkg.ant.dpkg.info.MockInfo;
import com.threerings.jpkg.ant.dpkg.permissions.MockPermission;
import com.threerings.jpkg.ant.dpkg.permissions.Permission;
import com.threerings.jpkg.ant.dpkg.permissions.Permissions;
import com.threerings.jpkg.ant.dpkg.scripts.Scripts;
import com.threerings.jpkg.ant.dpkg.scripts.SimpleValidationScript;
import com.threerings.jpkg.debian.PackageInfo;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PackageTest extends AntTestHelper
{
    @Test
    public void testGoodInfo ()
    {
        // <package>
        //   <info>...</info>
        // </package>
        final Package pkg = new MockPackage();

        assertNoViolations(pkg);
        assertEquals(MockInfo.OUTPUT_NAME, pkg.getFilename());
    }

    @Test
    public void testSetFilename ()
    {
        // <package filename="value">
        //   <info>...</info>
        // </package>
        final Package pkg = new MockPackage(MockPackage.FILENAME);

        assertNoViolations(pkg);
        assertEquals(MockPackage.FILENAME, pkg.getFilename());
    }

    @Test
    public void testUnsetInfo ()
    {
        // <package>
        // </package>
        final Package pkg = new MockPackage(false);
        assertOneViolation(pkg, UnsetFieldViolation.class);
    }

    @Test
    public void testTooManyInfoFields ()
    {
        // <package>
        //  <info>...</info>
        //  <info>...</info>
        // </package>
        final Package pkg = new MockPackage();
        final MockInfo info = MockInfo.generateValidInfo();
        info.setProject(pkg.getProject());
        info.populateFields();
        pkg.addInfo(info);

        assertOneViolation(pkg, OnlyOneFieldViolation.class);
    }

    @Test
    public void testMissingProperties ()
    {
        final Package pkg = new MockPackage();
        pkg.setDestroot(null);
        assertOneViolation(pkg, UnsetPropertyViolation.class);
    }

    @Test
    public void testAddScriptsSection ()
    {
        // <package>
        //  <info>...</info>
        //  <scripts>
        //    <simplescript/>
        //  </scripts>
        // </package>
        final Package pkg = new MockPackage();

        final Scripts scripts = new Scripts();
        scripts.setProject(pkg.getProject());
        final SimpleValidationScript simple = new SimpleValidationScript(true);
        scripts.add(simple);

        pkg.addScripts(scripts);

        assertNoViolations(pkg);
    }

    @Test
    public void testAddPermissionsSection ()
    {
        // <package>
        //  <info>...</info>
        //  <permission user="user" group="group" mode="644" recursive="true">
        //    <path>bin/</path>
        //  </permission>
        // </package>
        final Package pkg = new MockPackage();

        final Permission permission = MockPermission.createNamePermission();
        final Permissions permissions = new Permissions();
        permissions.setProject(pkg.getProject());
        permissions.addPermission(permission);

        pkg.addPermissions(permissions);

        assertNoViolations(pkg);
    }

    @Test
    public void testAddDependenciesSection ()
    {
        // <package>
        //  <info>...</info>
        //  <dependencies>
        //    <require package="packagename">
        //      <equalTo>1.4</equalTo>
        //    </require>
        //  </dependencies>
        // </package>
        final Package pkg = new MockPackage();

        final Dependencies dependencies = new Dependencies();
        dependencies.setProject(pkg.getProject());
        final Condition condition = new EqualTo();
        condition.setProject(pkg.getProject());
        condition.addText("1.4");
        final Require require = new Require();
        require.setPackage("packagename");
        require.add(condition);
        dependencies.add(require);

        pkg.addDependencies(dependencies);

        assertNoViolations(pkg);
    }

    @Test
    public void testRelativePaths ()
    {
        // <package>
        //  <info>...</info>
        //  <permissions>
        //     <permission user="user" group="group" mode="644" recursive="true">
        //       <path>/root/logs/</path>
        //     </permission>
        //     <permission mode="755" recursive="false">
        //       <path>bin/</path>
        //     </permission>
        //  </permissions>
        // </package>
        final Package pkg = new MockPackage();
        final Permissions permissions = new Permissions();
        Permission permission = MockPermission.createNamePermission();
        permissions.addPermission(permission);
        permission = MockPermission.createSimplePermission();
        permissions.addPermission(permission);
        pkg.addPermissions(permissions);
        assertNoViolations(pkg);

        final PackageInfo info = pkg.createPackageInfo(DISTRIBUTION, PREFIX);
        final PermissionsMap map = info.getPermissionsMap();
        assertEquals(2, map.getPermissions().size());

        // the permission with the absolute path should be at the same location in the map
        assertNotNull(map.getPathPermissions(MockPermission.ABSOLUTE_PATH));
        // the permission with the relative path should have the prefix appended.
        assertNotNull(map.getPathPermissions(MockPermission.RELATIVE_PATH_WITH_PREFIX));
    }

    @Test(expected=RequiresValidationException.class)
    public void requiresValidationPackageInfo ()
    {
        final Package pkg = new MockPackage();
        pkg.createPackageInfo(DISTRIBUTION, PREFIX);
    }

    @Test(expected=RequiresValidationException.class)
    public void requiresValidationFilename ()
    {
        final Package pkg = new MockPackage();
        pkg.getFilename();
    }

    @Test(expected=RequiresValidationException.class)
    public void requiresValidationDestroot ()
    {
        final Package pkg = new MockPackage();
        pkg.getDestroot();
    }

    private static final String DISTRIBUTION = "unstable";
    private static final String PREFIX = "/usr/local";
}
