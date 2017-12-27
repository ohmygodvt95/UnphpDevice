/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.util.ArrayList;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;

/**
 *
 * @author Admins
 */
public class Controller {

    public Controller() {
    }

    public void setStatus(Device device, UpnpService upnpService, boolean status) {
        Service service = device.findService(new UDAServiceId("SwitchStatus"));
        ActionInvocation invocation = new ActionInvocation(service.getAction("SetTarget"));
        invocation.setInput("NewTargetValue", status);
        new ActionCallback.Default(invocation, upnpService.getControlPoint()).run();
    }
}
