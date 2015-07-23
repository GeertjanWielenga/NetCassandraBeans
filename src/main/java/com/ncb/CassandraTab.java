package com.ncb;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class CassandraTab
        extends AbstractNode {

    public CassandraTab() {
        super(Children.create(new CassandraClusterFactory(), true));
        setDisplayName("Cassandra");
        setShortDescription("Cassandra");
        setIconBaseWithExtension("com/ncb/logo.png");
    }
}
