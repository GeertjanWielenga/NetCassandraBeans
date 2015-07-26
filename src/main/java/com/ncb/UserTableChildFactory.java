package com.ncb;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.table.TableColumnModel;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;
import org.netbeans.swing.outline.Outline;
import org.openide.actions.OpenAction;
import org.openide.cookies.OpenCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

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

    private class UserTableView extends TopComponent implements ExplorerManager.Provider {
        private final ExplorerManager em = new ExplorerManager();
        private UserTableView(String tmdName, ResultSet rs) {
            setDisplayName(tmdName);
            setLayout(new BorderLayout());
            try {
                em.setRootContext(new RootNode(rs));
                OutlineView ov = new OutlineView();
                Outline outline = ov.getOutline();
                for (ColumnDefinitions.Definition cd : rs.getColumnDefinitions()) {
                    String name = cd.getName();
                    ov.addPropertyColumn(name, name, cd.getKeyspace());
                }
                outline.setRootVisible(false);
                TableColumnModel columnModel = outline.getColumnModel();
                ETableColumn column0 = (ETableColumn) columnModel.getColumn(0);
                ((ETableColumnModel) columnModel).setColumnHidden(column0, true);
                add(ov, BorderLayout.CENTER);
                associateLookup(Lookups.singleton(rs));
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        @Override
        public ExplorerManager getExplorerManager() {
            return em;
        }
    }

    private class RootNode extends AbstractNode {
        public RootNode(ResultSet rs) throws IntrospectionException {
            super(Children.create(new RowChildFactory(rs), true));
        }
    }

    private class UserTableNode extends BeanNode {

        private final TableMetadata tmd;

        public UserTableNode(TableMetadata tmd) throws IntrospectionException {
            this(tmd, new InstanceContent());
        }

        private UserTableNode(TableMetadata tmd, InstanceContent ic) throws IntrospectionException {
            super(tmd, Children.create(new ColumnChildFactory(tmd), true), new AbstractLookup(ic));
            this.tmd = tmd;
            ResultSet rs = session.execute("select * from " + tmd.getKeyspace().getName() + "." + tmd.getName());
            ic.add(tmd);
            ic.add(new OpenCookie() {
                @Override
                public void open() {
                    TopComponent tc = findTopComponent(rs);
                    if (tc == null) {
                        tc = new UserTableView(tmd.getName(), rs);
                        tc.open();
                    }
                    tc.requestActive();
                }
            });
            setDisplayName(tmd.getName());
        }

        private TopComponent findTopComponent(ResultSet rs) {
            Set<TopComponent> openTopComponents = WindowManager.getDefault().getRegistry().getOpened();
            for (TopComponent tc : openTopComponents) {
                if (tc.getLookup().lookup(ResultSet.class) == rs) {
                    return tc;
                }
            }
            return null;
        }

        @Override
        public Action getPreferredAction() {
            return SystemAction.get(OpenAction.class);
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{SystemAction.get(OpenAction.class), new AbstractAction("Dump Data") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final String tmdName = tmd.getName();
                    OutputWriter writer;
                    InputOutput io = IOProvider.getDefault().getIO("Cassandra Output", false);
                    writer = io.getOut();
                    ResultSet rs = session.execute("select * from " + tmd.getKeyspace().getName() + "." + tmdName);
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
                            for (int j = 0; j < lengthOfColumn - lengthOfContent; j++) {
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
