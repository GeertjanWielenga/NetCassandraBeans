package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Session;
import java.beans.IntrospectionException;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

public class CassandraContainerFactory
        extends ChildFactory.Detachable<CassandraContainerFactory.Key>
        implements Host.StateListener {

    private Session session;
    private final Cluster cluster;

    public enum Key {

        HOSTS, KEYSPACES
    }

    public CassandraContainerFactory(Cluster cluster, Session session) {
        this.cluster = cluster;
        this.session = session;
    }

    @Override
    protected boolean createKeys(List<CassandraContainerFactory.Key> list) {
//        Metadata md = cluster.getMetadata();
        list.add(Key.HOSTS);
        list.add(Key.KEYSPACES);
//        list.add(Key.HOSTS);
//        list.addAll(md.getAllHosts());
        return true;
    }

    @Override
    protected Node createNodeForKey(Key key) {
        switch (key) {
            case HOSTS: {
                try {
                    return new HostNode(key, cluster);
                } catch (IntrospectionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            case KEYSPACES: {
                try {
                    return new KeySpaceNode(key, cluster);
                } catch (IntrospectionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return null;
    }

    private class HostNode extends BeanNode {
        @StaticResource
        private final String CASSANDRANODEICON = "com/ncb/cassandra-node.png";
        public HostNode(CassandraContainerFactory.Key key, Cluster cluster) throws IntrospectionException {
            super(key, Children.create(new HostChildFactory(cluster), true), Lookups.singleton(key));
            setDisplayName("Nodes");
            setShortDescription("Nodes");
            setIconBaseWithExtension(CASSANDRANODEICON);
        }
        @Override
        public Action getPreferredAction() {
            return null;
        }
        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{SystemAction.get(OpenLocalExplorerAction.class)};
        }
    }

    private class KeySpaceNode extends BeanNode {
        @StaticResource
        private final String CASSANDRADATAICON = "com/ncb/cassandra-data.png";
        public KeySpaceNode(CassandraContainerFactory.Key key, Cluster cluster) throws IntrospectionException {
            super(key, Children.create(new KeyspaceContainerChildFactory(cluster, session), true), Lookups.singleton(key));
            setDisplayName("Keyspaces");
            setShortDescription("Data");
            setIconBaseWithExtension(CASSANDRADATAICON);
        }
        @Override
        public Action getPreferredAction() {
            return null;
        }
        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{SystemAction.get(OpenLocalExplorerAction.class)};
        }
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
