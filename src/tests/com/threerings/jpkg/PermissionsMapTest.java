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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class PermissionsMapTest
{
    @Test
    public void testPermissionsMap ()
        throws Exception
    {
        final PermissionsMap map = new PermissionsMap();
        map.addPathPermissions("nonexistent/path", NAMED_PERMISSIONS);
        map.addPathPermissions("root/nonexistent/path", ID_PERMISSIONS);
        map.addPathPermissions("other/nonexistent/path", SIMPLE_PERMISSIONS);

        PathPermissions permissions = map.getPathPermissions("nonexistent/path");
        assertNotNull(permissions);
        assertEquals(NAMED_PERMISSIONS.getUser(), permissions.getUser());
        assertEquals(UnixStandardPermissions.ROOT_USER.getId(), permissions.getUid());
        assertFalse(permissions.isRecursive());

        permissions = map.getPathPermissions("root/nonexistent/path");
        assertNotNull(permissions);
        assertEquals(ID_PERMISSIONS.getUid(), permissions.getUid());
        assertEquals(UnixStandardPermissions.ROOT_USER.getName(), permissions.getUser());
        assertTrue(permissions.isRecursive());

        permissions = map.getPathPermissions("other/nonexistent/path");
        assertNotNull(permissions);
        assertEquals(UnixStandardPermissions.ROOT_USER.getName(), permissions.getUser());
        assertEquals(UnixStandardPermissions.ROOT_USER.getId(), permissions.getUid());
        assertFalse(permissions.isRecursive());
    }

    @Test
    public void testDuplicates ()
        throws Exception
    {
        final PermissionsMap map = new PermissionsMap();
        map.addPathPermissions("root/nonexistent/path", ID_PERMISSIONS);
        PathPermissions permissions = map.getPathPermissions("root/nonexistent/path");
        assertNotNull(permissions);
        assertEquals(ID_PERMISSIONS.getUid(), permissions.getUid());
        assertEquals(UnixStandardPermissions.ROOT_USER.getName(), permissions.getUser());

        map.addPathPermissions("root/nonexistent/path", NAMED_PERMISSIONS);
        assertEquals(1, map.getPermissions().size());
        permissions = map.getPathPermissions("root/nonexistent/path");
        assertNotNull(permissions);
        assertEquals(NAMED_PERMISSIONS.getUser(), permissions.getUser());
        assertEquals(UnixStandardPermissions.ROOT_USER.getId(), permissions.getUid());
    }

    @Test
    public void testNormalize ()
        throws Exception
    {
        final PermissionsMap map = new PermissionsMap();
        map.addPathPermissions("//root/nonexistent/path//", NAMED_PERMISSIONS);
        final PathPermissions permissions = map.getPathPermissions("/root/nonexistent/path");
        assertNotNull(permissions);
    }

    /** Test permissions. */
    private static final PathPermissions SIMPLE_PERMISSIONS = new PathPermissions(0755, false);
    private static final PathPermissions NAMED_PERMISSIONS = new PathPermissions("user", "group", 0644, false);
    private static final PathPermissions ID_PERMISSIONS = new PathPermissions(100, 100, 0644, true);
}
