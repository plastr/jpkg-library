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
package com.threerings.jpkg.ant.dpkg.info;

import org.apache.tools.ant.Project;

/**
 * A mutable Info field for unit tests.
 */
public class MockInfo extends Info
{
    /**
     * The data set in the mock.
     */
    public static final String PACKAGENAME = "newpackage";
    public static final String PACKAGEVERSION = "1.2";
    public static final String PACKAGEARCH = "i386";
    public static final String PACKAGEDESC = "Package description.";
    public static final String PACKAGEMAINTAINER = "Maintainer";
    public static final String PACKAGEEMAIL= "maintainer@package.com";
    public static final String PACKAGEPRIORITY = "optional";
    public static final String PACKAGESECTION = "misc";

    /**
     * Expected values from the above.
     */
    public static final String OUTPUT_NAME = PACKAGENAME + "_" + PACKAGEVERSION + ".dpkg";

    /**
     * Provide mutable direct access to the fields for test manipulation.
     */
    public Name nameField;
    public Version versionField;
    public Arch archField;
    public Description descriptionField;
    public MaintainerName maintainerNameField;
    public MaintainerEmail maintainerEmailField;
    public Maintainer maintainerField;
    public Priority priorityField;
    public Section sectionField;

    public MockInfo (String name, String version, String arch, String description,
           String maintainerName, String maintainerEmail)
    {
        this(name, version, arch, description, maintainerName, maintainerEmail, null, null);
    }

    public MockInfo (String name, String version, String arch, String description,
        String maintainerName, String maintainerEmail, String priority, String section)
    {
        final Project project = new Project();
        // init() depends on ant-launcher.jar being in the classpath
        project.init();
        setProject(project);

        nameField = new Name();
        nameField.setProject(getProject());
        nameField.addText(name);

        versionField = new Version();
        versionField.setProject(getProject());
        versionField.addText(version);

        archField = new Arch();
        archField.setProject(getProject());
        archField.addText(arch);

        descriptionField = new Description();
        descriptionField.setProject(getProject());
        descriptionField.addText(description);

        maintainerNameField = new MaintainerName();
        maintainerEmailField = new MaintainerEmail();
        maintainerNameField.setProject(getProject());
        maintainerEmailField.setProject(getProject());
        maintainerNameField.addText(maintainerName);
        maintainerEmailField.addText(maintainerEmail);

        maintainerField = new Maintainer();
        maintainerField.setProject(getProject());
        maintainerField.addName(maintainerNameField);
        maintainerField.addEmail(maintainerEmailField);

        if (priority != null) {
            priorityField = new Priority();
            priorityField.setProject(getProject());
            priorityField.addText(priority);
        }

        if (section != null) {
            sectionField = new Section();
            sectionField.setProject(getProject());
            sectionField.addText(section);
        }
    }

    /**
     * Provide a minimally populated valid &lt;info&gt; field.
     */
    public static MockInfo generateValidInfo ()
    {
        // <info>
        //   <name>newpackage</name>
        //   <version>1.2</version>
        //   <arch>i386</arch>
        //   <description>Package description.</description>
        //   <maintainer>
        //     <name>Maintainer</name>
        //     <email>maintainer@package.com<email>
        //   </maintainer>
        // </info>
        final MockInfo info = new MockInfo(PACKAGENAME, PACKAGEVERSION, PACKAGEARCH, PACKAGEDESC,
            PACKAGEMAINTAINER, PACKAGEEMAIL);
        return info;
    }

    /**
     * Provide a fully populated valid &lt;info&gt; field.
     */
    public static MockInfo generateFullValidInfo ()
    {
        // <info>
        //   <name>newpackage</name>
        //   <version>1.2</version>
        //   <arch>i386</arch>
        //   <description>Package description.</description>
        //   <maintainer>
        //     <name>Maintainer</name>
        //     <email>maintainer@package.com<email>
        //   </maintainer>
        //   <priority>optional</priority>
        //   <section>misc</section>
        // </info>
        final MockInfo info = new MockInfo(PACKAGENAME, PACKAGEVERSION, PACKAGEARCH, PACKAGEDESC,
            PACKAGEMAINTAINER, PACKAGEEMAIL, PACKAGEPRIORITY, PACKAGESECTION);
        return info;
    }

    /**
     * Add all the fields to the &lt;info&gt; field as if the Ant build was run.
     */
    public void populateFields ()
    {
        if (nameField != null) {
            addName(nameField);
        }
        if (versionField != null) {
            addVersion(versionField);
        }
        if (archField != null) {
            addArch(archField);
        }
        if (descriptionField != null) {
            addDescription(descriptionField);
        }
        if (maintainerField != null) {
            addMaintainer(maintainerField);
        }
        if (priorityField != null) {
            addPriority(priorityField);
        }
        if (sectionField != null) {
            addSection(sectionField);
        }
    }
}
