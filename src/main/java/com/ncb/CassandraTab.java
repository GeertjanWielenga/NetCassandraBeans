package com.ncb;

import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

@ServicesTabNodeRegistration(displayName = "Cassandra", name = "Cassandra", iconResource = "com/ncb/logo.png", shortDescription = "Cassandra")
public class CassandraTab
        extends AbstractNode {

    public CassandraTab() {
        super(Children.create(new CassandraClusterFactory(), true));
        setDisplayName("Cassandra");
        setShortDescription("Cassandra");
        setIconBaseWithExtension("com/ncb/logo.png");
    }
}
