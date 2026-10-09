
package com.example.groupproject.adapter

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groupproject.data.DatabaseHelper
import com.example.groupproject.databinding.ItemFoodBinding

class FoodAdapter(
    private val context: Context,
    private var cursor: Cursor?,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<FoodAdapter.VH>() {

    inner class VH(val b: ItemFoodBinding) :
        RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemFoodBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = cursor ?: return
        if (!c.moveToPosition(position)) return

        val id = c.getInt(
            c.getColumnIndexOrThrow(DatabaseHelper.COL_ID)
        )
        val name = c.getString(
            c.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)
        )
        val category = c.getString(
            c.getColumnIndexOrThrow(DatabaseHelper.COL_CATEGORY)
        )
        val quantity = c.getInt(
            c.getColumnIndexOrThrow(DatabaseHelper.COL_QUANTITY)
        )
        val unit = c.getString(
            c.getColumnIndexOrThrow(DatabaseHelper.COL_UNIT)
        )
        val area = c.getString(
            c.getColumnIndexOrThrow(DatabaseHelper.COL_PICKUP_AREA)
        )
        val expiry = c.getString(
            c.getColumnIndexOrThrow(DatabaseHelper.COL_EXPIRY_DATE)
        )
        val status = c.getString(
            c.getColumnIndexOrThrow(DatabaseHelper.COL_STATUS)
        )

        with(holder.b) {
            tvName.text = name
            tvCategoryQty.text = "$category • $quantity $unit"
            tvArea.text = "📍 $area"
            tvExpiry.text = "Expires: $expiry"
            tvStatus.text = status

            when (status) {
                "Available" -> {
                    tvStatus.setBackgroundColor(0xFFC8E6C9.toInt())
                    tvStatus.setTextColor(0xFF2E7D32.toInt())
                }

                "Reserved" -> {
                    tvStatus.setBackgroundColor(0xFFFFE0B2.toInt())
                    tvStatus.setTextColor(0xFFE65100.toInt())
                }

                "Collected" -> {
                    tvStatus.setBackgroundColor(0xFFBBDEFB.toInt())
                    tvStatus.setTextColor(0xFF1565C0.toInt())
                }

                "Expired" -> {
                    tvStatus.setBackgroundColor(0xFFFFCDD2.toInt())
                    tvStatus.setTextColor(0xFFC62828.toInt())
                }
            }

            root.setOnClickListener {
                onItemClick(id)
            }
        }
    }

    override fun getItemCount(): Int = cursor?.count ?: 0

    fun swapCursor(newCursor: Cursor?) {
        if (cursor === newCursor) return

        cursor?.close()
        cursor = newCursor
        notifyDataSetChanged()
    }
}
