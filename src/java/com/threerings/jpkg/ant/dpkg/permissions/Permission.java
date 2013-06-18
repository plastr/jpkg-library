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

import java.util.List;

import com.threerings.antidote.SetStatus;
import com.threerings.antidote.field.ListField;
import com.threerings.antidote.property.BooleanProperty;
import com.threerings.antidote.property.IntegerProperty;
import com.threerings.antidote.property.StringProperty;
import com.threerings.jpkg.PathPermissions;

import static com.threerings.antidote.MutabilityHelper.areMutablesSet;
import static com.threerings.antidote.MutabilityHelper.objectIsNotSet;
import static com.threerings.antidote.MutabilityHelper.requiresValidation;


/**
 * The &lt;permission&gt; child field type for the &lt;dpkg&gt; &lt;permissions&gt; field. Describes permissions
 * for the permissions map.
 * @see PathPermissions
 */
public class Permission extends ListField<Path>
{
    // from Field
    public String getFieldName ()
    {
        return "permission";
    }

    @Override // from ListField
    public String getChildFieldName ()
    {
        return "path";
    }

    /**
     * Ant setter field: user.
     */
    public void setUser (String value)
    {
        _user.setValue(value);
    }

    /**
     * Ant setter field: group.
     */
    public void setGroup (String value)
    {
        _group.setValue(value);
    }

    /**
     * Ant setter field: userId.
     */
    public void setUserId (String value)
    {
        _userId.setValue(value);
    }

    /**
     * Ant setter field: groupId.
     */
    public void setGroupId (String value)
    {
        _groupId.setValue(value);
    }

    /**
     * Ant setter field: mode.
     */
    public void setMode (String value)
    {
        _mode.setValue(value);
    }

    /**
     * Ant setter field: recursive.
     */
    public void setRecursive (String value)
    {
        _recursive.setValue(value);
    }

    /**
     * Ant adder field: Add a {@link Path}.
     */
    public void addPath (Path path)
    {
        appendRequiresValidation(path);
    }

    /**
     * Return the user supplied fields as a {@link PathPermissions} object.
     */
    public PathPermissions getPathPermissions ()
    {
        requiresValidation(_pathPermissions);
        return _pathPermissions;
    }

    /**
     * Return the list of Paths declared for this Permission.
     */
    public List<Path> getPaths ()
    {
        requiresValidation(_validatedPaths);
        return _validatedPaths;
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        // validate the required properties. recursive defaults to false if not set.
        switch (validateProperties(_mode, _recursive)) {
            case ALL_INVALID:
            case SOME_INVALID:
                // fail now if required properties are not valid otherwise broken PathPermissions
                // objects will be constructed
                return;

            case ALL_VALID:
                break;
        }

        // if none of the user/group/userId/groupId fields have been set, then construct
        // a mode only permission object.
        if (areMutablesSet(_user, _group, _userId, _groupId) == SetStatus.ALL_UNSET) {
            _pathPermissions = new PathPermissions(_mode.getValue(), _recursive.getValue());

        } else {
            // attempt to set the path permission object from the permission name fields
            switch (areMutablesSet(_user, _group)) {
            case ALL_UNSET:
                // none of the name fields were set, attempt to set the PathPermissions object
                // from the id fields
                _pathPermissions = validateIdFields(_mode.getValue(), _recursive.getValue());
                break;

            case SOME_UNSET:
                // only some of the name fields were set, validate but we will not get a path
                // permissions object so fail now
                validateNameFields(_mode.getValue(), _recursive.getValue());
                return;

            case ALL_SET:
                // all of the name fields were set, we should have a PathPermissions object now.
                _pathPermissions = validateNameFields(_mode.getValue(), _recursive.getValue());
                break;
            }
        }

        // if we still don't have a PathPermissions object, fail now
        if (objectIsNotSet(_pathPermissions)) {
            return;
        }

        // if we have no <path> child fields defined, fail now
        if (noChildFieldsDefined()) {
            return;
        }

        // validate the child <path> fields
        switch(validateFieldList()) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                 _validatedPaths = getValidatedFieldList();
                break;
        }
    }

    /**
     * Validate the user and group fields. Returns a PathPermissions object if one was able
     * to be created, null otherwise..
     */
    private PathPermissions validateNameFields (Integer mode, Boolean recursive)
    {
        switch (areMutablesSet(_user, _group)) {
            case ALL_UNSET:
                return null;

            case SOME_UNSET:
                reportUnsetDependentProperties(_user, _group);
                reportUnsetDependentProperties(_group, _user);
                return null;

            case ALL_SET:
                // report violations if any of the id fields are set
                reportConflictingProperties(_userId, _user, _group);
                reportConflictingProperties(_groupId, _user, _group);

                // continue to validation
                break;
        }

        switch (validateProperties(_user, _group)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return null;

            case ALL_VALID:
                return new PathPermissions(_user.getValue(), _group.getValue(), mode, recursive);
        }

        // required catch all for switch statement fall-through which should be full enumerated
        return null;
    }

    /**
     * Validate the userId and groupId fields. Returns a PathPermissions object if one was able
     * to be created, null otherwise.
     */
    private PathPermissions validateIdFields (Integer mode, Boolean recursive)
    {
        switch (areMutablesSet(_userId, _groupId)) {
            case ALL_UNSET:
                return null;

            case SOME_UNSET:
                reportUnsetDependentProperties(_userId, _groupId);
                reportUnsetDependentProperties(_groupId, _userId);
                return null;

            case ALL_SET:
                // report violations if any of the name fields are set
                reportConflictingProperties(_user, _userId, _groupId);
                reportConflictingProperties(_group, _userId, _groupId);

                // continue to validation
                break;
        }

        switch (validateProperties(_userId, _groupId)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return null;

            case ALL_VALID:
                return new PathPermissions(_userId.getValue(), _groupId.getValue(), mode, recursive);
        }

        // required catch all for switch statement fall-through which should be full enumerated
        return null;
    }

    /** The user supplied fields as a PathPermissions object. */
    private PathPermissions _pathPermissions;

    /** The list containing all the paths defined for this Permission after they have been validated. */
    private List<Path> _validatedPaths;

    /** Ant adder/setter fields. */
    private final StringProperty _user = new StringProperty("user", this);
    private final StringProperty _group = new StringProperty("group", this);
    private final IntegerProperty _userId = new IntegerProperty("userid", this);
    private final IntegerProperty _groupId = new IntegerProperty("groupid", this);
    private final BooleanProperty _recursive = new BooleanProperty("recursive", this, false);
    private final ModeProperty _mode = new ModeProperty("mode", this);
}
