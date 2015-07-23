package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import java.beans.IntrospectionException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

public class CassandraClusterFactory
        extends ChildFactory.Detachable<Host>
        implements Host.StateListener {

    Cluster connectToCluster;

    public CassandraClusterFactory() {
        this.connectToCluster = connectToCluster();
    }

    protected boolean createKeys(List<Host> list) {
        Metadata md = this.connectToCluster.getMetadata();
        list.addAll(md.getAllHosts());
        return true;
    }

    protected Node createNodeForKey(Host key) {
        BeanNode node = null;
        try {
            node = new BeanNode(key);
            node.setDisplayName(key.getAddress().getHostName());
            node.setShortDescription(key.getAddress().getHostAddress());
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    private Cluster connectToCluster() {
        Cluster cluster = Cluster.builder().addContactPointsWithPorts(Collections.singleton(new InetSocketAddress("some-ip-adddress", 9042))).build();

        cluster.register(this);
        System.out.println("Connected to cluster " + cluster.getClusterName());
        return cluster;
    }

    public void onAdd(Host host) {
        refresh(true);
        System.out.println("added: " + host);
        StatusDisplayer.getDefault().setStatusText("added: " + host);
    }

    public void onUp(Host host) {
        refresh(true);
        System.out.println("upped: " + host);
        StatusDisplayer.getDefault().setStatusText("upped: " + host);
    }

    public void onSuspected(Host host) {
        refresh(true);
        System.out.println("suspected: " + host);
        StatusDisplayer.getDefault().setStatusText("suspected: " + host);
    }

    public void onDown(Host host) {
        refresh(true);
        System.out.println("downed: " + host);
        StatusDisplayer.getDefault().setStatusText("downed: " + host);
    }

    public void onRemove(Host host) {
        refresh(true);
        System.out.println("removed: " + host);
        StatusDisplayer.getDefault().setStatusText("removed: " + host);
    }
}
