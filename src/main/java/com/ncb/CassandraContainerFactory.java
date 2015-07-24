package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Session;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
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

        public HostNode(CassandraContainerFactory.Key key, Cluster cluster) throws IntrospectionException {
            super(key, Children.create(new HostChildFactory(cluster), true), Lookups.singleton(key));
            setDisplayName("Hosts");
        }
    }

    private class KeySpaceNode extends BeanNode {

        public KeySpaceNode(CassandraContainerFactory.Key key, Cluster cluster) throws IntrospectionException {
            super(key, Children.create(new KeyspaceChildFactory(cluster, session), true), Lookups.singleton(key));
            setDisplayName("Keyspaces");
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
    public void onSuspected(Host host) {
        refresh(true);
        System.out.println("suspected: " + host);
        StatusDisplayer.getDefault().setStatusText("suspected: " + host);
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
