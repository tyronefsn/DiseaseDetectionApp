package com.example.diseasedetectionapp;

public class WaterManagement {
    private int vegStageStandingWater;
    private int vegSafe;
    private int repStageStandingWater0;
    private int repSafe;
    private int repStageStandingWater1;
    private int ripStageStandingWater;
    private int ripSafe;
    private int ripTerminal;

    // recommended water level
    public WaterManagement() {
        this.vegStageStandingWater = 10;
        this.vegSafe = 50;
        this.repStageStandingWater0 = 10;
        this.repSafe = 17;
        this.repStageStandingWater1 = 3;
        this.ripStageStandingWater = 3;
        this.ripSafe = 12;
        this.ripTerminal = 15;
    }
    // Getters
    public int getVegetativeGrowthStage() {
        return (this.vegStageStandingWater + this.vegSafe);
    }

    public int getReproductiveStage() {
        return (this.repStageStandingWater0 + this.repStageStandingWater1 + this.repSafe);
    }

    public int getRipStage() {
        return (this.ripStageStandingWater + this.ripSafe + this.ripTerminal);
    }
    // Setters
    public void setVegStageStandingWater(int vegStageStandingWater) {
        this.vegStageStandingWater = vegStageStandingWater;
    }
    public void setVegSafe(int vegSafe) {
        this.vegSafe = vegSafe;
    }
    public void setRepStageStandingWater0(int repStageStandingWater0) {
        this.repStageStandingWater0 = repStageStandingWater0;
    }
    public void setRepStageStandingWater1(int repStageStandingWater1) {
        this.repStageStandingWater1 = repStageStandingWater1;
    }
    public void setRepSafe(int repSafe) {
        this.repSafe = repSafe;
    }
    public void setRipStageStandingWater(int ripStageStandingWater) {
        this.ripStageStandingWater = ripStageStandingWater;
    }
    public void setRipSafe(int ripSafe) {
        this.ripSafe = ripSafe;
    }
    public void setRipTerminal(int ripTerminal) {
        this.ripTerminal = ripTerminal;
    }
}
