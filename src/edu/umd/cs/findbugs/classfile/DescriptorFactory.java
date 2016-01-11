/*
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2007 University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.umd.cs.findbugs.classfile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import edu.umd.cs.findbugs.internalAnnotations.DottedClassName;
import edu.umd.cs.findbugs.internalAnnotations.SlashedClassName;
import edu.umd.cs.findbugs.util.ClassName;
import edu.umd.cs.findbugs.util.MapCache;

/**
 * Factory for creating ClassDescriptors, MethodDescriptors, and
 * FieldDescriptors.
 *
 * @author David Hovemeyer
 */
public class DescriptorFactory {
    private static ThreadLocal<DescriptorFactory> instanceThreadLocal = new ThreadLocal<DescriptorFactory>() {
        @Override
        protected DescriptorFactory initialValue() {
            return new DescriptorFactory();
        }
    };

    private final MapCache<String, String> stringCache = new MapCache<String, String>(10000);

    public static String canonicalizeString(@CheckForNull String s) {
        if (s == null) {
            return s;
        }
        DescriptorFactory df =  instanceThreadLocal.get();
        String cached = df.stringCache.get(s);
        if (cached != null) {
            return cached;
        }
        df.stringCache.put(s, s);
        return s;
    }
}
