# Beyond Dimensions (Fabric 1.20.1)

> **原作者声明**：本项目是 [BeyondDimensions](https://github.com/Frostbite-time/BeyondDimensions) 的 Fabric 1.20.1 移植版，原作者为 **Frostbite-time**，原模组以 MIT License 开源，代码版权归原作者所有。

> **AI 移植声明**：本移植版由 **AI（deepseek-v4-flash）辅助移植**，在人工指导下基于 Forge 1.20.1 原版源码改造而来，移植工作与原作者无关。移植过程中产生的功能差异或问题请以原模组为准。

## 功能

- 维度网络：虚拟存储系统，支持物品、流体（经验流体）、FE 能量
- 网络控制方块、网络接口、网络通道、网络终端、网络泵/漏斗/熔炉/磁铁/喂食器/补货器/经验交换等机器
- 成员邀请、权限管理、主网络切换
- 快捷键：O 打开维度网络、P 打开合成终端、[ 切换磁铁、U 打开主网络切换等
- JSON 配置：`config/beyonddimensions-common.json`、`config/beyonddimensions-server.json`

## 移植说明

- 使用 Mojang 官方映射（`loom.officialMojangMappings()`），Forge 源码方法名与 Fabric 编译目标一致
- 在模组内实现了 Forge API 兼容层（`forgecompat` 包）：IItemHandler / IFluidHandler / IEnergyStorage / FluidStack / DeferredRegister / SimpleChannel / 事件总线等，368 个源文件大部分零改动
- Forge 事件总线桥接到 Fabric：服务器生命周期、服务端/客户端 tick、命令注册
- 网络：SimpleChannel 兼容层桥接 `ServerPlayNetworking` / `ClientPlayNetworking`
- 菜单：`ScreenHandlerRegistry.registerExtended` + `NetworkHooks` 兼容层（自动包装 ExtendedScreenHandlerFactory）
- 方块能力（capability）：`WorldlyContainer` 未接入，先以模组内部接口 + `CapabilityCompat` 静态辅助保持核心存储逻辑运行
- accesstransformer → access widener（容器同步方法、Slot 坐标、AbstractWidget.height 等）
- 客户端专属代码使用 `@Environment(EnvType.CLIENT)`，Fabric Loader 在服务端自动剥离

## 已知裁剪（第三方集成）

- 20 个第三方集成模块（131 个文件）未移植：AE2、Create、Botania、Ars Nouveau、Refined Storage、Mekanism、Curios、EMI、JEI、Jade、KubeJS、Polymorph 等
- 对应功能降级：Curios 槽位支持、JEI/EMI 搜索同步、自定义槽位渲染钩子、Create 条件配方等
- 核心维度网络与所有原生机器功能完整保留

## 已知状态（未经测试）

以下物品/方块尚未经过完整测试，功能可能存在问题或与原版行为有差异，使用前请知悉：

- 成员连接符（`net_member_inviter`）
- 管理员连接符（`net_manager_inviter`）
- 网络赠送符（`net_gifter`）
- 网络自毁符（`net_destroyer`）
- 压缩物质球（`matter_compress_ball`）
- 便携网络终端（`net_terminal_item`）
- 网络终端（`net_terminal_block`）
- 维度网络能量通道（`net_energy_pathway`）

## 许可证

本项目是 [BeyondDimensions](https://github.com/Frostbite-time/BeyondDimensions)（原模组，MIT License）的 Fabric 1.20.1 移植版。

- 原模组版权：Copyright (c) 2025 Frostbite-time
- 移植版同样以 [MIT License](LICENSE) 发布
