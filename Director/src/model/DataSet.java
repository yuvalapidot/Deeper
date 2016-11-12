package model;

public class DataSet {

    private String name;
    private String symbol;
    private int count;

    public DataSet(String name, String symbol, int count) {
        this.name = name;
        this.symbol = symbol;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
