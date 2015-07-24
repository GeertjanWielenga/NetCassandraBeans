package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

class HostChildFactory extends ChildFactory<Host> {

    private final Cluster cluster;

    public HostChildFactory(Cluster cluster) {
        this.cluster = cluster;
    }

    @Override
    protected boolean createKeys(List<Host> list) {
        list.addAll(cluster.getMetadata().getAllHosts());
        return true;
    }

    @Override
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

}
