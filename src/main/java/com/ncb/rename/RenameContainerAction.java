package com.ncb.rename;

import com.datastax.driver.core.Cluster;
import java.awt.Dialog;
import java.util.logging.Logger;
import javax.swing.JButton;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.NodeAction;

public class RenameContainerAction extends NodeAction {

    private static final Logger LOGGER = Logger.getLogger(RenameContainerAction.class.getName());

    @NbBundle.Messages({
        "LBL_Rename=&Rename",
        "# {0} - container name",
        "LBL_RenameContainer=Rename {0}"
    })
    @Override
    protected void performAction(Node[] activatedNodes) {
        Node container = activatedNodes[0].getLookup().lookup(Node.class);
        if (container != null) {
            JButton renameButton = new JButton();
            Mnemonics.setLocalizedText(renameButton, Bundle.LBL_Rename());
            RenamePanel panel = new RenamePanel(renameButton);

            DialogDescriptor descriptor
                    = new DialogDescriptor(panel, Bundle.LBL_RenameContainer(container.getDisplayName()),
                            true, new Object[] {renameButton, DialogDescriptor.CANCEL_OPTION}, renameButton,
                            DialogDescriptor.DEFAULT_ALIGN, null, null);
            descriptor.setClosingOptions(new Object[] {renameButton, DialogDescriptor.CANCEL_OPTION});
            panel.setMessageLine(descriptor.createNotificationLineSupport());
            Dialog dlg = null;

            try {
                dlg = DialogDisplayer.getDefault().createDialog(descriptor);
                dlg.setVisible(true);

                if (descriptor.getValue() == renameButton) {
                    perform(container, panel.getContainerName());
                }
            } finally {
                if (dlg != null) {
                    dlg.dispose();
                }
            }
        }
    }

    @NbBundle.Messages({
        "# {0} - container name",
        "MSG_Renaming=Renaming {0}"
    })
    private void perform(final Node container, final String name) {
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                ProgressHandle handle = ProgressHandleFactory.createHandle("Renaming " + container.getDisplayName());
                handle.start();
                try {
//                    DockerAction facade = new DockerAction(container.getContainer().getInstance());
                    container.setDisplayName(name);
//                } catch (DockerException ex) {
//                    // FIXME inform user
//                    LOGGER.log(Level.INFO, null, ex);
                } finally {
                    handle.finish();
                }
            }
        });
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes.length != 1) {
            return false;
        }
        return activatedNodes[0].getLookup().lookup(Cluster.class) != null;
    }

    @NbBundle.Messages("LBL_RenameContainerAction=Rename...")
    @Override
    public String getName() {
        return Bundle.LBL_RenameContainerAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

}
