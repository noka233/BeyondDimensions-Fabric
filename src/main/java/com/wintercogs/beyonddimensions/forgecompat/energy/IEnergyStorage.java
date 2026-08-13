package com.wintercogs.beyonddimensions.forgecompat.energy;

public interface IEnergyStorage
{
    int receiveEnergy(int maxReceive, boolean simulate);

    int extractEnergy(int maxExtract, boolean simulate);

    int getEnergyStored();

    int getMaxEnergyStored();

    boolean canExtract();

    boolean canReceive();
}
