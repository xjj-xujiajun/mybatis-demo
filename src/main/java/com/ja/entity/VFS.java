package com.ja.entity;

import org.apache.ibatis.io.DefaultVFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class VFS extends DefaultVFS {

    private static final Log log = LogFactory.getLog(VFS.class);

    @Override
    public List<String> list(URL url, String path) throws IOException {
        {
            InputStream is = null;
            try {
                List<String> resources = new ArrayList<>();

                // First, try to find the URL of a JAR file containing the requested resource. If a JAR
                // file is found, then we'll list child resources by reading the JAR.
                URL jarUrl = findJarForResource(url);
                if (jarUrl != null) {
                    is = jarUrl.openStream();
                    if (log.isDebugEnabled()) {
                        log.debug("Listing " + url);
                    }
                    resources = listResources(new JarInputStream(is), path);
                }
                else {
                    List<String> children = new ArrayList<>();
                    try {
                        if (isJar(url)) {
                            // Some versions of JBoss VFS might give a JAR stream even if the resource
                            // referenced by the URL isn't actually a JAR
                            is = url.openStream();
                            try (JarInputStream jarInput = new JarInputStream(is)) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Listing " + url);
                                }
                                for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null; ) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Jar entry: " + entry.getName());
                                    }
                                    children.add(entry.getName());
                                }
                            }
                        }
                        else {
                            /*
                             * Some servlet containers allow reading from directory resources like a
                             * text file, listing the child resources one per line. However, there is no
                             * way to differentiate between directory and file resources just by reading
                             * them. To work around that, as each line is read, try to look it up via
                             * the class loader as a child of the current resource. If any line fails
                             * then we assume the current resource is not a directory.
                             */
                            if(!url.getPath().endsWith(".class")){
                                is = url.openStream();
                                List<String> lines = new ArrayList<>();
                                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                                    for (String line; (line = reader.readLine()) != null;) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Reader entry: " + line);
                                        }
                                        lines.add(line);
                                        if (getResources(path + "/" + line).isEmpty()) {
                                            lines.clear();
                                            break;
                                        }
                                    }
                                }
                                if (!lines.isEmpty()) {
                                    if (log.isDebugEnabled()) {
                                        log.debug("Listing " + url);
                                    }
                                    children.addAll(lines);
                                }
                            }
                        }
                    } catch (FileNotFoundException e) {
                        /*
                         * For file URLs the openStream() call might fail, depending on the servlet
                         * container, because directories can't be opened for reading. If that happens,
                         * then list the directory directly instead.
                         */
                        if ("file".equals(url.getProtocol())) {
                            File file = new File(url.getFile());
                            if (log.isDebugEnabled()) {
                                log.debug("Listing directory " + file.getAbsolutePath());
                            }
                            if (file.isDirectory()) {
                                if (log.isDebugEnabled()) {
                                    log.debug("Listing " + url);
                                }
                                children = Arrays.asList(file.list());
                            }
                        }
                        else {
                            // No idea where the exception came from so rethrow it
                            throw e;
                        }
                    }

                    // The URL prefix to use when recursively listing child resources
                    String prefix = url.toExternalForm();
                    if (!prefix.endsWith("/")) {
                        prefix = prefix + "/";
                    }

                    // Iterate over immediate children, adding files and recursing into directories
                    for (String child : children) {
                        String resourcePath = path + "/" + child;
                        resources.add(resourcePath);
                        URL childUrl = new URL(prefix + child);
                        resources.addAll(list(childUrl, resourcePath));
                    }
                }

                return resources;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
        }
    }
}
