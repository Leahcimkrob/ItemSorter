package me.clcondorcet.itemsorter.data;

/**
 * @author clcondorcet
 */
public class InFilterObject {

    public Filter filter;
    public System sys;
    public int page;

    public InFilterObject (Filter filter, System sys, int page) {
        this.filter = filter;
        this.sys = sys;
        this.page = page;
    }
}
