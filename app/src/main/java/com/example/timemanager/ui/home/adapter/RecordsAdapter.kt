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
import com.example.timemanager.databinding.ItemRecordBinding
import com.example.timemanager.databinding.ItemSheetBinding
import com.example.timemanager.db.model.Record
import com.example.timemanager.db.model.Sheet

/**
 * [ListAdapter] to show a sheet list.
 */
class RecordsAdapter : ListAdapter<Record, RecordsAdapter.RecordViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        return RecordViewHolder(
            ItemRecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    private fun secondToString(seconds: Long): String {
        if (seconds == 0L || seconds == null){
            return "0秒"
        }
        var s = ""
        if (seconds > 3600) {
            s += (seconds/3600).toString() + "时 "
        }
        if (seconds > 60) {
            s += ((seconds-(seconds/3600)*3600)/60).toString() + "分 "
        }
        s += (seconds%60).toString() + "秒"
        return s
    }

    inner class RecordViewHolder(private val binding: ItemRecordBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(record: Record) {
            binding.record = record
            binding.recordName.text=record.taskName.toString()
            binding.recordLength.text= record.duration?.let { secondToString(it) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Record>() {
            override fun areItemsTheSame(oldRecord: Record, newRecord: Record): Boolean {
                return oldRecord.id == newRecord.id
            }

            override fun areContentsTheSame(oldRecord: Record, newRecord: Record): Boolean {
                return oldRecord == newRecord
            }
        }
    }
}
