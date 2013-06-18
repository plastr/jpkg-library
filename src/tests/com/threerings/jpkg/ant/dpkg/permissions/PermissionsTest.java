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

import org.junit.Test;

import com.threerings.antidote.AntTestHelper;
import com.threerings.antidote.RequiresValidationException;
import com.threerings.antidote.field.AtLeastOneFieldViolation;
import com.threerings.antidote.field.ConflictingPropertiesViolation;
import com.threerings.antidote.field.UnsetDependentPropertyViolation;
import com.threerings.antidote.property.InvalidBooleanViolation;
import com.threerings.antidote.property.InvalidIntegerViolation;
import com.threerings.antidote.property.UnsetPropertyViolation;
import com.threerings.jpkg.PermissionsMap;
import com.threerings.jpkg.UnixStandardPermissions;

import static com.threerings.antidote.ValidationTestHelper.assertNoViolations;
import static com.threerings.antidote.ValidationTestHelper.assertOneViolation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PermissionsTest extends AntTestHelper
{
    @Test
    public void testGoodPermissions ()
    {
        Permissions permissions = new Permissions();
        Permission permission = MockPermission.createSimplePermission();
        permissions.addPermission(permission);
        assertNoViolations(permissions);
        assertEquals(UnixStandardPermissions.ROOT_USER.getName(), permission.getPathPermissions().getUser());
        assertEquals(UnixStandardPermissions.ROOT_USER.getId(), permission.getPathPermissions().getUid());
        assertEquals(MockPermission.BIN_MODE, Integer.toOctalString(permission.getPathPermissions().getMode()));
        assertEquals(1, permissions.getPermissionsMap(MockPermission.PREFIX).getPermissions().size());

        permissions = new Permissions();
        permission = MockPermission.createNamePermission();
        permissions.addPermission(permission);
        assertNoViolations(permissions);
        assertEquals("user", permission.getPathPermissions().getUser());
        assertEquals(UnixStandardPermissions.ROOT_USER.getId(), permission.getPathPermissions().getUid());
        assertEquals(1, permissions.getPermissionsMap(MockPermission.PREFIX).getPermissions().size());

        permissions = new Permissions();
        permission = MockPermission.createIdPermission();
        permissions.addPermission(permission);
        assertNoViolations(permissions);
        assertEquals(100, permission.getPathPermissions().getUid());
        assertEquals(UnixStandardPermissions.ROOT_USER.getName(), permission.getPathPermissions().getUser());
        assertEquals(1, permissions.getPermissionsMap(MockPermission.PREFIX).getPermissions().size());
    }

    @Test
    public void testBadMode ()
    {
        // <permission user="user" group="group" mode="not_an_int" recursive="true">
        //   <path>logs/</path>
        // </permission>
        Permissions permissions = new Permissions();
        Permission permission = MockPermission.createNamePermission();
        permission.setMode("no_an_int");
        permissions.addPermission(permission);
        assertOneViolation(permissions, InvalidModeViolation.class);

        // <permission user="user" group="group" mode="99" recursive="true">
        //   <path>logs/</path>
        // </permission>
        permissions = new Permissions();
        permission = MockPermission.createNamePermission();
        permission.setMode("99");
        permissions.addPermission(permission);
        assertOneViolation(permissions, InvalidModeViolation.class);
    }

    @Test
    public void testBadRecursive ()
    {
        // <permission user="user" group="group" mode="644" recursive="not_a_boolean">
        //   <path>logs/</path>
        // </permission>
        final Permissions permissions = new Permissions();
        final Permission permission = MockPermission.createNamePermission();
        permission.setRecursive("not_a_boolean");
        permissions.addPermission(permission);
        assertOneViolation(permissions, InvalidBooleanViolation.class);
    }

    @Test
    public void testBadIdData ()
    {
        // <permission userId="not_an_int" groupId="200" mode="755" recursive="false">
        //   <path>bin/</path>
        // </permission>
        Permissions permissions = new Permissions();
        Permission permission = MockPermission.createIdPermission();
        permission.setUserId("not_an_int");
        permissions.addPermission(permission);
        assertOneViolation(permissions, InvalidIntegerViolation.class);

        // <permission userId="100" groupId="not_an_int" mode="755" recursive="false">
        //   <path>bin/</path>
        // </permission>
        permissions = new Permissions();
        permission = MockPermission.createIdPermission();
        permission.setGroupId("not_an_int");
        permissions.addPermission(permission);
        assertOneViolation(permissions, InvalidIntegerViolation.class);
    }

    @Test
    public void testMissingRequiredFields ()
    {
        // <permission user="user" group="group" recursive="true">
        //   <path>logs/</path>
        // </permission>
        final Permissions permissions = new Permissions();
        final Permission permission = MockPermission.createNamePermission();
        permission.setMode(null);
        permissions.addPermission(permission);
        assertOneViolation(permissions, UnsetPropertyViolation.class);
    }

    @Test
    public void testRecursiveProperty ()
    {
        // <permission user="user" group="group" mode="644">
        //   <path>logs/</path>
        // </permission>
        Permissions permissions = new Permissions();
        Permission permission = MockPermission.createNamePermission();
        permission.setRecursive(null);
        permissions.addPermission(permission);
        assertNoViolations(permissions);
        PermissionsMap map = permissions.getPermissionsMap(MockPermission.PREFIX);
        assertFalse(map.getPathPermissions("/root/logs").isRecursive());

        // <permission user="user" group="group" mode="644" recursive="true">
        //   <path>logs/</path>
        // </permission>
        permissions = new Permissions();
        permission = MockPermission.createNamePermission();
        permission.setRecursive("true");
        permissions.addPermission(permission);
        assertNoViolations(permissions);
        map = permissions.getPermissionsMap(MockPermission.PREFIX);
        assertTrue(map.getPathPermissions("/root/logs").isRecursive());
    }

    @Test
    public void testMissingPath ()
    {
        // <permission user="user" group="group" mode="644" recursive="true">
        // </permission>
        final Permissions permissions = new Permissions();
        final Permission permission = MockPermission.createNamePermission(false);
        permissions.addPermission(permission);
        assertOneViolation(permissions, AtLeastOneFieldViolation.class);
    }

    @Test
    public void testDependsProperties ()
    {
        // <permission user="user" mode="644" recursive="true">
        //   <path>logs/</path>
        // </permission>
        Permissions permissions = new Permissions();
        Permission permission = MockPermission.createNamePermission();
        permission.setUser(null);
        permissions.addPermission(permission);
        assertOneViolation(permissions, UnsetDependentPropertyViolation.class);

        // <permission userId="100" mode="755" recursive="false">
        //   <path>bin/</path>
        // </permission>
        permissions = new Permissions();
        permission = MockPermission.createIdPermission();
        permission.setGroupId(null);
        permissions.addPermission(permission);
        assertOneViolation(permissions, UnsetDependentPropertyViolation.class);
    }

    @Test
    public void testConflictsProperties ()
    {
        // <permission user="user" group="group" groupId="200" mode="644" recursive="true">
        //   <path>logs/</path>
        // </permission>
        Permissions permissions = new Permissions();
        Permission permission = MockPermission.createNamePermission();
        permission.setGroupId("200");
        permissions.addPermission(permission);
        assertOneViolation(permissions, ConflictingPropertiesViolation.class);

        // <permission user="user" group="group" userId="100" mode="644" recursive="true">
        //   <path>logs/</path>
        // </permission>
        permissions = new Permissions();
        permission = MockPermission.createNamePermission();
        permission.setUserId("100");
        permissions.addPermission(permission);
        assertOneViolation(permissions, ConflictingPropertiesViolation.class);

        // <permission user="user" userId="100" groupId="200" mode="755" recursive="false">
        //   <path>bin/</path>
        // </permission>
        // the name fields are checked first and determine they do not have a dependent field
        // before the conflict with the id fields can be seen.
        permissions = new Permissions();
        permission = MockPermission.createIdPermission();
        permission.setUser("user");
        permissions.addPermission(permission);
        assertOneViolation(permissions, UnsetDependentPropertyViolation.class);

        // <permission group="group" userId="100" groupId="200" mode="755" recursive="false">
        //   <path>bin/</path>
        // </permission>
        // the name fields are checked first and determine they do not have a dependent field
        // before the conflict with the id fields can be seen.
        permissions = new Permissions();
        permission = MockPermission.createIdPermission();
        permission.setGroup("group");
        permissions.addPermission(permission);
        assertOneViolation(permissions, UnsetDependentPropertyViolation.class);
    }

    @Test
    public void testPathsWithPrefix ()
    {
        final Permissions permissions = new Permissions();
        final Permission absolute = MockPermission.createNamePermission();
        final Permission relative = MockPermission.createIdPermission();
        permissions.addPermission(absolute);
        permissions.addPermission(relative);
        assertNoViolations(permissions);
        final PermissionsMap map = permissions.getPermissionsMap(MockPermission.PREFIX);
        assertEquals(2, map.getPermissions().size());
        assertNotNull(map.getPathPermissions(MockPermission.ABSOLUTE_PATH));
        // the relative path should have had the prefix added.
        assertNotNull(map.getPathPermissions(MockPermission.RELATIVE_PATH_WITH_PREFIX));
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationPermission ()
    {
        final Permission permission = new Permission();
        permission.getPathPermissions();
    }

    @Test(expected=RequiresValidationException.class)
    public void testRequiresValidationPermissionsMap ()
    {
        final Permissions permissions = new Permissions();
        permissions.getPermissionsMap(MockPermission.PREFIX);
    }
}
