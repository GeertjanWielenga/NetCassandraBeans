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
    
    private CassandraServerStartable csStart = new CassandraServerStartable();
    private CassandraServerStoppable csStop = new CassandraServerStoppable();
    
    
    public CassandraRootNode() {
        super(Children.create(new CassandraClusterFactory(), true));
        CentralLookup.getDefault().add(csStart);
        setDisplayName("Cassandra");
        setShortDescription("Cassandra");
        setIconBaseWithExtension("com/ncb/logo.png");
    }
    
    private class CassandraServerStartable implements Startable {
        @Override
        public void start() {
            CentralLookup.getDefault().remove(csStart);
            StatusDisplayer.getDefault().setStatusText("Starting...");
            CentralLookup.getDefault().add(csStop);
        }
    }
    
    private class CassandraServerStoppable implements Stoppable {
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
            Utilities.actionsForPath("Actions/CassandraServer").get(0),
            Utilities.actionsForPath("Actions/CassandraServer").get(1),
            SystemAction.get(NewAction.class),
            null,
            SystemAction.get(OpenLocalExplorerAction.class)};
        return result;
    }
    
    @NbBundle.Messages({
        "LBL_NewProp=Cluster",
        "LBL_NewProp_dialog_title1=Host:",
        "LBL_NewProp_dialog_title2=Port:",
        "LBL_NewProp_dialog_title3=Authentication Required?",
        "LBL_NewProp_dialog_title4=Username:",
        "LBL_NewProp_dialog_title5=Password:",
        "MSG_NewProp_dialog_host=Set Cluster Host",
        "MSG_NewProp_dialog_port=Set Cluster Port",
        "MSG_NewProp_dialog_authentication=Authentication Required",
        "MSG_NewProp_dialog_username=Set User Name",
        "MSG_NewProp_dialog_password=Set Password",
    })
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
                    NotifyDescriptor.Confirmation authNeeded = new NotifyDescriptor.Confirmation(
                            Bundle.LBL_NewProp_dialog_title3(), NotifyDescriptor.YES_NO_OPTION);
                    DialogDisplayer.getDefault().notify(authNeeded);
                    if ( authNeeded.getValue() == NotifyDescriptor.YES_OPTION ) {
                        msg = new NotifyDescriptor.InputLine(
                                Bundle.LBL_NewProp_dialog_title4(), Bundle.MSG_NewProp_dialog_username());
                        msg.setInputText("username");
                        DialogDisplayer.getDefault().notify(msg);
                        String user = msg.getInputText();
                        InputSecret msg3 = new InputSecret(
                                Bundle.LBL_NewProp_dialog_title5(), Bundle.MSG_NewProp_dialog_password());
                        msg3.setInputText("password");
                        DialogDisplayer.getDefault().notify(msg3);
                        String pass = msg3.getInputText();
                        StatusDisplayer.getDefault().setStatusText(key+":"+value);
                        NbPreferences.forModule(CassandraRootNode.class).put("cassandraCluster", key+":"+value);
                        NbPreferences.forModule(CassandraRootNode.class).put(key+":"+value, user+":"+pass);
                    } else {
                        NbPreferences.forModule(CassandraRootNode.class).put("cassandraCluster", key+":"+value);
                        StatusDisplayer.getDefault().setStatusText(key+":"+value);
                    }
                    PropertiesNotifier.changed();
                }
            }
        };
    }
    
}
