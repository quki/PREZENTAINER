
package com.puregodic.android.prezentainer.decoration;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;

public class CustomItemAnimator extends PendingItemAnimator {
    
    public CustomItemAnimator() {
        setAddDuration(300);
        setRemoveDuration(300);
    }

    @Override
    protected boolean prepHolderForAnimateRemove(ViewHolder holder) {
        return true;
    }

    @Override
    protected ViewPropertyAnimatorCompat animateRemoveImpl(ViewHolder holder) {
        return ViewCompat.animate(holder.itemView).rotationX(90)
                .translationY(-(holder.itemView.getMeasuredHeight() / 2));
    }

    @Override
    protected void onRemoveCanceled(ViewHolder holder) {
        ViewCompat.setRotationX(holder.itemView, 0);
        ViewCompat.setTranslationY(holder.itemView, 0);
    }

    @Override
    protected boolean prepHolderForAnimateAdd(ViewHolder holder) {
        ViewCompat.setRotationX(holder.itemView, 90);
        ViewCompat.setTranslationY(holder.itemView, -(holder.itemView.getMeasuredHeight() / 2));
        return true;
    }

    @Override
    protected ViewPropertyAnimatorCompat animateAddImpl(ViewHolder holder) {
        return ViewCompat.animate(holder.itemView).rotationX(0).translationY(0);
    }

    @Override
    protected void onAddCanceled(ViewHolder holder) {
        ViewCompat.setRotationX(holder.itemView, 0);
        ViewCompat.setTranslationY(holder.itemView, 0);
    }

    @Override
    public boolean animateChange(ViewHolder arg0, ViewHolder arg1, int arg2, int arg3, int arg4,
            int arg5) {
        // TODO Auto-generated method stub
        return false;
    }
}
