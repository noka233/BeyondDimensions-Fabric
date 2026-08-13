package com.wintercogs.beyonddimensions.client.init;

import com.wintercogs.beyonddimensions.client.gui.*;
import com.wintercogs.beyonddimensions.common.menu.DimensionsCraftMenu;
import com.wintercogs.beyonddimensions.common.menu.DimensionsCraftMenuTerminal;
import com.wintercogs.beyonddimensions.common.menu.DimensionsNetMenu;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

import static com.wintercogs.beyonddimensions.common.init.BDMenus.*;

public class BDScreens
{
    public static void registerScreens()
    {
        ScreenRegistry.<DimensionsNetMenu, DimensionsNetGUI<DimensionsNetMenu>>register(Dimensions_Net_Menu, DimensionsNetGUI::new);
        ScreenRegistry.<DimensionsCraftMenu, DimensionsCraftGUI<DimensionsCraftMenu>>register(Dimensions_Craft_Menu, DimensionsCraftGUI::new);
        ScreenRegistry.register(Net_Control_Menu, NetControlGUI::new);
        ScreenRegistry.register(Net_Interface_Menu, NetInterfaceBaseGUI::new);
        ScreenRegistry.register(Net_Energy_Menu, NetEnergyGUI::new);
        ScreenRegistry.<DimensionsCraftMenuTerminal, DimensionsTerminalCraftGUI>register(Dimensions_Craft_Menu_Terminal, DimensionsTerminalCraftGUI::new);
        ScreenRegistry.register(Net_Pump_Menu, NetPumpGUI::new);
        ScreenRegistry.register(Net_Hopper_Menu, NetHopperGUI::new);
        ScreenRegistry.register(Net_Furnace_Menu, NetFurnaceGUI::new);
        ScreenRegistry.register(Net_Magnet_Menu, NetMagnetGUI::new);
        ScreenRegistry.register(Net_Feeder_Menu, NetFeederGUI::new);
        ScreenRegistry.register(Net_Restocker_Menu, NetRestockerGUI::new);
        ScreenRegistry.register(Xp_Exchange_Menu, XpExchangeGUI::new);
        ScreenRegistry.register(Primary_Net_Switcher_Menu, PrimaryNetSwitcherGUI::new);
    }
}
