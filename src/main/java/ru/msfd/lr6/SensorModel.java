package ru.msfd.lr6;

public class SensorModel
{
    public SensorModel() { }

    public SensorModel(int id, String name, String value)
    {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public SensorModel(int id, String name, String value, boolean showValue)
    {
        this(id, name, value);
        this.showValue = showValue;
    }

    public String name = "";
    public String value = "";
    public int id = -1;
    public boolean showValue = false;

    @Override
    public String toString()
    {
        return showValue ? name + " / " + value : name;
    }

}
