package com.ncb;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.Exceptions;
import org.openide.util.actions.SystemAction;

class RowChildFactory extends ChildFactory<ColumnDefinitions.Definition> {

    private final ColumnDefinitions cds;
    private List<Row> rows;

    public RowChildFactory(ResultSet rs) {
        this.cds = rs.getColumnDefinitions();
        this.rows = rs.all();
    }

    @Override
    protected boolean createKeys(List<ColumnDefinitions.Definition> list) {
        for (ColumnDefinitions.Definition cd : cds) {
            list.add(cd);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(ColumnDefinitions.Definition key) {
        RowNode node = null;
        try {
            node = new RowNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    private class RowNode extends BeanNode {
        ColumnDefinitions.Definition key = null;
        public RowNode(ColumnDefinitions.Definition key) throws IntrospectionException {
            super(key);
            this.key = key;
            setDisplayName(key.getName());
        }
        @Override
        public PropertySet[] getPropertySets() {
            Set set = Sheet.createPropertiesSet();
            for (int i = 0; i < cds.size(); i++) {
//                Row r = rows.get(i);
                ColumnDefinitions.Definition def = cds.asList().get(i);
                set.put(new PropertySupport.ReadOnly<String>(
                        def.getName(), String.class, def.getName(), null) {
                            @Override
                            public String getValue() throws IllegalAccessException, InvocationTargetException {
                                String name = def.getName();
                                return def.getType().toString();
                            }
                        });
            }
            PropertySet[] original = super.getPropertySets();
            PropertySet[] withLayer = new PropertySet[original.length + 1];
            System.arraycopy(original, 0, withLayer, 0, original.length);
            withLayer[withLayer.length - 1] = set;
            return withLayer;
        }
    }

}
