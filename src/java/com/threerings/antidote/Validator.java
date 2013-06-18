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
package com.threerings.antidote;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;

/**
 * Turns lists of {@link RequiresValidation} objects into a single {@link BuildException}
 */
 public class Validator
{
     /**
      * Add a {@link RequiresValidation} object to be reported by this Validator
      */
     public void addValidation (RequiresValidation validator)
     {
         _validators.add(validator);
     }

     /**
      * Validates all {@link RequiresValidation} objects known to this Validator, throwing a
      * single {@link BuildException} for all detected violations.
      * @throws BuildException
      */
     public void validateAll ()
     {
         final StringBuilder builder = new StringBuilder();
         for (final RequiresValidation validator : _validators) {
             for (final Violation violation : validator.validate()) {
                 if (builder.length() > 0) {
                     builder.append("\n");
                 }
                 if (builder.length() == 0) {
                     builder.append("Validation exceptions encountered:\n");
                 }
                 builder.append(violation.getLocation() + violation.getReason());
             }
         }
         if (builder.length() > 0) {
             throw new BuildException(builder.toString());
         }
     }

     private final List<RequiresValidation> _validators = new ArrayList<RequiresValidation>();
}
