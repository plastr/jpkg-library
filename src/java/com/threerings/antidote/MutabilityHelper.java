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
package com.threerings.antidote;


/**
 * A static helper utility for dealing with mutable Ant data.
 */
public class MutabilityHelper
{
    /**
     * Checks to see if the supplied Object is set, e.g. not null.
     * It is expected this method would be used as a static import to increase readability as in:
     * if (objectIsSet(object))
     */
    public static boolean objectIsSet (Object object)
    {
        if (object != null) {
            return true;
        }
        return false;
    }

    /**
     * Checks to see if the supplied Object is not set, e.g. null.
     * It is expected this method would be used as a static import to increase readability as in:
     * if (objectIsNotSet(object))
     */
    public static boolean objectIsNotSet (Object object)
    {
        return !objectIsSet(object);
    }

    /**
     * Return a {@link SetStatus} enum describing the "set" state, of the list of
     * supplied {@link Mutable} objects.
     */
    public static SetStatus areMutablesSet (Mutable...mutables)
    {
        int unset = 0;
        for (final Mutable mutable : mutables) {
            if (mutable.isNotSet()) {
                unset++;
            }
        }

        if (unset == mutables.length) {
            return SetStatus.ALL_UNSET;

        } else if (unset == 0) {
            return SetStatus.ALL_SET;

        } else {
            return SetStatus.SOME_UNSET;
        }
    }

    /**
     * Alerts the developer via an {@link RequiresValidationException} that an object was accessed before
     * validate() or validateField() was called, which is the method where the object is being set.
     * This is a workaround for Ant's mutable state.
     * It is expected this method would be used as a static import to increase readability as in:
     * requiresValidation(object);
     * @throws RequiresValidationException If the supplied object is null
     */
    public static void requiresValidation (Object notNull)
    {
        if (objectIsNotSet(notNull)) {
            throw new RequiresValidationException();
        }
    }
}
