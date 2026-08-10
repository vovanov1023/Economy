package me.vovanov.economy.model;

public class ATM {
    final int id;
    final int x;
    final int y;
    final int z;
    final String worldName;

    public ATM(int id, int x, int y, int z, String worldName) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorldName() {
        return worldName;
    }

}
