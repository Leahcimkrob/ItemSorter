package me.clcondorcet.itemsorter.data;

/**
 * @author clcondorcet
 */
public class InSystemObject {

    public System sys;
    public boolean inMainMenu;
    public int page;

    public InSystemObject (System sys, boolean inMainMenu, int page) {
        this.sys = sys;
        this.inMainMenu = inMainMenu;
        this.page = page;
    }

}
