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

public class KeyspaceContainerChildFactory
        extends ChildFactory.Detachable<KeyspaceContainerChildFactory.Key> {

    private final Session session;
    private final Cluster cluster;

    public enum Key {
        SYSTEM, USER
    }

    public KeyspaceContainerChildFactory(Cluster cluster, Session session) {
        this.cluster = cluster;
        this.session = session;
    }

    @Override
    protected boolean createKeys(List<KeyspaceContainerChildFactory.Key> list) {
        list.add(Key.SYSTEM);
        list.add(Key.USER);
        return true;
    }

    @Override
    protected Node createNodeForKey(Key key) {
        switch (key) {
            case SYSTEM: {
                try {
                    return new SystemKeyspaceNode(key, cluster);
                } catch (IntrospectionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            case USER: {
                try {
                    return new UserKeyspaceNode(key, cluster);
                } catch (IntrospectionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return null;
    }

    private class SystemKeyspaceNode extends BeanNode {
        public SystemKeyspaceNode(KeyspaceContainerChildFactory.Key key, Cluster cluster) throws IntrospectionException {
            super(key, Children.create(new SystemKeyspaceChildFactory(cluster, session), true), Lookups.singleton(key));
            setDisplayName("System Keyspaces");
        }
    }

    private class UserKeyspaceNode extends BeanNode {
        public UserKeyspaceNode(KeyspaceContainerChildFactory.Key key, Cluster cluster) throws IntrospectionException {
            super(key, Children.create(new UserKeyspaceChildFactory(cluster, session), true), Lookups.singleton(key));
            setDisplayName("User Keyspaces");
        }
    }

}
