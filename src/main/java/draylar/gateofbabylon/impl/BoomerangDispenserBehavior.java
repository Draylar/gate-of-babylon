package draylar.gateofbabylon.impl;

import draylar.gateofbabylon.entity.BoomerangEntity;
import draylar.gateofbabylon.item.BoomerangItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BoomerangDispenserBehavior implements DispenserBehavior {

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
        BlockPos facing = pointer.getPos().offset(direction);

        if(stack.getItem() instanceof BoomerangItem boomerangItem) {
            BoomerangEntity boomerang = boomerangItem.createBoomerang(stack, pointer.getWorld());
            boomerang.updatePosition(facing.getX() + 0.5, facing.getY() + 0.5, facing.getZ() + 0.5);
            boomerang.setVelocity(direction.getOffsetX(), (float)direction.getOffsetY() + 0.1F, direction.getOffsetZ());
            boomerang.setTemporary();
            pointer.getWorld().spawnEntity(boomerang);
        }

        return stack;
    }
}
