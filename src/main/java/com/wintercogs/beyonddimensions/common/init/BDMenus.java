package com.wintercogs.beyonddimensions.common.init;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.common.menu.*;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class BDMenus
{
    public static final MenuType<DimensionsNetMenu> Dimensions_Net_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "dimensions_net_menu"), DimensionsNetMenu::new);
    public static final MenuType<DimensionsCraftMenu> Dimensions_Craft_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "dimensions_craft_menu"), DimensionsCraftMenu::new);
    public static final MenuType<NetControlMenu> Net_Control_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_control_menu"), NetControlMenu::new);
    public static final MenuType<NetEnergyMenu> Net_Energy_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_energy_menu"), NetEnergyMenu::new);
    public static final MenuType<NetInterfaceBaseMenu> Net_Interface_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_interface_menu"), NetInterfaceBaseMenu::fromNetwork);
    public static final MenuType<DimensionsCraftMenuTerminal> Dimensions_Craft_Menu_Terminal = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "dimensions_craft_menu_terminal"), DimensionsCraftMenuTerminal::new);
    public static final MenuType<NetPumpMenu> Net_Pump_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_pump_menu"), NetPumpMenu::new);
    public static final MenuType<NetHopperMenu> Net_Hopper_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_hopper_menu"), NetHopperMenu::new);
    public static final MenuType<NetFurnaceMenu> Net_Furnace_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_furnace_menu"), NetFurnaceMenu::new);
    public static final MenuType<NetMagnetMenu> Net_Magnet_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_magnet_menu"), NetMagnetMenu::new);
    public static final MenuType<NetFeederMenu> Net_Feeder_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_feeder_menu"), NetFeederMenu::new);
    public static final MenuType<NetRestockerMenu> Net_Restocker_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "net_restocker_menu"), NetRestockerMenu::new);
    public static final MenuType<XpExchangeMenu> Xp_Exchange_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "xp_exchange_menu"), XpExchangeMenu::new);
    public static final MenuType<PrimaryNetSwitcherMenu> Primary_Net_Switcher_Menu = ScreenHandlerRegistry.registerExtended(new ResourceLocation(BDConstants.MODID, "primary_net_switcher_menu"), PrimaryNetSwitcherMenu::new);

    public static void register(com.wintercogs.beyonddimensions.forgecompat.eventbus.api.IEventBus eventBus)
    {
    }
}
