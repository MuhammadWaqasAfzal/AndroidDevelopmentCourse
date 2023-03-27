package com.example.myapplication.ui.messages

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alphaSquared.wifapp.common.Constants
import com.example.myapplication.Data
import com.example.myapplication.R
import com.example.myapplication.common.getSpObject

class MessagesAdapter(
    private var messagesList: ArrayList<com.example.myapplication.Data>,
    private var context:Activity,
    private val onMessageClick: (message: Data, position: Int, action: String) -> Unit
) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messagesList[position]
        holder.bind(message, position,context, onMessageClick)
    }

    fun update(Messages: ArrayList<Data>) {
        messagesList = Messages
        this.notifyDataSetChanged()
    }

    fun updateData(messagesData: ArrayList<Data>?) {
        this.messagesList.clear()
        this.messagesList = messagesData!!;
        this.notifyDataSetChanged()

    }

    fun updateSingleCell(user: Data, position: Int) {
        messagesList.set(position,user);
        this.notifyItemChanged(position)

    }


    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.single_item_message_list, parent, false)) {

        var imageView: ImageView? = null
        var tvUserName: TextView? = null
        var tvTextMessage: TextView? = null
        var tvSentOrReceived: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.ivAvatar)
            tvUserName = itemView.findViewById(R.id.tvUserName)
            tvTextMessage = itemView.findViewById(R.id.tvTextMessage)
            tvSentOrReceived = itemView.findViewById(R.id.tvSentOrReceived)

        }

        fun bind(
            message: Data,
            position: Int,
            context: Activity,
            onMessageClick: (message: Data, position: Int, action: String) -> Unit
        ) {
            if(message.SenderId== getSpObject(context)?.getString(Constants.Id,"0")!!)
            {
                tvSentOrReceived!!.text = context.getString(R.string.sent)
                tvUserName!!.text = message.ReceiverName
                if(message.ReceiverGender.toString()=="0")
                    imageView?.setImageDrawable(context.getDrawable(R.drawable.ic_avatar_male))
                else
                    imageView?.setImageDrawable(context.getDrawable(R.drawable.ic_avatar_female))

            }
            else
            {
                tvSentOrReceived!!.text = context.getString(R.string.received)
                tvUserName!!.text = message.SenderName
                if(message.SenderGender.toString()=="0")
                    imageView?.setImageDrawable(context.getDrawable(R.drawable.ic_avatar_male))
                else
                    imageView?.setImageDrawable(context.getDrawable(R.drawable.ic_avatar_female))
            }
            tvTextMessage!!.text = message.TextMessage

        }
    }
}
