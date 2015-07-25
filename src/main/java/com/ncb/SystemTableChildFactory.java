package com.ncb;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

class SystemTableChildFactory extends ChildFactory<TableMetadata> {

    private final KeyspaceMetadata key;
    private final Session session;

    public SystemTableChildFactory(KeyspaceMetadata key, Session session) {
        this.key = key;
        this.session = session;
    }

    @Override
    protected boolean createKeys(List<TableMetadata> list) {
        list.addAll(key.getTables());
        return true;
    }

    @Override
    protected Node createNodeForKey(TableMetadata key) {
        SystemTableNode node = null;
        try {
            node = new SystemTableNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    private class SystemTableNode extends BeanNode {
        private final TableMetadata tm;
        public SystemTableNode(TableMetadata tm) throws IntrospectionException {
            super(tm, Children.create(new ColumnChildFactory(tm), true), Lookups.singleton(tm));
            this.tm = tm;
            setDisplayName(tm.getName());
        }
    }

}
