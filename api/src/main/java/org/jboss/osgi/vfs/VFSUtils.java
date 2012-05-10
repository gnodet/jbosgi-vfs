/*
 * #%L
 * JBossOSGi VFS API
 * %%
 * Copyright (C) 2010 - 2012 JBoss by Red Hat
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.osgi.vfs;

import static org.jboss.osgi.vfs.internal.VFSLogger.LOGGER;
import static org.jboss.osgi.vfs.internal.VFSMessages.MESSAGES;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Some VFS utilities that are used by the OSGi layer.
 * 
 * @author thomas.diesler@jboss.com
 * @since 02-Mar-2010
 */
public final class VFSUtils {

    // Hide ctor
    private VFSUtils() {
    }

    public static Manifest getManifest(VirtualFile archive) throws IOException {
        if (archive == null)
            throw MESSAGES.illegalArgumentNull("archive");

        VirtualFile manifest = archive.getChild(JarFile.MANIFEST_NAME);
        if (manifest == null)
            return null;

        InputStream stream = manifest.openStream();
        try {
            return new Manifest(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        if (is == null)
            throw MESSAGES.illegalArgumentNull("input");
        if (os == null)
            throw MESSAGES.illegalArgumentNull("output");

        try {
            byte[] buff = new byte[65536];
            int rc = is.read(buff);
            while (rc != -1) {
                os.write(buff, 0, rc);
                rc = is.read(buff);
            }
        } finally {
            os.flush();
        }
    }

    /**
     * Safely close some resource without throwing an exception. 
     * Any exception will be logged at TRACE level.
     */
    public static void safeClose(Closeable c) {
        try {
            if (c != null)
                c.close();
        } catch (Exception ex) {
            LOGGER.tracef(ex, "Failed to close resource");
        }
    }

    public static String getPathFromClassName(final String className) {
        int idx = className.lastIndexOf('.');
        return idx > -1 ? getPathFromPackageName(className.substring(0, idx)) : "";
    }

    public static String getPathFromPackageName(String packageName) {
        return packageName.replace('.', '/');
    }
}
