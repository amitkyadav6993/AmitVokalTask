package com.amit.amitvokaltask.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by readyassist on 2/24/18.
 */

public abstract class ScrollListener extends RecyclerView.OnScrollListener {

    LinearLayoutManager layoutManager;

    public ScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }

    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

}
