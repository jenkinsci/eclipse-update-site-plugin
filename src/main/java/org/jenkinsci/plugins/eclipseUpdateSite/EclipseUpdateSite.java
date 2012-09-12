package org.jenkinsci.plugins.eclipseUpdateSite;

import hudson.Extension;
import hudson.PluginWrapper;
import hudson.model.RootAction;
import hudson.model.UnprotectedRootAction;
import jenkins.model.Jenkins;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class EclipseUpdateSite implements UnprotectedRootAction {
    public long timestamp = -1;

    public long getTimestamp() {
        if (timestamp==-1)
            timestamp = System.currentTimeMillis();
        return timestamp;
    }

    public List<PluginWrapper> getChildren() {
        List<PluginWrapper> r = new ArrayList<PluginWrapper>();
        for (PluginWrapper p : Jenkins.getInstance().pluginManager.getPlugins()) {
            String shortName = p.getShortName();
            for (String probe : PROBE) {
                if (exists(p.baseResourceURL,"eclipse.site/"+probe)) {
                    r.add(p);
                    break;
                }
            }
        }

        return r;
    }

    private boolean exists(URL base, String rel) {
        try {
            InputStream s = new URL(base,rel).openStream();
            IOUtils.closeQuietly(s);
            return s!=null;
        } catch (IOException e) {
            return false;
        }
    }

    public String getIconFileName() {
        if (getChildren().isEmpty())    return null;
        return "/plugin/eclipse-update-site/eclipse.png";
    }

    public String getDisplayName() {
        return Messages.EclipseUpdateSite_DisplayName();
    }

    public String getUrlName() {
        return "eclipse.site";
    }

    private static String[] PROBE = {"content.jar","content.xml","compositeContent.xml"};
}
