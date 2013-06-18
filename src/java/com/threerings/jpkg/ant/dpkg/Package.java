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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.threerings.antidote.field.BaseField;
import com.threerings.antidote.field.OptionalField;
import com.threerings.antidote.field.RequiredField;
import com.threerings.antidote.property.FileProperty;
import com.threerings.antidote.property.StringProperty;
import com.threerings.jpkg.PathPermissions;
import com.threerings.jpkg.PermissionsMap;
import com.threerings.jpkg.ant.dpkg.dependencies.BaseDependency;
import com.threerings.jpkg.ant.dpkg.dependencies.Dependencies;
import com.threerings.jpkg.ant.dpkg.dependencies.PackageInfoDependency;
import com.threerings.jpkg.ant.dpkg.info.Info;
import com.threerings.jpkg.ant.dpkg.permissions.Permissions;
import com.threerings.jpkg.ant.dpkg.scripts.Scripts;
import com.threerings.jpkg.ant.dpkg.scripts.runner.PackageScript;
import com.threerings.jpkg.ant.dpkg.scripts.runner.ScriptRunner;
import com.threerings.jpkg.ant.dpkg.scripts.runner.UnexpectedScriptTypeException;
import com.threerings.jpkg.debian.PackageInfo;
import com.threerings.jpkg.debian.MaintainerScript.Type;

import static com.threerings.antidote.MutabilityHelper.requiresValidation;

/**
 * The &lt;dpkg&gt; task &lt;package&gt; field. Holds all information needed to generate a given package.
 */
public class Package extends BaseField
{
    // from Field
    public String getFieldName ()
    {
        return "package";
    }

    /**
     * Ant adder field: Set the package meta information.
     */
    public void addInfo (Info info)
    {
        _info.setField(info);
    }

    /**
     * Ant adder field: Set the list of maintainer scripts.
     */
    public void addScripts (Scripts scripts)
    {
        _scripts.setField(scripts);
    }

    /**
     * Ant adder field: Set the list of path permissions.
     */
    public void addPermissions (Permissions permissions)
    {
        _permissions.setField(permissions);
    }

    /**
     * Ant adder field: Set the list of package dependencies.
     */
    public void addDependencies (Dependencies dependencies)
    {
        _dependencies.setField(dependencies);
    }

    /**
     * Ant setter field: destroot. The directory where the root of the package starts.
     */
    public void setDestroot (String value)
    {
        _destroot.setValue(value);
    }

    /**
     * Ant setter field: filename. Optionally set the filename of the package output.
     */
    public void setFilename (String value)
    {
        _filenameProp.setValue(value);
    }

    /**
     * Returns the user data converted into a {@link PackageInfo} object. Cannot be called before validate().
     */
    public PackageInfo createPackageInfo (String distribution, String prefix)
    {
        final Info info = _info.getField();
        final DpkgData data = new DpkgData(info.getPackageNameAsString(), info.getVersionAsString(),
            distribution, prefix);
        final PackageInfo packageInfo = info.getPackageInfo();

        if (_scripts.isSet()) {
            appendPackageScripts(_scripts.getField().getPackageScripts(), packageInfo, data);
        }

        if (_permissions.isSet()) {
            appendPermissionsMap(_permissions.getField().getPermissionsMap(prefix), packageInfo);
        }

        if (_dependencies.isSet()) {
            appendDependencies(_dependencies.getField().getDependencies(), packageInfo);
        }

        // log the fully populated package info data to the verbose log.
        log(packageInfo.toString(), Project.MSG_VERBOSE);

        return packageInfo;
    }

    /**
     * Returns the destroot to use for this package. Cannot be called before validate().
     */
    public File getDestroot ()
    {
        return _destroot.getValue();
    }

    /**
     * Returns the filename to use for this package. Cannot be called before validate().
     */
    public String getFilename ()
    {
        requiresValidation(_filename);
        return _filename;
    }

    @Override // from BaseComponent
    protected void validateField ()
    {
        // validate the fields
        switch (validateChildFields(_info, _scripts, _permissions, _dependencies)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        // validate the required properties
        switch (validateProperties(_destroot)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        // validate the optional properties
        switch (validateOptionalProperties(_filenameProp)) {
            case ALL_INVALID:
            case SOME_INVALID:
                return;

            case ALL_VALID:
                break;
        }

        _filename = generateFilename(_info.getField());
    }

    /**
     * Add any defined PackageScripts to ScriptRunners and add them to the PackageInfo.
     */
    private void appendPackageScripts (List<PackageScript> scripts, PackageInfo packageInfo, DpkgData data)
    {
        // write all the script source out to the debug log
        printScriptsDebugging(scripts, data);

        // for each script type, encode all scripts of that type into a ScriptRunner
        // and add that runner to the PackageInfo object.
        for (final Type scriptType : Type.values()) {
            final List<PackageScript> foundScripts = new ArrayList<PackageScript>();
            for (final PackageScript script : scripts) {
                if (script.getTypes().contains(scriptType)) {
                    foundScripts.add(script);
                }
            }

            if (foundScripts.size() > 0) {
                try {
                    final ScriptRunner runner = new ScriptRunner(scriptType, foundScripts, data);
                    packageInfo.setMaintainerScript(runner);

                    // write the runner source out to the debug log
                    printRunnerDebugging(runner);

                } catch (final IOException e) {
                    throw new BuildException(e);

                } catch (final UnexpectedScriptTypeException uste) {
                    throw new BuildException(uste);
                }
            }
        }
    }

    /**
     * Add the PermissionsMap to the PackageInfo.
     */
    private void appendPermissionsMap (PermissionsMap permissionsMap, PackageInfo packageInfo)
    {
        // write the permissions map to the debug log
        log("Defined path permissions:\n", Project.MSG_VERBOSE);
        log(permissionsMap.toString(), Project.MSG_VERBOSE);

        for (final Entry<String, PathPermissions> entry : permissionsMap.getPermissions()) {
            packageInfo.addPathPermissions(entry.getKey(), entry.getValue());

        }
    }

    /**
     * Add a list of {@link BaseDependency} objects to the {@link PackageInfo} object.
     */
    private void appendDependencies (List<PackageInfoDependency> dependencies, PackageInfo packageInfo)
    {
        for (final PackageInfoDependency dependency : dependencies) {
            dependency.addToPackageInfo(packageInfo);
        }
    }

    /**
     * Print a list of PackageScripts to the debug log.
     */
    private void printScriptsDebugging (List<PackageScript> scripts, DpkgData data)
    {
        for (final PackageScript script : scripts) {
            try {
                log("Defined PackageScript: name=[" + script.getFriendlyName() + "] " +
                    "type=[" + script.getTypes() + "], source=[" +
                    IOUtils.toString(script.getSource(data)) + "].", Project.MSG_VERBOSE);

            } catch (final Exception e) {
                throw new BuildException(e);
            }
        }
    }

    /**
     * Print the ScriptRunner to the debug log.
     */
    private void printRunnerDebugging (ScriptRunner runner)
        throws IOException
    {
        log("Defined Script Runner for type " + runner.getType() + " :", Project.MSG_VERBOSE);
        log(IOUtils.toString(runner.getStream()), Project.MSG_VERBOSE);
    }

    /**
     * Set the filename for this package field. Use the user supplied string if set, otherwise use
     * a default.
     */
    private String generateFilename (Info info)
    {
        String filename;
        if (_filenameProp.isSet()) {
            filename = _filenameProp.getValue();

        } else {
            filename = info.getPackageNameAsString() + "_" + info.getVersionAsString() + EXTENSION;
        }
        return filename;
    }

    /** The default file extension for the package output file. */
    private static final String EXTENSION = ".dpkg";

    /** The filename, either from the user property or a default. */
    private String _filename;

    /** Ant adder/setter fields. */
    private final RequiredField<Info> _info = new RequiredField<Info>(Info.class, this);
    private final OptionalField<Scripts> _scripts = new OptionalField<Scripts>(Scripts.class, this);
    private final OptionalField<Permissions> _permissions = new OptionalField<Permissions>(Permissions.class, this);
    private final OptionalField<Dependencies> _dependencies = new OptionalField<Dependencies>(Dependencies.class, this);
    private final FileProperty _destroot = new FileProperty("destroot", this);
    private final StringProperty _filenameProp = new StringProperty("filename", this);
}
