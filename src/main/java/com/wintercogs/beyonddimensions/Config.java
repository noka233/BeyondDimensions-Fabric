package com.wintercogs.beyonddimensions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wintercogs.beyonddimensions.api.ButtonState;
import com.wintercogs.beyonddimensions.config.CommonConfigRuntime;
import com.wintercogs.beyonddimensions.config.ServerConfigRuntime;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class Config
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path COMMON_PATH = FabricLoader.getInstance().getConfigDir().resolve("beyonddimensions-common.json");
    public static final Path SERVER_PATH = FabricLoader.getInstance().getConfigDir().resolve("beyonddimensions-server.json");

    public static Config INSTANCE;

    public final CommonConfig commonConfig = new CommonConfig();
    public final ServerConfig serverConfig = new ServerConfig();

    private Config()
    {
    }

    public static void register()
    {
        INSTANCE = new Config();
        INSTANCE.commonConfig.load(COMMON_PATH);
        INSTANCE.serverConfig.load(SERVER_PATH);
    }

    private abstract static class JsonConfig
    {
        public void load(Path path)
        {
            if (path.getParent() != null)
            {
                path.getParent().toFile().mkdirs();
            }
            JsonObject json = new JsonObject();
            if (Files.exists(path))
            {
                try
                {
                    JsonElement element = JsonParser.parseString(Files.readString(path));
                    if (element.isJsonObject())
                    {
                        json = element.getAsJsonObject();
                    }
                }
                catch (Exception e)
                {
                    BeyondDimensions.LOGGER.error("Failed to load config {}", path, e);
                }
            }
            boolean changed = loadValues(json);
            if (changed || !Files.exists(path))
            {
                save(path);
            }
        }

        protected abstract boolean loadValues(JsonObject json);

        protected abstract void saveValues(JsonObject json);

        public void save(Path path)
        {
            JsonObject json = new JsonObject();
            saveValues(json);
            try
            {
                Files.writeString(path, GSON.toJson(json));
            }
            catch (Exception e)
            {
                BeyondDimensions.LOGGER.error("Failed to save config {}", path, e);
            }
        }
    }

    public static class CommonConfig extends JsonConfig
    {
        public ButtonState uiSortButton = ButtonState.SORT_NAME;
        public ButtonState uiSecondSortButton = ButtonState.SORT_INSERTED_TIME;
        public ButtonState uiReverseButton = ButtonState.DISABLED;
        public ButtonState uiSearchButton = ButtonState.DISABLED;
        public ButtonState uiCraftButton = ButtonState.DISABLED;
        public ButtonState uiCraftReturnButton = ButtonState.DISABLED;
        public int uiPageNum = 5;
        public String uiSearch = "";
        public boolean searchTextWithJEIEMI = true;
        public boolean emiAllowNetworkStorageInfo = false;
        public boolean interfaceCanReceiveResource = true;
        public boolean interfaceCanOutputResource = true;
        public boolean interfaceCanPopResource = true;
        public int interfaceUsableCapacity = 27;

        @Override
        protected boolean loadValues(JsonObject json)
        {
            boolean changed = false;
            changed |= readEnum(json, "ui_sort_button", uiSortButton, v -> uiSortButton = v);
            changed |= readEnum(json, "ui_second_sort_button", uiSecondSortButton, v -> uiSecondSortButton = v);
            changed |= readEnum(json, "ui_reverse_button", uiReverseButton, v -> uiReverseButton = v);
            changed |= readEnum(json, "ui_search_button", uiSearchButton, v -> uiSearchButton = v);
            changed |= readEnum(json, "ui_craft_button", uiCraftButton, v -> uiCraftButton = v);
            changed |= readEnum(json, "ui_craft_return_button", uiCraftReturnButton, v -> uiCraftReturnButton = v);
            changed |= readInt(json, "ui_page_num", uiPageNum, v -> uiPageNum = v);
            changed |= readString(json, "ui_search", uiSearch, v -> uiSearch = v);
            changed |= readBool(json, "search_text_with_jei_emi", searchTextWithJEIEMI, v -> searchTextWithJEIEMI = v);
            changed |= readBool(json, "emi_allow_network_storage_info", emiAllowNetworkStorageInfo, v -> emiAllowNetworkStorageInfo = v);
            changed |= readBool(json, "interface_can_receive_resource", interfaceCanReceiveResource, v -> interfaceCanReceiveResource = v);
            changed |= readBool(json, "interface_can_output_resource", interfaceCanOutputResource, v -> interfaceCanOutputResource = v);
            changed |= readBool(json, "interface_can_pop_resource", interfaceCanPopResource, v -> interfaceCanPopResource = v);
            changed |= readInt(json, "interface_usable_capacity", interfaceUsableCapacity, v -> interfaceUsableCapacity = v);
            applyRuntime();
            return changed;
        }

        @Override
        protected void saveValues(JsonObject json)
        {
            json.addProperty("ui_sort_button", uiSortButton.name());
            json.addProperty("ui_second_sort_button", uiSecondSortButton.name());
            json.addProperty("ui_reverse_button", uiReverseButton.name());
            json.addProperty("ui_search_button", uiSearchButton.name());
            json.addProperty("ui_craft_button", uiCraftButton.name());
            json.addProperty("ui_craft_return_button", uiCraftReturnButton.name());
            json.addProperty("ui_page_num", uiPageNum);
            json.addProperty("ui_search", uiSearch);
            json.addProperty("search_text_with_jei_emi", searchTextWithJEIEMI);
            json.addProperty("emi_allow_network_storage_info", emiAllowNetworkStorageInfo);
            json.addProperty("interface_can_receive_resource", interfaceCanReceiveResource);
            json.addProperty("interface_can_output_resource", interfaceCanOutputResource);
            json.addProperty("interface_can_pop_resource", interfaceCanPopResource);
            json.addProperty("interface_usable_capacity", interfaceUsableCapacity);
        }

        public void applyRuntime()
        {
            CommonConfigRuntime.uiSortButton = uiSortButton;
            CommonConfigRuntime.uiSecondSortButton = uiSecondSortButton;
            CommonConfigRuntime.uiReverseButton = uiReverseButton;
            CommonConfigRuntime.uiSearchButton = uiSearchButton;
            CommonConfigRuntime.uiCraftButton = uiCraftButton;
            CommonConfigRuntime.uiCraftReturnButton = uiCraftReturnButton;
            CommonConfigRuntime.uiPageNum = uiPageNum;
            CommonConfigRuntime.uiSearch = uiSearch;
            CommonConfigRuntime.searchTextWithJEIEMI = searchTextWithJEIEMI;
            CommonConfigRuntime.emiAllowNetworkStorageInfo = emiAllowNetworkStorageInfo;
            CommonConfigRuntime.interfaceCanReceiveResource = interfaceCanReceiveResource;
            CommonConfigRuntime.interfaceCanOutputResource = interfaceCanOutputResource;
            CommonConfigRuntime.interfaceCanPopResource = interfaceCanPopResource;
            CommonConfigRuntime.interfaceUsableCapacity = interfaceUsableCapacity;
        }
    }

    public static class ServerConfig extends JsonConfig
    {
        public long fragmentTransferTime = 3600L;
        public int crystalGenerateTime = 600;

        @Override
        protected boolean loadValues(JsonObject json)
        {
            boolean changed = false;
            changed |= readLong(json, "fragmentTransferTime", fragmentTransferTime, v -> fragmentTransferTime = v);
            changed |= readInt(json, "crystalGenerateTime", crystalGenerateTime, v -> crystalGenerateTime = v);
            applyRuntime();
            return changed;
        }

        @Override
        protected void saveValues(JsonObject json)
        {
            json.addProperty("fragmentTransferTime", fragmentTransferTime);
            json.addProperty("crystalGenerateTime", crystalGenerateTime);
        }

        public void applyRuntime()
        {
            ServerConfigRuntime.fragmentTransferTime = fragmentTransferTime;
            ServerConfigRuntime.crystalGenerateTime = crystalGenerateTime;
        }
    }

    private static boolean readInt(JsonObject json, String key, int def, java.util.function.IntConsumer setter)
    {
        if (json.has(key) && json.get(key).isJsonPrimitive())
        {
            setter.accept(json.get(key).getAsInt());
            return false;
        }
        setter.accept(def);
        return true;
    }

    private static boolean readLong(JsonObject json, String key, long def, java.util.function.LongConsumer setter)
    {
        if (json.has(key) && json.get(key).isJsonPrimitive())
        {
            setter.accept(json.get(key).getAsLong());
            return false;
        }
        setter.accept(def);
        return true;
    }

    private static boolean readBool(JsonObject json, String key, boolean def, java.util.function.Consumer<Boolean> setter)
    {
        if (json.has(key) && json.get(key).isJsonPrimitive())
        {
            setter.accept(json.get(key).getAsBoolean());
            return false;
        }
        setter.accept(def);
        return true;
    }

    private static boolean readString(JsonObject json, String key, String def, java.util.function.Consumer<String> setter)
    {
        if (json.has(key) && json.get(key).isJsonPrimitive())
        {
            setter.accept(json.get(key).getAsString());
            return false;
        }
        setter.accept(def);
        return true;
    }

    private static boolean readEnum(JsonObject json, String key, ButtonState def, java.util.function.Consumer<ButtonState> setter)
    {
        if (json.has(key) && json.get(key).isJsonPrimitive())
        {
            try
            {
                setter.accept(ButtonState.valueOf(json.get(key).getAsString()));
                return false;
            }
            catch (Exception ignored)
            {
            }
        }
        setter.accept(def);
        return true;
    }
}
