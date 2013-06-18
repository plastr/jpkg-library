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
package com.threerings.jpkg.ant.dpkg.scripts;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.threerings.jpkg.ant.dpkg.Dpkg;
import com.threerings.jpkg.ant.dpkg.MockDpkgData;
import com.threerings.jpkg.debian.MaintainerScript;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TemplateScriptTest
{
    @Test
    public void testTemplateScript ()
        throws Exception
    {
        final HelloTemplateScript script = new HelloTemplateScript();
        assertTrue(IOUtils.toString(script.getSource(new MockDpkgData()), Dpkg.CHAR_ENCODING).contains("Hello Earth!"));
        assertTrue(script.getTypes().contains(MaintainerScript.Type.PRERM));
        assertEquals(1, script.getTypes().size());
    }

    private static class HelloTemplateScript extends TemplateScript
    {
        public String getFieldName ()
        {
            return "hello";
        }

        public HelloTemplateScript ()
        {
            super(MaintainerScript.Type.PRERM);
            addSubstitution("world", "Earth");
        }

        public String getFriendlyName ()
        {
            return "Mock template script";
        }

        public boolean failOnError ()
        {
            return true;
        }

        public String getTemplateName ()
        {
            return "hello.vm";
        }

        @Override
        protected void validateField ()
        {
            // no validation
        }
    }
}
