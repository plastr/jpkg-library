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

import org.apache.commons.io.FilenameUtils;

import com.threerings.antidote.field.ListField;
import com.threerings.jpkg.PathUtils;
import com.threerings.jpkg.PermissionsMap;


/**
 * The &lt;dpkg&gt; task &lt;package&gt; &lt;permissions&gt; field. Used to add package permission maps.
 * @see com.threerings.jpkg.PathPermissions
 * @see com.threerings.jpkg.PermissionsMap
 */
public class Permissions extends ListField<Permission>
{
    // from Field
    public String getFieldName ()
    {
        return "permissions";
    }

    @Override // from ListField
    public String getChildFieldName ()
    {
        return "permission";
    }

    /**
     * Ant adder field: Add a {@link Permission}.
     */
    public void addPermission (Permission permission)
    {
        appendRequiresValidation(permission);
    }

    /**
     * Return the list of &lt;permission&gt; fields defined in this section.
     */
    public PermissionsMap getPermissionsMap (String prefix)
    {
        final PermissionsMap map = new PermissionsMap();
        for (final Permission permission : getValidatedFieldList()) {
            for (final Path pathField : permission.getPaths()) {
                final String path = pathField.getPath();

                // if the path is absolute, starts with /, then add the path unmodified to the
                // permissionsmap
                if (path.startsWith("/")) {
                    map.addPathPermissions(path, permission.getPathPermissions());

                    // if the path is relative, does not start with /, then prepend the path with
                    // the prefix, and then add it to the permissionsmap.
                } else {
                    final String pathWithPrefix = FilenameUtils.concat(prefix, PathUtils.normalize(path));
                    map.addPathPermissions(pathWithPrefix, permission.getPathPermissions());
                }
            }
        }
        return map;
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        if (noChildFieldsDefined()) {
            return;
        }

        switch (validateFieldList()) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;
            case ALL_VALID:
                break;
        }
    }
}
