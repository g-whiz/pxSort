package io.pxsort.pxsort.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import io.pxsort.pxsort.R;
import io.pxsort.pxsort.sorting.filter.Filter;
import io.pxsort.pxsort.util.FilterManagerAdapter;

public class FilterManagerActivity extends AbstractSortActivity
        implements FilterManagerAdapter.FilterManagerEventListener {

    private static final String TAG = FilterManagerActivity.class.getSimpleName();
    private static final int SPAN_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_manager);
        initFilterGrid();
    }


    private void initFilterGrid() {
        RecyclerView filterGridView = (RecyclerView) findViewById(R.id.filter_grid);
        if (filterGridView == null) {
            Log.e(TAG, "Unable to initialize UI: R.id.filter_picker_view not found.");
            finish();
            return;
        }

        List<Filter> filters = getFilterDB().getFilters();
        FilterManagerAdapter adapter = new FilterManagerAdapter(
                getSortingContext(), filters, this);
        GridLayoutManager layoutManager;

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(
                    this, SPAN_COUNT, LinearLayoutManager.VERTICAL, false);
        } else {
            layoutManager = new GridLayoutManager(
                    this, SPAN_COUNT, LinearLayoutManager.HORIZONTAL, false);
        }

        filterGridView.setAdapter(adapter);
        filterGridView.setLayoutManager(layoutManager);
    }


    @Override
    public void onEditFilter(Filter filter) {
        Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCloneFilter(Filter filter) {
        Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteFilter(Filter filter) {
        getFilterDB().deleteFilter(filter);
    }

    public void addNewFilter(View view) {
        Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
    }

    /* onClick method */
    public void back(View view) {
        NavUtils.navigateUpFromSameTask(this);
        finish();
    }
}
