package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Session;
import com.ncb.rename.RenameContainerAction;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.actions.DeleteAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

class CassandraClusterFactory extends ChildFactory.Detachable<Cluster> implements Host.StateListener, NodeListener {

    private List<Cluster> clusters;
    private Session session;
    private ChangeListener listener;

    public CassandraClusterFactory() {
        this.clusters = new ArrayList<Cluster>();
    }

    @Override
    protected void addNotify() {
        PropertiesNotifier.addChangeListener(listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ev) {
                Cluster c = connectToCluster();
                if (c != null && !c.isClosed()) {
                    clusters.add(c);
                    refresh(true);
                }
            }
        });
    }

    @Override
    protected void removeNotify() {
        if (listener != null) {
            PropertiesNotifier.removeChangeListener(listener);
            listener = null;
        }
    }

    @Override
    protected boolean createKeys(List<Cluster> list) {
        list.addAll(clusters);
        return true;
    }

    @Override
    protected Node createNodeForKey(Cluster key) {
        ClusterNode node = null;
        try {
            node = new ClusterNode(key);
            node.addNodeListener(this);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    @Override
    public void childrenAdded(NodeMemberEvent nme) {}
    @Override
    public void childrenRemoved(NodeMemberEvent nme) {}
    @Override
    public void childrenReordered(NodeReorderEvent nre) {}
    @Override
    public void nodeDestroyed(NodeEvent ev) {
        clusters.remove(ev.getNode().getLookup().lookup(Cluster.class));
        refresh(true);
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {}

    private class ClusterNode extends BeanNode {

        @StaticResource
        private final String CASSANDRACLUSTERICON = "com/ncb/cassandra-cluster.png";
        private final Cluster key;

        private ClusterNode(Cluster key) throws IntrospectionException {
            this(key, new InstanceContent());
        }

        public ClusterNode(Cluster key, InstanceContent ic) throws IntrospectionException {
            super(key, Children.create(new CassandraContainerFactory(key, session), true), new AbstractLookup(ic));
            this.key = key;
            ic.add(key);
            ic.add(this);
            setDisplayName(key.getClusterName());
            setShortDescription("Clusters");
            setIconBaseWithExtension(CASSANDRACLUSTERICON);
        }

        @Override
        public boolean canRename() {
            return true;
        }

        @Override
        public Action getPreferredAction() {
            return null;
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{
                SystemAction.get(RenameContainerAction.class),
                SystemAction.get(DeleteAction.class),
                SystemAction.get(OpenLocalExplorerAction.class)
            };
        }

        @Override
        public boolean canDestroy() {
            return true;
        }

        @Override
        public void destroy() throws IOException {
            fireNodeDestroyed();
        }

    }
    
    private Cluster connectToCluster() {
        String cassandraCluster = NbPreferences.forModule(CassandraRootNode.class).get("cassandraCluster", "127.0.0.1:9042");
        String[] split = cassandraCluster.split(":");
        String host = split[0];
        String port = split[1];
        System.out.println("host = " + host);
        System.out.println("port = " + port);
        String clusterAuth = NbPreferences.forModule(CassandraRootNode.class).get(host + ":" + port, null);
        Builder builder = Cluster.builder();
        builder.addContactPointsWithPorts(Collections.singleton(new InetSocketAddress(host, Integer.valueOf(port))));
        if (clusterAuth != null) {
            String[] split2 = clusterAuth.split(":");
            String user = split2[0];
            String pass = split2[1];
            builder.withCredentials(user, pass);
        }
        Cluster cluster = builder.build();
        cluster.register(this);
        try {
            session = cluster.connect();
        } catch (com.datastax.driver.core.exceptions.NoHostAvailableException e) {
            JOptionPane.showMessageDialog(null, "Please start Cassandra first!");
        }
        System.out.println("Connected to cluster " + cluster.getClusterName());
        return cluster;
    }

    @Override
    public void onAdd(Host host) {
        refresh(true);
        System.out.println("added: " + host);
        StatusDisplayer.getDefault().setStatusText("added: " + host);
    }

    @Override
    public void onUp(Host host) {
        refresh(true);
        System.out.println("upped: " + host);
        StatusDisplayer.getDefault().setStatusText("upped: " + host);
    }

    @Override
    public void onRegister(Cluster clstr) {
    }

    @Override
    public void onUnregister(Cluster clstr) {
    }

    @Override
    public void onDown(Host host) {
        refresh(true);
        System.out.println("downed: " + host);
        StatusDisplayer.getDefault().setStatusText("downed: " + host);
    }

    @Override
    public void onRemove(Host host) {
        refresh(true);
        System.out.println("removed: " + host);
        StatusDisplayer.getDefault().setStatusText("removed: " + host);
    }

}
