package com.ncb;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.db.explorer.node.NodeProvider;
import org.netbeans.api.db.explorer.node.NodeProviderFactory;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

public class CassandraNodeProvider extends NodeProvider {

    public static NodeProviderFactory getFactory() {
        return FactoryHolder.FACTORY;
    }

    private static class FactoryHolder {
        static final NodeProviderFactory FACTORY = new NodeProviderFactory() {
            @Override
            public CassandraNodeProvider createInstance(Lookup lookup) {
                CassandraNodeProvider provider = new CassandraNodeProvider(lookup);
                return provider;
            }
        };
    }

    private CassandraNodeProvider(Lookup lookup) {
        super(lookup);
    }

    @Override
    protected synchronized void initialize() {
        List<Node> newList = new ArrayList<Node>();
        newList.add(new UniverseNode());
        setNodes(newList);
    }

    private class UniverseNode extends AbstractNode {

        public UniverseNode() {
            super(Children.LEAF);
            setDisplayName("Universe");
        }

    }

}