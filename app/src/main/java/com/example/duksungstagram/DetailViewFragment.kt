package com.example.duksungstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment: Fragment() {
    companion object{
        var firestore : FirebaseFirestore? =null
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.activity_main,container, false)
        firestore = FirebaseFirestore.getInstance()

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)


        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        init{
            firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail,p0,false)

            return CustomViewHolder(view)
        }

        // Click RecyclerViewItem
        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            init {
                view.setOnClickListener {
                    var myuid: String? = contentDTOs[adapterPosition].explain
                    var myimage: String? = contentDTOs[adapterPosition].imageUrl.toString()
                    var myid: String? = contentDTOs[adapterPosition].userId

                    val intent = Intent(activity, DetailViewPageActivity::class.java)
                    intent.putExtra("myuid",myuid)
                    intent.putExtra("myimage",myimage)
                    intent.putExtra("myid",myid)
                    intent.putExtra("position",adapterPosition.toString())

                    startActivity(intent)
                }
            }
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView

            //userid
            viewholder.detailviewitem_profile_textview.text= contentDTOs[p1].userId

            //image
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl).into(viewholder.detailviewitem_imageview_content)

            //explain
            viewholder.detailviewitem_explain_textview.text = contentDTOs[p1].explain

            //likes
            viewholder.detailviewitem_favoritecounter_textview.text = "" + contentDTOs[p1].favoriteCount
        }
    }
}