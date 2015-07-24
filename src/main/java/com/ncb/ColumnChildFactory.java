package com.ncb;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.TableMetadata;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

class ColumnChildFactory extends ChildFactory<ColumnMetadata> {
    
    private final TableMetadata key;

    public ColumnChildFactory(TableMetadata key) {
        this.key = key;
    }

    @Override
    protected boolean createKeys(List<ColumnMetadata> list) {
        list.addAll(key.getColumns());
        return true;
    }

    @Override
    protected Node createNodeForKey(ColumnMetadata key) {
        BeanNode node = null;
        try {
            node = new BeanNode(key);
            node.setDisplayName(key.getName());
            node.setShortDescription(key.getType().getName().name());
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }
    
}
