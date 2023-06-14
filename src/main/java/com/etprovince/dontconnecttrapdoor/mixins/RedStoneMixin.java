package com.etprovince.dontconnecttrapdoor.mixins;

import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
public abstract class RedStoneMixin {

    @Shadow protected abstract boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor);

    @Shadow protected static boolean connectsTo(BlockState state, @Nullable Direction dir){
        if (state.isOf(Blocks.REDSTONE_WIRE)) {
            return true;
        } else if (state.isOf(Blocks.REPEATER)) {
            Direction direction = (Direction)state.get(RepeaterBlock.FACING);
            return direction == dir || direction.getOpposite() == dir;
        } else if (state.isOf(Blocks.OBSERVER)) {
            return dir == state.get(ObserverBlock.FACING);
        } else {
            return state.emitsRedstonePower() && dir != null;
        }
    }

    @Shadow protected static boolean connectsTo(BlockState state){
        return connectsTo(state, (Direction)null);
    };


    @Inject(method = "getRenderConnectionType(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)Lnet/minecraft/block/enums/WireConnection;",
            at = @At("HEAD"), cancellable = true)
    public void DontConnect(BlockView world, BlockPos pos, Direction direction, boolean bl, CallbackInfoReturnable<WireConnection> cir) {

        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl2 = this.canRunOnTop(world, blockPos, blockState);
        if(bl2 && connectsTo(world.getBlockState(blockPos.up()))){
            if(blockState.isSideSolidFullSquare(world, blockPos, direction.getOpposite())) {
                cir.setReturnValue(WireConnection.UP);
            } else {
                cir.setReturnValue(WireConnection.SIDE);
            }
        } else {
            if(!connectsTo(blockState, direction)
                    && (blockState.isSolidBlock(world, blockPos)
                    || !connectsTo(world.getBlockState(blockPos.down())))
            ) {
                cir.setReturnValue(WireConnection.NONE);
            } else {
                cir.setReturnValue(WireConnection.SIDE);
            }
        }
    }}