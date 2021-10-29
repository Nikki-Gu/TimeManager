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

package com.example.timemanager.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanager.databinding.ItemSheetBinding
import com.example.timemanager.db.model.Sheet

/**
 * [ListAdapter] to show a sheet list.
 */
class SheetsAdapter : ListAdapter<Sheet, SheetsAdapter.SheetViewHolder>(DIFF_CALLBACK) {

    var sheetClickListener: SheetClickListener? = null

    var sheetSelected: Int? = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetViewHolder {
        return SheetViewHolder(
            ItemSheetBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: SheetViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class SheetViewHolder(private val binding: ItemSheetBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sheet: Sheet) {
            binding.sheet = sheet
            binding.sheetItem.isSelected = sheetSelected == sheet.id
            binding.sheetNameTextView.isSelected = sheetSelected == sheet.id
            binding.sheetItem.setOnClickListener {
                sheetClickListener?.onSheetClick(sheet)
            }
            binding.editSheetButton.setOnClickListener {
                sheetClickListener?.onEditSheetClick(sheet)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Sheet>() {
            override fun areItemsTheSame(oldSheet: Sheet, newSheet: Sheet): Boolean {
                return oldSheet.id == newSheet.id
            }

            override fun areContentsTheSame(oldSheet: Sheet, newSheet: Sheet): Boolean {
                return oldSheet == newSheet
            }
        }
    }

    interface SheetClickListener {
        fun onSheetClick(sheet: Sheet)
        fun onEditSheetClick(sheet: Sheet)
    }
}
