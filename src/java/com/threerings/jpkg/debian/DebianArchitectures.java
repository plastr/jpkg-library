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
package com.threerings.jpkg.debian;
/**
 * Known Debian architectures used by the Debian packaging system.
 * Generated from Debian dpkg-architecture version 1.13.25. on 2008-02-26T19:13:50-08:00.
 * @see <a href="http://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Architecture">Debian Policy Manual</a>
 */
public enum DebianArchitectures
{
    /** Indicates a package available for building on any architecture. */
    ANY ("any"),
    /** Indicates an architecture-independent package. */
    ALL ("all"),
    /** Indicates a source package. */
    SOURCE ("source"),

    /** fields generated from dpkg--architecture -L output */
    /** The i386 architecture. */
    I386 ("i386"),
    /** The ia64 architecture. */
    IA64 ("ia64"),
    /** The alpha architecture. */
    ALPHA ("alpha"),
    /** The amd64 architecture. */
    AMD64 ("amd64"),
    /** The armeb architecture. */
    ARMEB ("armeb"),
    /** The arm architecture. */
    ARM ("arm"),
    /** The hppa architecture. */
    HPPA ("hppa"),
    /** The m32r architecture. */
    M32R ("m32r"),
    /** The m68k architecture. */
    M68K ("m68k"),
    /** The mips architecture. */
    MIPS ("mips"),
    /** The mipsel architecture. */
    MIPSEL ("mipsel"),
    /** The powerpc architecture. */
    POWERPC ("powerpc"),
    /** The ppc64 architecture. */
    PPC64 ("ppc64"),
    /** The s390 architecture. */
    S390 ("s390"),
    /** The s390x architecture. */
    S390X ("s390x"),
    /** The sh3 architecture. */
    SH3 ("sh3"),
    /** The sh3eb architecture. */
    SH3EB ("sh3eb"),
    /** The sh4 architecture. */
    SH4 ("sh4"),
    /** The sh4eb architecture. */
    SH4EB ("sh4eb"),
    /** The sparc architecture. */
    SPARC ("sparc"),
    /** The darwin-i386 architecture. */
    DARWIN_I386 ("darwin-i386"),
    /** The darwin-ia64 architecture. */
    DARWIN_IA64 ("darwin-ia64"),
    /** The darwin-alpha architecture. */
    DARWIN_ALPHA ("darwin-alpha"),
    /** The darwin-amd64 architecture. */
    DARWIN_AMD64 ("darwin-amd64"),
    /** The darwin-armeb architecture. */
    DARWIN_ARMEB ("darwin-armeb"),
    /** The darwin-arm architecture. */
    DARWIN_ARM ("darwin-arm"),
    /** The darwin-hppa architecture. */
    DARWIN_HPPA ("darwin-hppa"),
    /** The darwin-m32r architecture. */
    DARWIN_M32R ("darwin-m32r"),
    /** The darwin-m68k architecture. */
    DARWIN_M68K ("darwin-m68k"),
    /** The darwin-mips architecture. */
    DARWIN_MIPS ("darwin-mips"),
    /** The darwin-mipsel architecture. */
    DARWIN_MIPSEL ("darwin-mipsel"),
    /** The darwin-powerpc architecture. */
    DARWIN_POWERPC ("darwin-powerpc"),
    /** The darwin-ppc64 architecture. */
    DARWIN_PPC64 ("darwin-ppc64"),
    /** The darwin-s390 architecture. */
    DARWIN_S390 ("darwin-s390"),
    /** The darwin-s390x architecture. */
    DARWIN_S390X ("darwin-s390x"),
    /** The darwin-sh3 architecture. */
    DARWIN_SH3 ("darwin-sh3"),
    /** The darwin-sh3eb architecture. */
    DARWIN_SH3EB ("darwin-sh3eb"),
    /** The darwin-sh4 architecture. */
    DARWIN_SH4 ("darwin-sh4"),
    /** The darwin-sh4eb architecture. */
    DARWIN_SH4EB ("darwin-sh4eb"),
    /** The darwin-sparc architecture. */
    DARWIN_SPARC ("darwin-sparc"),
    /** The freebsd-i386 architecture. */
    FREEBSD_I386 ("freebsd-i386"),
    /** The freebsd-ia64 architecture. */
    FREEBSD_IA64 ("freebsd-ia64"),
    /** The freebsd-alpha architecture. */
    FREEBSD_ALPHA ("freebsd-alpha"),
    /** The freebsd-amd64 architecture. */
    FREEBSD_AMD64 ("freebsd-amd64"),
    /** The freebsd-armeb architecture. */
    FREEBSD_ARMEB ("freebsd-armeb"),
    /** The freebsd-arm architecture. */
    FREEBSD_ARM ("freebsd-arm"),
    /** The freebsd-hppa architecture. */
    FREEBSD_HPPA ("freebsd-hppa"),
    /** The freebsd-m32r architecture. */
    FREEBSD_M32R ("freebsd-m32r"),
    /** The freebsd-m68k architecture. */
    FREEBSD_M68K ("freebsd-m68k"),
    /** The freebsd-mips architecture. */
    FREEBSD_MIPS ("freebsd-mips"),
    /** The freebsd-mipsel architecture. */
    FREEBSD_MIPSEL ("freebsd-mipsel"),
    /** The freebsd-powerpc architecture. */
    FREEBSD_POWERPC ("freebsd-powerpc"),
    /** The freebsd-ppc64 architecture. */
    FREEBSD_PPC64 ("freebsd-ppc64"),
    /** The freebsd-s390 architecture. */
    FREEBSD_S390 ("freebsd-s390"),
    /** The freebsd-s390x architecture. */
    FREEBSD_S390X ("freebsd-s390x"),
    /** The freebsd-sh3 architecture. */
    FREEBSD_SH3 ("freebsd-sh3"),
    /** The freebsd-sh3eb architecture. */
    FREEBSD_SH3EB ("freebsd-sh3eb"),
    /** The freebsd-sh4 architecture. */
    FREEBSD_SH4 ("freebsd-sh4"),
    /** The freebsd-sh4eb architecture. */
    FREEBSD_SH4EB ("freebsd-sh4eb"),
    /** The freebsd-sparc architecture. */
    FREEBSD_SPARC ("freebsd-sparc"),
    /** The kfreebsd-i386 architecture. */
    KFREEBSD_I386 ("kfreebsd-i386"),
    /** The kfreebsd-ia64 architecture. */
    KFREEBSD_IA64 ("kfreebsd-ia64"),
    /** The kfreebsd-alpha architecture. */
    KFREEBSD_ALPHA ("kfreebsd-alpha"),
    /** The kfreebsd-amd64 architecture. */
    KFREEBSD_AMD64 ("kfreebsd-amd64"),
    /** The kfreebsd-armeb architecture. */
    KFREEBSD_ARMEB ("kfreebsd-armeb"),
    /** The kfreebsd-arm architecture. */
    KFREEBSD_ARM ("kfreebsd-arm"),
    /** The kfreebsd-hppa architecture. */
    KFREEBSD_HPPA ("kfreebsd-hppa"),
    /** The kfreebsd-m32r architecture. */
    KFREEBSD_M32R ("kfreebsd-m32r"),
    /** The kfreebsd-m68k architecture. */
    KFREEBSD_M68K ("kfreebsd-m68k"),
    /** The kfreebsd-mips architecture. */
    KFREEBSD_MIPS ("kfreebsd-mips"),
    /** The kfreebsd-mipsel architecture. */
    KFREEBSD_MIPSEL ("kfreebsd-mipsel"),
    /** The kfreebsd-powerpc architecture. */
    KFREEBSD_POWERPC ("kfreebsd-powerpc"),
    /** The kfreebsd-ppc64 architecture. */
    KFREEBSD_PPC64 ("kfreebsd-ppc64"),
    /** The kfreebsd-s390 architecture. */
    KFREEBSD_S390 ("kfreebsd-s390"),
    /** The kfreebsd-s390x architecture. */
    KFREEBSD_S390X ("kfreebsd-s390x"),
    /** The kfreebsd-sh3 architecture. */
    KFREEBSD_SH3 ("kfreebsd-sh3"),
    /** The kfreebsd-sh3eb architecture. */
    KFREEBSD_SH3EB ("kfreebsd-sh3eb"),
    /** The kfreebsd-sh4 architecture. */
    KFREEBSD_SH4 ("kfreebsd-sh4"),
    /** The kfreebsd-sh4eb architecture. */
    KFREEBSD_SH4EB ("kfreebsd-sh4eb"),
    /** The kfreebsd-sparc architecture. */
    KFREEBSD_SPARC ("kfreebsd-sparc"),
    /** The knetbsd-i386 architecture. */
    KNETBSD_I386 ("knetbsd-i386"),
    /** The knetbsd-ia64 architecture. */
    KNETBSD_IA64 ("knetbsd-ia64"),
    /** The knetbsd-alpha architecture. */
    KNETBSD_ALPHA ("knetbsd-alpha"),
    /** The knetbsd-amd64 architecture. */
    KNETBSD_AMD64 ("knetbsd-amd64"),
    /** The knetbsd-armeb architecture. */
    KNETBSD_ARMEB ("knetbsd-armeb"),
    /** The knetbsd-arm architecture. */
    KNETBSD_ARM ("knetbsd-arm"),
    /** The knetbsd-hppa architecture. */
    KNETBSD_HPPA ("knetbsd-hppa"),
    /** The knetbsd-m32r architecture. */
    KNETBSD_M32R ("knetbsd-m32r"),
    /** The knetbsd-m68k architecture. */
    KNETBSD_M68K ("knetbsd-m68k"),
    /** The knetbsd-mips architecture. */
    KNETBSD_MIPS ("knetbsd-mips"),
    /** The knetbsd-mipsel architecture. */
    KNETBSD_MIPSEL ("knetbsd-mipsel"),
    /** The knetbsd-powerpc architecture. */
    KNETBSD_POWERPC ("knetbsd-powerpc"),
    /** The knetbsd-ppc64 architecture. */
    KNETBSD_PPC64 ("knetbsd-ppc64"),
    /** The knetbsd-s390 architecture. */
    KNETBSD_S390 ("knetbsd-s390"),
    /** The knetbsd-s390x architecture. */
    KNETBSD_S390X ("knetbsd-s390x"),
    /** The knetbsd-sh3 architecture. */
    KNETBSD_SH3 ("knetbsd-sh3"),
    /** The knetbsd-sh3eb architecture. */
    KNETBSD_SH3EB ("knetbsd-sh3eb"),
    /** The knetbsd-sh4 architecture. */
    KNETBSD_SH4 ("knetbsd-sh4"),
    /** The knetbsd-sh4eb architecture. */
    KNETBSD_SH4EB ("knetbsd-sh4eb"),
    /** The knetbsd-sparc architecture. */
    KNETBSD_SPARC ("knetbsd-sparc"),
    /** The netbsd-i386 architecture. */
    NETBSD_I386 ("netbsd-i386"),
    /** The netbsd-ia64 architecture. */
    NETBSD_IA64 ("netbsd-ia64"),
    /** The netbsd-alpha architecture. */
    NETBSD_ALPHA ("netbsd-alpha"),
    /** The netbsd-amd64 architecture. */
    NETBSD_AMD64 ("netbsd-amd64"),
    /** The netbsd-armeb architecture. */
    NETBSD_ARMEB ("netbsd-armeb"),
    /** The netbsd-arm architecture. */
    NETBSD_ARM ("netbsd-arm"),
    /** The netbsd-hppa architecture. */
    NETBSD_HPPA ("netbsd-hppa"),
    /** The netbsd-m32r architecture. */
    NETBSD_M32R ("netbsd-m32r"),
    /** The netbsd-m68k architecture. */
    NETBSD_M68K ("netbsd-m68k"),
    /** The netbsd-mips architecture. */
    NETBSD_MIPS ("netbsd-mips"),
    /** The netbsd-mipsel architecture. */
    NETBSD_MIPSEL ("netbsd-mipsel"),
    /** The netbsd-powerpc architecture. */
    NETBSD_POWERPC ("netbsd-powerpc"),
    /** The netbsd-ppc64 architecture. */
    NETBSD_PPC64 ("netbsd-ppc64"),
    /** The netbsd-s390 architecture. */
    NETBSD_S390 ("netbsd-s390"),
    /** The netbsd-s390x architecture. */
    NETBSD_S390X ("netbsd-s390x"),
    /** The netbsd-sh3 architecture. */
    NETBSD_SH3 ("netbsd-sh3"),
    /** The netbsd-sh3eb architecture. */
    NETBSD_SH3EB ("netbsd-sh3eb"),
    /** The netbsd-sh4 architecture. */
    NETBSD_SH4 ("netbsd-sh4"),
    /** The netbsd-sh4eb architecture. */
    NETBSD_SH4EB ("netbsd-sh4eb"),
    /** The netbsd-sparc architecture. */
    NETBSD_SPARC ("netbsd-sparc"),
    /** The openbsd-i386 architecture. */
    OPENBSD_I386 ("openbsd-i386"),
    /** The openbsd-ia64 architecture. */
    OPENBSD_IA64 ("openbsd-ia64"),
    /** The openbsd-alpha architecture. */
    OPENBSD_ALPHA ("openbsd-alpha"),
    /** The openbsd-amd64 architecture. */
    OPENBSD_AMD64 ("openbsd-amd64"),
    /** The openbsd-armeb architecture. */
    OPENBSD_ARMEB ("openbsd-armeb"),
    /** The openbsd-arm architecture. */
    OPENBSD_ARM ("openbsd-arm"),
    /** The openbsd-hppa architecture. */
    OPENBSD_HPPA ("openbsd-hppa"),
    /** The openbsd-m32r architecture. */
    OPENBSD_M32R ("openbsd-m32r"),
    /** The openbsd-m68k architecture. */
    OPENBSD_M68K ("openbsd-m68k"),
    /** The openbsd-mips architecture. */
    OPENBSD_MIPS ("openbsd-mips"),
    /** The openbsd-mipsel architecture. */
    OPENBSD_MIPSEL ("openbsd-mipsel"),
    /** The openbsd-powerpc architecture. */
    OPENBSD_POWERPC ("openbsd-powerpc"),
    /** The openbsd-ppc64 architecture. */
    OPENBSD_PPC64 ("openbsd-ppc64"),
    /** The openbsd-s390 architecture. */
    OPENBSD_S390 ("openbsd-s390"),
    /** The openbsd-s390x architecture. */
    OPENBSD_S390X ("openbsd-s390x"),
    /** The openbsd-sh3 architecture. */
    OPENBSD_SH3 ("openbsd-sh3"),
    /** The openbsd-sh3eb architecture. */
    OPENBSD_SH3EB ("openbsd-sh3eb"),
    /** The openbsd-sh4 architecture. */
    OPENBSD_SH4 ("openbsd-sh4"),
    /** The openbsd-sh4eb architecture. */
    OPENBSD_SH4EB ("openbsd-sh4eb"),
    /** The openbsd-sparc architecture. */
    OPENBSD_SPARC ("openbsd-sparc"),
    /** The hurd-i386 architecture. */
    HURD_I386 ("hurd-i386"),
    /** The hurd-ia64 architecture. */
    HURD_IA64 ("hurd-ia64"),
    /** The hurd-alpha architecture. */
    HURD_ALPHA ("hurd-alpha"),
    /** The hurd-amd64 architecture. */
    HURD_AMD64 ("hurd-amd64"),
    /** The hurd-armeb architecture. */
    HURD_ARMEB ("hurd-armeb"),
    /** The hurd-arm architecture. */
    HURD_ARM ("hurd-arm"),
    /** The hurd-hppa architecture. */
    HURD_HPPA ("hurd-hppa"),
    /** The hurd-m32r architecture. */
    HURD_M32R ("hurd-m32r"),
    /** The hurd-m68k architecture. */
    HURD_M68K ("hurd-m68k"),
    /** The hurd-mips architecture. */
    HURD_MIPS ("hurd-mips"),
    /** The hurd-mipsel architecture. */
    HURD_MIPSEL ("hurd-mipsel"),
    /** The hurd-powerpc architecture. */
    HURD_POWERPC ("hurd-powerpc"),
    /** The hurd-ppc64 architecture. */
    HURD_PPC64 ("hurd-ppc64"),
    /** The hurd-s390 architecture. */
    HURD_S390 ("hurd-s390"),
    /** The hurd-s390x architecture. */
    HURD_S390X ("hurd-s390x"),
    /** The hurd-sh3 architecture. */
    HURD_SH3 ("hurd-sh3"),
    /** The hurd-sh3eb architecture. */
    HURD_SH3EB ("hurd-sh3eb"),
    /** The hurd-sh4 architecture. */
    HURD_SH4 ("hurd-sh4"),
    /** The hurd-sh4eb architecture. */
    HURD_SH4EB ("hurd-sh4eb"),
    /** The hurd-sparc architecture. */
    HURD_SPARC ("hurd-sparc");

    DebianArchitectures (String name)
    {
        _name = name;
    }

    /** Returns the string name for this architecture */
    public String getName ()
    {
        return _name;
    }

    private final String _name;
}
