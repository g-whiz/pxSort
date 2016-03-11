package io.github.pxsort.filter;

import android.content.ContentValues;

/**
 * Created by George on 2016-01-17.
 */
public class Filter {

    private String name;

    private int component;
    private int order;
    private int zipMethod;
    private int baseOp;
    private int numRows;
    private int numCols;
    private boolean isDefault;

    /**
     * todo
     *
     * @param component
     * @param order
     * @param zipMethod
     * @param baseOp
     * @param numRows
     * @param numCols
     */
    public Filter(String name, int component, int order, int zipMethod, int baseOp, int numRows,
                  int numCols, boolean isDefault) {
        this.name = name;
        this.component = component;
        this.order = order;
        this.zipMethod = zipMethod;
        this.baseOp = baseOp;
        this.numRows = numRows;
        this.numCols = numCols;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaseOp() {
        return baseOp;
    }

    public void setBaseOp(int baseOp) {
        this.baseOp = baseOp;
    }

    public int getComponent() {
        return component;
    }

    public void setComponent(int component) {
        this.component = component;
    }

    public int getNumCols(int bitmapWidth) {
        return numCols > bitmapWidth ? bitmapWidth : numCols;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public int getNumRows(int bitmapHeight) {
        return numRows > bitmapHeight ? bitmapHeight : numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getZipMethod() {
        return zipMethod;
    }

    public void setZipMethod(int zipMethod) {
        this.zipMethod = zipMethod;
    }


    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("component", component);
        values.put("order", order);
        values.put("zip_method", zipMethod);
        values.put("base_op", baseOp);
        values.put("num_rows", numRows);
        values.put("num_cols", numCols);

        return values;
    }

    @Override
    public String toString() {
        return name;
    }
}
