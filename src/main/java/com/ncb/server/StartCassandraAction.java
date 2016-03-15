package com.ncb.server;

import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

@ActionID(
        category = "CassandraServer",
        id = "org.sim.start.StartCassandraAction")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_StartCassandraAction")
@Messages("CTL_StartCassandraAction=Start Server")
public final class StartCassandraAction extends NodeAction {

    @Override
    protected void performAction(Node[] nodes) {
        CentralLookup.getDefault().lookup(Startable.class).start();
    }

    @Override
    protected boolean enable(Node[] nodes) {
        return CentralLookup.getDefault().lookup(Startable.class) != null;
    }

    @Override
    public String getName() {
        return Bundle.CTL_StartCassandraAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
