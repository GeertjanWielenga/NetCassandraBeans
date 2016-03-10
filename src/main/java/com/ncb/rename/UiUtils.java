package com.ncb.rename;

import java.awt.Font;
import java.awt.FontMetrics;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

public final class UiUtils {

    private static final Logger LOGGER = Logger.getLogger(UiUtils.class.getName());

    private UiUtils() {
        super();
    }

    public static String getValue(JComboBox<String> combo) {
        if (combo.isEditable()) {
            return getValue((String) combo.getEditor().getItem());
        }
        return getValue((String) combo.getSelectedItem());
    }

    public static String getValue(JTextComponent c) {
        return getValue(c.getText());
    }

    public static String getValue(String str) {
        String value = str;
        if (value != null) {
            value = value.trim();
            if (value.isEmpty()) {
                return null;
            }
        }
        return value;
    }

    public static void configureRowHeight(JTable table) {
        int height = table.getRowHeight();
        Font cellFont = UIManager.getFont("TextField.font");
        if (cellFont != null) {
            FontMetrics metrics = table.getFontMetrics(cellFont);
            if (metrics != null) {
                height = metrics.getHeight() + 2;
            }
        }
        table.setRowHeight(Math.max(table.getRowHeight(), height));
    }

    public static Collection<String> getAddresses(boolean includeIpv6, boolean includeDocker) {
        Set<InetAddress> addresses = new HashSet<>();
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
                NetworkInterface iface = e.nextElement();
                if (includeDocker || !iface.getName().contains("docker")) { // NOI18N
                    for (Enumeration<InetAddress> ei = iface.getInetAddresses(); ei.hasMoreElements();) {
                        InetAddress addr = ei.nextElement();
                        if (!addr.isLinkLocalAddress() && (includeIpv6 || !(addr instanceof Inet6Address))) {
                            addresses.add(addr);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            LOGGER.log(Level.FINE, null, ex);
        }
        try {
            addresses.add(InetAddress.getLocalHost());
        } catch (UnknownHostException ex) {
            LOGGER.log(Level.FINE, null, ex);
        }
        Set<String> ret = new TreeSet<>();
        for (InetAddress addr : addresses) {
            String host = addr.getHostAddress();
            if (addr instanceof Inet6Address) {
                int index = host.indexOf('%'); // NOI18N
                if (index > 0) {
                    host = host.substring(0, index);
                }
                // compress IPv6 address
                host = host.replaceFirst("(^|:)(0+(:|$)){2,8}", "::").replaceAll("(:|^)0+([0-9A-Fa-f])", "$1$2"); // NOI18N
            }
            ret.add(host);
        }
        return ret;
    }

//    public static void loadRepositories(final DockerInstance instance, final JComboBox<String> combo) {
//        assert SwingUtilities.isEventDispatchThread();
//
//        if (!(combo.getEditor().getEditorComponent() instanceof JTextComponent)) {
//            return;
//        }
//
//        RequestProcessor.getDefault().post(new Runnable() {
//            @Override
//            public void run() {
//                DockerAction facade = new DockerAction(instance);
//                List<DockerImage> images = facade.getImages();
//                final Set<String> repositories = new TreeSet<>();
//                for (DockerImage image : images) {
//                    for (DockerTag tag : image.getTags()) {
//                        int index = tag.getTag().lastIndexOf(':'); // NOI18N
//                        if (index > 0) {
//                            repositories.add(tag.getTag().substring(0, index));
//                        }
//                    }
//                }
//                SwingUtilities.invokeLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        // FIXME can we load items without clearing editor
//                        if (UiUtils.getValue(combo) == null) {
//                            int i = 0;
//                            for (String repo : repositories) {
//                                combo.insertItemAt(repo, i++);
//                            }
//                        }
//                    }
//                });
//            }
//        });
//
//    }
}
