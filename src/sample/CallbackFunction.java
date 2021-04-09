package sample;


import javafx.scene.control.Label;
import sample.wirelessfileslistview.WirelessListEntry;

public interface CallbackFunction {
    void onFinish(WirelessListEntry label);
    void onMetaDataFinish();
}
