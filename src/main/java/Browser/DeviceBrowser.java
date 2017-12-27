/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Browser;

import Controller.Controller;
import java.util.Map;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.STAllHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UDAServiceId;

/**
 *
 * @author Admins
 */
public class DeviceBrowser {

    Controller controller = new Controller();
    boolean checkK = false;
    boolean checkR = false;

    public DeviceBrowser() {
        upnpService = new UpnpServiceImpl(listener);
        upnpService.getControlPoint().search(new STAllHeader());
    }

    UpnpService upnpService;

    RegistryListener listener = new RegistryListener() {

        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
        }//"Discovery started: " + device.getDisplayString()

//                        "Discovery failed: " + device.getDisplayString()
        public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
        }

        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
            System.out.println("Remote device available: " + device.getDisplayString());
            setDevice(device);
            if (device == deviceK || device == deviceR) {
                SubscriptionCallback subscriptionCallback = new SubscriptionCallback(device.findService(
                        new UDAServiceId("SwitchStatus")
                )) {
                    @Override
                    protected void failed(GENASubscription genaSubscription, UpnpResponse upnpResponse, Exception e, String s) {
                    }

                    @Override
                    protected void established(GENASubscription genaSubscription) {
                    }

                    @Override
                    protected void ended(GENASubscription genaSubscription, CancelReason cancelReason, UpnpResponse upnpResponse) {
                    }

                    @Override
                    protected void eventReceived(GENASubscription genaSubscription) {
                        if (device == deviceK) {
                            checkK = true;
                            if (checkK == true && checkR == true) {
                                checkK = false;
                                checkR = false;
                                return;
                            }
                            Map<String, StateVariableValue> values = genaSubscription.getCurrentValues();
                            StateVariableValue status = values.get("Status");
                            int value = Integer.parseInt(status.toString());
                            if (deviceR != null) {
                                if (value == 0) {
                                    controller.setStatus(deviceR, upnpService, true);
                                }
                                if (value == 1) {
                                    controller.setStatus(deviceR, upnpService, false);
                                }
                            }
                        }
                        if (device == deviceR) {
                            checkR = true;
                            if (checkK == true && checkR == true) {
                                checkK = false;
                                checkR = false;
                                return;
                            }
                            Map<String, StateVariableValue> values = genaSubscription.getCurrentValues();
                            StateVariableValue status = values.get("Status");
                            int value = Integer.parseInt(status.toString());
                            if (deviceK != null) {
                                if (value == 0) {
                                    controller.setStatus(deviceK, upnpService, true);
                                }
                                if (value == 1) {
                                    controller.setStatus(deviceK, upnpService, false);
                                }
                            }
                        }
                    }

                    @Override
                    protected void eventsMissed(GENASubscription genaSubscription, int i) {
                    }
                };
                upnpService.getControlPoint().execute(subscriptionCallback);
            }
        }

        public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
        }//"Remote device updated: " + device.getDisplayString()

        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
            System.out.println("Remote device removed: " + device.getDisplayString());
        }

        public void localDeviceAdded(Registry registry, LocalDevice device) {
        } //"Local device added: " + device.getDisplayString()

        public void localDeviceRemoved(Registry registry, LocalDevice device) {
        }//"Local device removed:

        public void beforeShutdown(Registry registry) {
        }//"Before shutdown, the registry has devices: " + registry.getDevices().size()

        public void afterShutdown() {
        }//"Shutdown of registry complete!
    };

    public void setDevice(RemoteDevice device) {
        if (device.getDetails().getFriendlyName().compareTo(QUAT) == 0
                || device.getDetails().getFriendlyName().compareTo(DIEUHOA) == 0) {
            if (deviceK == null) {
                System.out.println("K");
                deviceK = device;
                return;
            } else if (deviceR == null) {
                System.out.println("R");
                deviceR = device;
                return;
            } else {
                return;
            }
        }
    }

    private Device deviceR = null;
    private Device deviceK = null;

    public static void main(String[] args) {
        new DeviceBrowser();
    }

    final String QUAT = "Quyen Quat";
    final String DIEUHOA = "Quyen Dieu Hoa";
}
