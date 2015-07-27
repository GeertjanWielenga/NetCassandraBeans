package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import java.beans.IntrospectionException;
import java.util.List;
import javax.swing.Action;
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
        HostNode node = null;
        try {
            node = new HostNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    private class HostNode extends BeanNode {
        public HostNode(Host key) throws IntrospectionException {
            super(key);
            setDisplayName(key.getAddress().getHostName());
            setShortDescription(key.getAddress().getHostAddress());
        }
        @Override
        public Action getPreferredAction() {
            return null;
        }
        @Override
        public Action[] getActions(boolean context) {
            return null;
        }
    }

}
