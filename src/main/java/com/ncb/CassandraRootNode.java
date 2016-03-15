package com.ncb;

import com.ncb.server.CentralLookup;
import com.ncb.server.Startable;
import com.ncb.server.Stoppable;
import java.io.IOException;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.NewAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

//@ServicesTabNodeRegistration(
//        displayName = "Cassandra", 
//        name = "Cassandra", 
//        iconResource = "com/ncb/logo.png", 
//        shortDescription = "Cassandra")
public class CassandraRootNode extends AbstractNode {
    
    private CouchbaseServerStartable csStart = new CouchbaseServerStartable();
    private CouchbaseServerStoppable csStop = new CouchbaseServerStoppable();
    
    
    public CassandraRootNode() {
        super(Children.create(new CassandraClusterFactory(), true));
        CentralLookup.getDefault().add(csStart);
        setDisplayName("Cassandra");
        setShortDescription("Cassandra");
        setIconBaseWithExtension("com/ncb/logo.png");
    }
    
    private class CouchbaseServerStartable implements Startable {
        @Override
        public void start() {
            CentralLookup.getDefault().remove(csStart);
            StatusDisplayer.getDefault().setStatusText("Starting...");
            CentralLookup.getDefault().add(csStop);
        }
    }
    
    private class CouchbaseServerStoppable implements Stoppable {
        @Override
        public void stop() {
            CentralLookup.getDefault().remove(csStop);
            StatusDisplayer.getDefault().setStatusText("Stopping...");
            CentralLookup.getDefault().add(csStart);
        }
    }
    
    @Override
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            Utilities.actionsForPath("Actions/CouchbaseServer").get(0),
            Utilities.actionsForPath("Actions/CouchbaseServer").get(1),
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
                    msg.setInputText("127.0.0.1");
                    DialogDisplayer.getDefault().notify(msg);
                    String key = msg.getInputText();
                    if ("".equals(key)) {
                        return;
                    }
                    msg = new NotifyDescriptor.InputLine(
                            Bundle.LBL_NewProp_dialog_title2(), Bundle.MSG_NewProp_dialog_port());
                    msg.setInputText("9042");
                    DialogDisplayer.getDefault().notify(msg);
                    String value = msg.getInputText();
                    StatusDisplayer.getDefault().setStatusText(key+":"+value);
                    NbPreferences.forModule(CassandraRootNode.class).put("cassandraCluster", key+":"+value);
                    PropertiesNotifier.changed();
                }
            }
        };
    }
    
}
