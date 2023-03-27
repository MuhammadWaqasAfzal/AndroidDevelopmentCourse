package com.example.myapplication.ui.messages.newMessage

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.login.Data

class AllUsersAdapter(
    private var usersList: ArrayList<Data>,
    private var context:Activity,
    private val onUserClick: (user: Data, position: Int, action: String) -> Unit
) :
    RecyclerView.Adapter<AllUsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersList[position]
        holder.bind(user, position,context, onUserClick)
    }

    fun update(users: ArrayList<Data>) {
        usersList = users
        this.notifyDataSetChanged()
    }

    fun updateData(usersData: ArrayList<Data>?) {
        this.usersList.clear()
        this.usersList = usersData!!;
        this.notifyDataSetChanged()

    }

    fun updateSingleCell(user: Data, position: Int) {
        usersList.set(position,user);
        this.notifyItemChanged(position)

    }


    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.single_item_user_list, parent, false)) {

        var imageView: ImageView? = null
        var ivLike: ImageView? = null
        var tvUserName: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.ivAvatar)

           // ivLike = itemView.findViewById(R.id.ivLike)
            tvUserName = itemView.findViewById(R.id.tvUserName)

        }

        fun bind(
            user: Data,
            position: Int,
            context: Activity,
            onUserClick: (user: Data, position: Int, action: String) -> Unit
        ) {
            tvUserName!!.text = user.FirstName + " " + user.LastName

            if(user.Gender.toString()=="0")
                imageView?.setImageDrawable(context.getDrawable(R.drawable.ic_avatar_male))
            else
                imageView?.setImageDrawable(context.getDrawable(R.drawable.ic_avatar_female))

            itemView.setOnClickListener {
                onUserClick(user,position,"nnull")
            }
        }
    }
}