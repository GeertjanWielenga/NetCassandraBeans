package com.ncb;

import java.io.IOException;
import javax.swing.Action;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.NewAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

@ServicesTabNodeRegistration(
        displayName = "Cassandra", 
        name = "Cassandra", 
        iconResource = "com/ncb/logo.png", 
        shortDescription = "Cassandra")
public class CassandraTab
        extends AbstractNode {

    public CassandraTab() {
        super(Children.create(new CassandraClusterFactory(), true));
        setDisplayName("Cassandra");
        setShortDescription("Cassandra");
        setIconBaseWithExtension("com/ncb/logo.png");
    }
    
    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            SystemAction.get(NewAction.class),
            null,
            SystemAction.get(OpenLocalExplorerAction.class)};
        return result;
    }
    
    @NbBundle.Messages({
        "LBL_NewProp=Cluster",
        "LBL_NewProp_dialog_title1=Host:",
        "LBL_NewProp_dialog_title2=Port:",
        "MSG_NewProp_dialog_host=Set Cluster Host",
        "MSG_NewProp_dialog_port=Set Cluster Port"})
    @Override
    public NewType[] getNewTypes() {
        return new NewType[]{
            new NewType() {
                @Override
                public String getName() {
                    return Bundle.LBL_NewProp();
                }
                @Override
                public void create() throws IOException {
                    NotifyDescriptor.InputLine msg = new NotifyDescriptor.InputLine(
                            Bundle.LBL_NewProp_dialog_title1(), Bundle.MSG_NewProp_dialog_host());
                    DialogDisplayer.getDefault().notify(msg);
                    String key = msg.getInputText();
                    if ("".equals(key)) {
                        return;
                    }
                    msg = new NotifyDescriptor.InputLine(
                            Bundle.LBL_NewProp_dialog_title2(), Bundle.MSG_NewProp_dialog_port());
                    DialogDisplayer.getDefault().notify(msg);
                    String value = msg.getInputText();
                    StatusDisplayer.getDefault().setStatusText(value);
//                    System.setProperty(key, value);
//                    PropertiesNotifier.changed();
                }
            }
        };
    }
    
}
