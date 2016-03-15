package com.ncb.server;

import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

@ActionID(
        category = "CassandraServer",
        id = "org.sim.stop.StopCassandraAction")
@ActionRegistration(
        lazy = false,
        displayName = "#CTL_StopCassandraAction")
@Messages("CTL_StopCassandraAction=Stop Server")
public final class StopCassandraAction extends NodeAction {

    @Override
    protected void performAction(Node[] nodes) {
        CentralLookup.getDefault().lookup(Stoppable.class).stop();
    }

    @Override
    protected boolean enable(Node[] nodes) {
        return CentralLookup.getDefault().lookup(Stoppable.class) != null;
    }

    @Override
    public String getName() {
        return Bundle.CTL_StopCassandraAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
