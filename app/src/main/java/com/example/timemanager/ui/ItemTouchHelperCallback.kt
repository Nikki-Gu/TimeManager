/*
 * Copyright 2020 Sergio Belda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.timemanager.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.PaintDrawable
import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanager.R

class ItemTouchHelperCallback(
    val context: Context,
    private val itemTouchHelperAdapter: ItemTouchHelperAdapter
) : ItemTouchHelper.Callback() {
    private var swipeBack = false

    private var clearPaint: Paint = Paint()
    private var background: PaintDrawable = PaintDrawable()
    private var colorBackground = ContextCompat.getColor(context, R.color.red)
    private var deleteDrawable =
        ContextCompat.getDrawable(context, R.drawable.ic_delete_outline_24dp)
    private var intrinsicWidth: Int
    private var intrinsicHeight: Int

    init {
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        intrinsicWidth = deleteDrawable?.intrinsicWidth ?: 0
        intrinsicHeight = deleteDrawable?.intrinsicHeight ?: 0
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        itemTouchHelperAdapter.onItemMove(
            viewHolder.adapterPosition,
            target.adapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (actionState != ACTION_STATE_SWIPE) {
            return
        }
        setTouchListener(recyclerView, viewHolder, dX)

        val itemView = viewHolder.itemView
        val itemHeight = itemView.height

        val isCancelled = dX == 0F && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right.toFloat() + dX.toInt(),
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            return
        }

        background.paint.color = colorBackground
        val cardMargin = context.resources.getDimension(R.dimen.card_margin).toInt()
        val cardCornerRadius = context.resources.getDimension(R.dimen.card_corner_radius)
        background.setBounds(
            itemView.right + dX.toInt() - cardMargin - cardCornerRadius.toInt() * 2,
            itemView.top + cardMargin,
            itemView.right - cardMargin,
            itemView.bottom - cardMargin
        )
        background.setCornerRadius(cardCornerRadius)
        background.draw(c)

        val deleteIconMargin = context.resources.getDimension(R.dimen.delete_button_margin).toInt()
        val deleteIconTop =
            itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight
        deleteDrawable?.setTint(context.getColor(R.color.white))
        deleteDrawable?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteDrawable?.draw(c)
    }

    private fun clearCanvas(
        c: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        c.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack && dX < 0) {
                itemTouchHelperAdapter.onDelete(viewHolder.adapterPosition)
            }
            false
        }
    }

    interface ItemTouchHelperAdapter {
        fun onDelete(position: Int)
        fun onItemMove(fromPosition: Int, toPosition: Int)
    }
}
