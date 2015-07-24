package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import java.beans.IntrospectionException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

class CassandraClusterFactory extends ChildFactory.Detachable<Cluster>
        implements Host.StateListener {

    Cluster connectToCluster;
    Session session;

    public CassandraClusterFactory() {
        this.connectToCluster = connectToCluster();
    }

    @Override
    protected boolean createKeys(List<Cluster> list) {
//        Metadata md = this.connectToCluster.getMetadata();
//        list.addAll(md.getAllHosts());
        list.add(connectToCluster);
        return true;
    }

    @Override
    protected Node createNodeForKey(Cluster key) {
        ClusterNode node = null;
        try {
            node = new ClusterNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    private class ClusterNode extends BeanNode {

        public ClusterNode(Cluster key) throws IntrospectionException {
            super(key, Children.create(new CassandraContainerFactory(key, session), true));
            setDisplayName(key.getClusterName());
        }
    }

    private Cluster connectToCluster() {
        Cluster cluster = Cluster.builder().
                addContactPointsWithPorts(Collections.singleton(new InetSocketAddress("127.0.0.1", 9042))).
                build();
        cluster.register(this);
        session = cluster.connect();
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
