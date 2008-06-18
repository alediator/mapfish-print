package org.mapfish.print;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Base class for MapPrinter servlets (deals with the configuration loading)
 */
public abstract class BaseMapServlet extends HttpServlet {
    public static final Logger LOGGER = Logger.getLogger(BaseMapServlet.class);

    private MapPrinter printer = null;
    private long lastModified = 0L;

    /**
     * Builds a MapPrinter instance out of the file pointed by the servlet's
     * configuration. The location can be configured in two locations:
     * <ul>
     * <li>web-app/servlet/init-param[param-name=config] (top priority)
     * <li>web-app/context-param[param-name=config] (used only if the servlet has no config)
     * </ul>
     * <p/>
     * If the location is a relative path, it's taken from the servlet's root directory.
     */
    protected synchronized MapPrinter getMapPrinter() throws ServletException {
        String configPath = getInitParameter("config");
        if (configPath == null) {
            throw new ServletException("Missing configuration in web.xml 'web-app/servlet/init-param[param-name=config]' or 'web-app/context-param[param-name=config]'");
        }

        File configFile = new File(configPath);
        if (!configFile.isAbsolute()) {
            configFile = new File(getServletContext().getRealPath(configPath));
        }

        if (printer != null && configFile.lastModified() != lastModified) {
            //file modified, reload it
            printer = null;
            LOGGER.info("Configuration file modified. Reloading...");
        }

        if (printer == null) {
            lastModified = configFile.lastModified();
            try {
                LOGGER.info("Loading configuration file: " + configFile.getAbsolutePath());
                printer = new MapPrinter(configFile);
            } catch (FileNotFoundException e) {
                throw new ServletException("Cannot read configuration file: " + configPath, e);
            }
        }
        return printer;
    }
}