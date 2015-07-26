package com.ncb;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
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

class RowChildFactory extends ChildFactory<ColumnDefinitions.Definition> {

    private final ColumnDefinitions cds;
    private ResultSet rs;
    private String content;

    public RowChildFactory(ResultSet rs) {
        this.cds = rs.getColumnDefinitions();
        this.rs = rs;
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
                ColumnDefinitions.Definition def = cds.asList().get(i);
                PropertySupport.ReadOnly<String> ValuePropertySupport = ValuePropertySupport(def);
                ValuePropertySupport.setValue("suppressCustomEditor", Boolean.TRUE);
                set.put(ValuePropertySupport);
            }
            PropertySet[] original = super.getPropertySets();
            PropertySet[] withLayer = new PropertySet[original.length + 1];
            System.arraycopy(original, 0, withLayer, 0, original.length);
            withLayer[withLayer.length - 1] = set;
            return withLayer;
        }

        private PropertySupport.ReadOnly<String> ValuePropertySupport(ColumnDefinitions.Definition def) {
            return new PropertySupport.ReadOnly<String>(
                    def.getName(), String.class, def.getName(), null) {
                        @Override
                        public String getValue() throws IllegalAccessException, InvocationTargetException {
                            getValueFromRows(rs, def);
                            return content;
                        }
                    };
        }
    }

    private void getValueFromRows(ResultSet rs, ColumnDefinitions.Definition cd) {
        for (Row r : rs.all()) {
            String name = cd.getName();
            if (cd.getType() == DataType.cint()) {
                content = String.valueOf(r.getInt(name));
            }
            if (cd.getType() == DataType.varchar()) {
                content = r.getString(name);
            }
            if (cd.getType() == DataType.uuid()) {
                content = r.getUUID(name).toString();
            }
            if (cd.getType() == DataType.cfloat()) {
                content = String.valueOf(r.getFloat(name));
            }
            if (cd.getType() == DataType.cdouble()) {
                content = String.valueOf(r.getDouble(name));
            }
            if (cd.getType() == DataType.list(DataType.text())) {
                content = "list of items";
            }
            if (cd.getType() == DataType.timestamp()) {
                content = r.getDate(name).toString();
            }
        }
    }

}
