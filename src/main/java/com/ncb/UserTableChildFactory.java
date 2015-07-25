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

class UserTableChildFactory extends ChildFactory<TableMetadata> {

    private final KeyspaceMetadata key;
    private final Session session;

    public UserTableChildFactory(KeyspaceMetadata key, Session session) {
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
        UserTableNode node = null;
        try {
            node = new UserTableNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    private class UserTableNode extends BeanNode {

        private final TableMetadata tm;

        public UserTableNode(TableMetadata tm) throws IntrospectionException {
            super(tm, Children.create(new ColumnChildFactory(tm), true), Lookups.singleton(tm));
            this.tm = tm;
            setDisplayName(tm.getName());
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{new AbstractAction("Show Data") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    OutputWriter writer;
                    InputOutput io = IOProvider.getDefault().getIO("Cassandra Output", false);
                    writer = io.getOut();
                    ResultSet rs = session.execute("select * from " + tm.getKeyspace().getName() + "." + tm.getName());
                    writeColumnNames(rs, writer);
                    writeColumnContent(rs, writer);
                }

                private void writeColumnNames(ResultSet rs, OutputWriter writer) {
                    int lengthOfNames = 0;
                    ColumnDefinitions columnDefinitions = rs.getColumnDefinitions();
                    int noOfColumnDefinitions = columnDefinitions.size();
                    for (int i = 0; i < columnDefinitions.size(); i++) {
                        ColumnDefinitions.Definition cd = columnDefinitions.asList().get(i);
                        String name = cd.getName();
                        lengthOfNames = lengthOfNames + name.length();
                        writer.print(name);
                        writer.print(" | ");
                    }
                    writer.println("");
                    //write as many dashes below the column definitions
                    //as characters in the names together with the spaces and pipes
                    for (int j = 0; j < (lengthOfNames - 1) + (noOfColumnDefinitions * 3); j++) {
                        writer.print("-");
                    }
                    writer.println("");
                }

                private void writeColumnContent(ResultSet rs, OutputWriter writer) {
                    for (Row r : rs.all()) {
                        String content = "undefined";
                        ColumnDefinitions columnDefinitions = rs.getColumnDefinitions();
                        for (ColumnDefinitions.Definition cd : columnDefinitions) {
                            String name = cd.getName();
                            if (cd.getType() == DataType.cint()) {
                                content = String.valueOf(r.getInt(name));
                                writer.print(content);
                            }
                            if (cd.getType() == DataType.varchar()) {
                                content = r.getString(name);
                                writer.print(content);
                            }
                            if (cd.getType() == DataType.uuid()) {
                                content = r.getUUID(name).toString();
                                writer.print(content);
                            }
                            if (cd.getType() == DataType.cfloat()) {
                                content = String.valueOf(r.getFloat(name));
                                writer.print(content);
                            }
                            if (cd.getType() == DataType.cdouble()) {
                                content = String.valueOf(r.getDouble(name));
                                writer.print(content);
                            }
                            if (cd.getType() == DataType.list(DataType.text())) {
                                content = "list of items";
                                writer.print(content);
                            }
                            if (cd.getType() == DataType.timestamp()) {
                                content = r.getDate(name).toString();
                                writer.print(content);
                            }
                            int lengthOfColumn = name.length();
                            int lengthOfContent = content.length();
                            for (int j = 0; j < lengthOfColumn-lengthOfContent; j++) {
                                writer.print(" ");
                            }
                            writer.print(" | ");
                        }
                        writer.println("");
                    }
                    writer.println("");
                }
            }};
        }
    }

}
