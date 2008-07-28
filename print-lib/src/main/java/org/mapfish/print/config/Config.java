package org.mapfish.print.config;

import org.apache.log4j.Logger;
import org.ho.yaml.CustomYamlConfig;
import org.ho.yaml.YamlConfig;
import org.json.JSONException;
import org.json.JSONWriter;
import org.mapfish.print.config.layout.Layout;
import org.mapfish.print.config.layout.Layouts;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * Bean mapping the root of the configuration file.
 */
public class Config {
    public static final Logger LOGGER = Logger.getLogger(Config.class);

    private Layouts layouts;

    private TreeSet<Integer> dpis;

    private TreeSet<Integer> scales;

    private List<HostMatcher> hosts = new ArrayList<HostMatcher>();

    public Config() {
        hosts.add(new LocalHostMatcher());
    }

    /**
     * Create an instance out of the given file.
     */
    public static Config fromYaml(File file) throws FileNotFoundException {
        YamlConfig config = new CustomYamlConfig();
        return config.loadType(file, Config.class);
    }

    public Layout getLayout(String name) {
        return layouts.get(name);
    }

    public void setLayouts(Layouts layouts) {
        this.layouts = layouts;
    }

    public void setDpis(TreeSet<Integer> dpis) {
        this.dpis = dpis;
    }

    public TreeSet<Integer> getDpis() {
        return dpis;
    }

    public void printClientConfig(JSONWriter json) throws JSONException {
        json.key("scales");
        json.array();
        for (Integer scale : scales) {
            json.object();
            json.key("name").value("1:" + NumberFormat.getIntegerInstance().format(scale));
            json.key("value").value(scale.toString());
            json.endObject();
        }
        json.endArray();

        json.key("dpis");
        json.array();
        for (Integer dpi : dpis) {
            json.object();
            json.key("name").value(dpi.toString());
            json.key("value").value(dpi.toString());
            json.endObject();
        }
        json.endArray();

        json.key("layouts");
        json.array();
        ArrayList<String> sortedLayouts = new ArrayList<String>();
        sortedLayouts.addAll(layouts.keySet());
        Collections.sort(sortedLayouts);
        for (int i = 0; i < sortedLayouts.size(); i++) {
            String key = sortedLayouts.get(i);
            json.object();
            json.key("name").value(key);
            layouts.get(key).printClientConfig(json);
            json.endObject();
        }
        json.endArray();
    }

    public void setScales(TreeSet<Integer> scales) {
        this.scales = scales;
    }

    public boolean isScalePresent(int scale) {
        return scales.contains(scale);
    }

    public void setHosts(List<HostMatcher> hosts) {
        this.hosts = hosts;
    }

    public boolean validateUri(URI uri) throws UnknownHostException, SocketException, MalformedURLException {
        for (int i = 0; i < hosts.size(); i++) {
            HostMatcher matcher = hosts.get(i);
            if (matcher.validate(uri)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("URI [" + uri + "] accepted by: " + matcher);
                }
                return true;
            }
        }
        return false;
    }
}
